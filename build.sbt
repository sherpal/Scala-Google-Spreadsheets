name := "ScalaSpreadSheets"

version := "0.1"

scalaVersion := "2.13.0"

val fastCompileRenderer = taskKey[File]("Return main file")
lazy val fastCompileCreateFunctions = taskKey[Unit]("Fast compile, and adds to the compiled file the created functions")
val fullCompileRenderer = taskKey[File]("Return full optimized main file")
lazy val fullCompileCreateFunctions = taskKey[Unit]("Full compile, and adds to the compiled file the created functions")

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

/**
 * Returns a list of stuff to add at the end of compiled file for adding Google functions.
 *
 * The file already compiled so we may assume comments, parenthesis and stuff are balanced.
 */
def exploreFileForFunctions(directory: File): List[String] = {


  // while scanning a file, we first collect all function definitions and all comments found
  sealed trait InfoConstruction
  case class Comments(docs: List[String], startIdx: Int) extends InfoConstruction {
    val endIdx = startIdx + docs.length - 1
  }
  case class FunctionDefinition(functionName: String, arguments: List[String], startIdx: Int) extends InfoConstruction {

    val args: String = arguments.mkString(", ")

    private def commentsToGoogleComments(comments: List[String]): List[String] = {
      if (comments.isEmpty) Nil
      else {
        val lastCommentLine = comments.last

        comments.dropRight(1) ++ (if (lastCommentLine.dropRight(2).trim.isEmpty) {
          List(
            "   * @customfunction",
            lastCommentLine
          )
        } else {
          List(
            lastCommentLine.dropRight(2),
            "   * @customfunction",
            "   */"
          )
        })
      }
    }

    def toGoogleFunction: List[String] = List(
      s"function ${functionName.toUpperCase}($args) { return $functionName($args) }"
    )

    def toGoogleFunction(comments: List[String]): List[String] =
      commentsToGoogleComments(comments) ++ toGoogleFunction


    def toOverloadedGoogleFunction: List[String] = List(
      s"function ${functionName.toUpperCase}() { return $functionName.apply(void 0, arguments) }"
    )

    def toOverloadedGoogleFunction(comments: List[String]): List[String] =
      commentsToGoogleComments(comments) ++ toOverloadedGoogleFunction
  }

  // search for all relevant information for building the Google functions
  @scala.annotation.tailrec
  def constructInformation(
                            lines: List[String],
                            lineIdx: Int,
                            information: List[InfoConstruction]
                          ): List[InfoConstruction] = {
    if (lines.isEmpty) information
    else {
      val line = lines.head
      if (line.contains("/**")) { // beginning of a comment
        val endOfCommentsIdx = lines.indexWhere(_.contains("*/")) // looking where comment ends
        val lastLine = lines(endOfCommentsIdx)
        // splitting last comment line where the comment ends
        val (endOfComment, restOfTheLine) = lastLine.splitAt(lastLine.indexOf("*/") + 2)
        // creating the comment
        val comments = Comments(lines.take(endOfCommentsIdx) :+ endOfComment, lineIdx)
        // continuing with the new comment created.
        constructInformation(
          restOfTheLine +: lines.drop(endOfCommentsIdx + 1), lineIdx + endOfCommentsIdx, information :+ comments
        )
      } else if (line.contains("@JSExportTopLevel(\"")) { // need to add a function
        """(?<=\").+?(?=\")""".r.findFirstIn(line) match {
          case Some(functionName) => // found correct export function name, look for function arguments
            @scala.annotation.tailrec
            def findFunction(lines: List[String], lineNbr: Int, fctInfo: String): (List[String], Int) = {
              """(?<=\().*?(?=\))""".r.findFirstIn(fctInfo) match {
                case Some(args) => // here args is a string of the form "arg1: T1, arg2: T2, arg3: T3"
                  (
                    if (args.trim == "") Nil // no argument here
                    else {
                      args.split(""", ?""")
                        .map(arg => """.+(?=:)""".r.findFirstIn(arg).get)
                        .toList
                        .map(_.trim) // results is a List of the form List(arg1,arg2,arg3)
                    },
                    lineNbr
                  )
                case None => // we did not find all the arguments yet. continue searching with next line
                  findFunction(lines.tail, lineNbr + 1, fctInfo ++ lines.head)
              }
            }

            val annotationRemoved = lines.head.drop(
              """@JSExportTopLevel\(\".+?\"\)""".r.findFirstIn(lines.head).get.length
            )
            val (arguments, lineNbr) = findFunction(annotationRemoved +: lines.tail, 0, "")

            constructInformation(
              lines.drop(lineNbr),
              lineIdx + lineNbr,
              information :+ FunctionDefinition(functionName, arguments, lineIdx)
            )
          case None =>
            println(s"[warning] malformed exported function at line ${lineIdx + 1} in file ${directory.toString}")
            constructInformation(lines.tail, lineIdx + 1, information)
        }

      } else {
        constructInformation(lines.tail, lineIdx + 1, information)
      }
    }
  }


  val exportedFunctionsFile = IO.readLines(directory)

  val information = constructInformation(exportedFunctionsFile, 0, List[InfoConstruction]())


  val (functionDefinitions, commentDefinitions) = information.partition(_.isInstanceOf[FunctionDefinition])

  val functions = functionDefinitions.map(_.asInstanceOf[FunctionDefinition])
  val comments = commentDefinitions.map(_.asInstanceOf[Comments])

  val functionsWithComments = functions
    .map(fct => (fct, comments.find(_.endIdx + 1 == fct.startIdx)))

  val grouped = functionsWithComments.groupBy(_._1.functionName)

  val (overloaded, single) = grouped.partition(_._2.tail.nonEmpty)

  (single.values.flatten.flatMap({
    case (fct, Some(jsDocs)) => fct.toGoogleFunction(jsDocs.docs)
    case (fct, None) => fct.toGoogleFunction
  }) ++ overloaded.values.map(functions => {
    functions.find(_._2.isDefined) match {
      case Some(elem) => elem
      case None => functions.head
    }
  }).flatMap({
    case (fct, Some(jsDocs)) => fct.toOverloadedGoogleFunction(jsDocs.docs)
    case (fct, None) => fct.toOverloadedGoogleFunction
  })).toList
}


def recursiveListFiles(f: File): Array[File] = {
  val (directories, files) = f.listFiles.partition(_.isDirectory)
  files.filter(_.toString.endsWith(".scala")) ++ directories.flatMap(recursiveListFiles)
}

def createGoogleFunctions(compiledFileDirectory: File, baseDirectory: File): Unit = {
  // ignoring files listed in file "google-export-ignore".
  val ignoredFiles = IO.readLines(baseDirectory / "google-export-ignore")
    .map(_.trim)
    .filter(_.nonEmpty)
    .filterNot(_.startsWith("//"))

  if (ignoredFiles.nonEmpty) {
    println(s"[info] Ignoring files: ${ignoredFiles.mkString(", ")}")
  }

  // searching for all files in the src directory
  val functions = recursiveListFiles(baseDirectory / "src/main")
    .filterNot(file => ignoredFiles.exists(file.toString.endsWith))
    .flatMap(exploreFileForFunctions)

  val parentDir = compiledFileDirectory.getParent

  IO.writeLines(
    new java.io.File(parentDir) / "google.js",
    IO.readLines(compiledFileDirectory) ++ functions
  )
}

fastCompileCreateFunctions := {

  createGoogleFunctions(fastCompileRenderer.value, baseDirectory.value)

}

fullCompileCreateFunctions := {

  createGoogleFunctions(fullCompileRenderer.value, baseDirectory.value)

}


lazy val `renderer` = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSLinkerConfig in ThisBuild ~= { _.withESFeatures(_.withUseECMAScript2015(false)) },
    fastCompileRenderer := {
      (fastOptJS in Compile).value.data
    },
    fullCompileRenderer := {
      (fullOptJS in Compile).value.data
    }
  )


