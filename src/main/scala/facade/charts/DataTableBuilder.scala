package facade.charts

import scala.scalajs.js

@js.native
trait DataTableBuilder extends js.Object {

  def addColumn(columnType: ColumnType, label: String): DataTableBuilder = js.native

  def addRow(values: js.Array[Any]): DataTableBuilder = js.native

}
