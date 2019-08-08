# Scala SpreadSheets

This project allows you to create [Google custom functions](https://developers.google.com/apps-script/guides/sheets/functions) in [Google SpreadSheets](https://www.google.com/intl/en_UK/sheets/about/) with Scala.

It simply uses [Scala.js](https://www.scala-js.org/) for translating your Scala code into JavaScript, and then parse your source files to declare all the exported functions [the way Google wants it](https://developers.google.com/apps-script/guides/sheets/functions#using_a_custom_function).

## How to use it

### Crash course

- Clone this repository
- [Export your functions](https://www.scala-js.org/doc/interoperability/export-to-javascript.html#exporting-top-level-methods) into the JavaScript global scope using the `@JSExportTopLevel("FCTNAME")` annotation. Function name need not be upper case, but it will be transformed into upper case for Google function.
- Use the `fastCompileCreateFunctions` (or `fullCompileCreateFunctions` for full optimization) sbt task.
- Copy-paste the google.js file into [Spread Sheet Script Editor](https://developers.google.com/apps-script/guides/sheets/functions#creating_a_custom_function) and save it.
- You can now use your functions within Google Spreadsheet.

### Examples

The functions defined in the [Getting Started](https://developers.google.com/apps-script/guides/sheets/functions) page for custom functions have been translated into Scala within the `tutorial` package, in the `GoogleGettingStarted.scala` file.

Other examples are presented in the `ExportedFunctions.scala` file.

### Docs

#### Exporting a function

In order to export a method of an object as Google custom function, you must add the annotation `@JSExportTopLevel` before the function definition. The argument of the annotation must be of the form `FCTNAME` for a method you would like to be exported as `FCTNAME` in the spreadsheets.

It is not necessary for the function name to be upper case. However, following [Google recommendations](https://developers.google.com/apps-script/guides/sheets/functions#naming), the function will be upper case in the compiled `google.js` file.

#### Data types

Contents in Google spreadsheet are converted to one of the four following types:

- String,
- Double,
- Boolean, and
- [js.Date](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date).

The project contains a data type for that, simply called `Data` in the `Cell` object of the `cells` package.

Arguments passed through exported functions from the spreadsheets can therefore be either `Data` or `js.Array[js.Array[Data]]`. In the latter, first rows are indexed, then columns, like the usual Matrix index notation (except of course indices start at 0).

The project contains a case class Cell, which is a wrapper for the Data type. The Cell class contains facility methods for dealing with the data. In Scala, it is probably more usual to work with `Vector[Vector[Cell]]` than to work with `js.Array[js.Array[Data]]`, and there is therefore and implicit conversion from the latter to the former. In order to go back to the expected returned type `js.Array[js.Array[Data]]`, there is an implicit method `toGoogleCells` on `Vector[Vector[Cell]]`.

#### Ignoring files

When exporting your functions to Google custom functions, the task will scan all of your source files. Perhaps sometimes you don't want to export all the functions, and you want to exclude functions from a particular file.

You can do that by adding the file name to the `google-export-ignore` file. One file per line. Lines starting with `//` are ignored.

This can for example be used to ignore the functions in the tutorial package, instead of just deleting it.

#### Making jsDocs for your functions

In order to have [auto-completion](https://developers.google.com/apps-script/guides/sheets/functions#autocomplete) of your custom methods when using them in a Spreadsheet, you must provide the relevant documentation.

In particular the methods documentation must end by the `@customfunction` tag. This is actually done automatically for you.

Thus, if you want your functions to be documented within Google Spreadsheets, as well as having auto-completion, your simply need to make the Scala documentation of your methods the usual way, and the sbt task will take care of it for you.


Important restriction: the documentation must

- begin by `/**`
- end with `*/`
- should not contain `*/`
- have its last line (the one containing the closing `*/`) must be the line above the line you have the `@JSExportTopLevel` annotation. 

#### Overloaded methods

You may make overloaded methods. All of them will be exported, but the documentation of only one of them will be copied (in a not-really deterministic way). So be sure to document only one, or all of them in the same way.

#### Using the Google services

Custom functions allows you to use certain functions from the [Google Services](https://developers.google.com/apps-script/guides/services/). In order to use them within Scala, you have to create the corresponding [facade types](https://www.scala-js.org/doc/interoperability/facade-types.html).

The facade package contains a few of these facade types, necessary for the getting started guide.



