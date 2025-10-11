import com.local.guider.utils.mkFirstUppercase
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.io.OutputStream

class ExcelExporter {

    @Throws(IOException::class)
    fun <T> exportToExcel(sheetName: String, objects: List<T>, outputStream: OutputStream?) {
        XSSFWorkbook().use { workbook ->
            val sheet: Sheet = workbook.createSheet(sheetName)

            // Create header row
            val headerRow = sheet.createRow(0)
            val objectClass: Class<*> = objects[0]!!::class.java
            val fields = objectClass.declaredFields
            for (i in fields.indices) {
                val cell = headerRow.createCell(i)
                cell.setCellValue(fields[i].name.mkFirstUppercase()?.replace("_", " "))
                cell.cellStyle.alignment = HorizontalAlignment.CENTER
                cell.cellStyle.verticalAlignment = VerticalAlignment.CENTER
                cell.cellStyle.fillBackgroundColor = IndexedColors.GREY_40_PERCENT.index
            }

            // Create data rows
            var rowNum = 1
            for (obj in objects) {
                val row = sheet.createRow(rowNum++)
                var cellNum = 0
                for (field in fields) {
                    field.isAccessible = true
                    try {
                        val value = field[obj]
                        val cell = row.createCell(cellNum)
                        cell.cellStyle.alignment = HorizontalAlignment.CENTER
                        cell.cellStyle.verticalAlignment = VerticalAlignment.CENTER
                        if (value != null) {
                            cell.setCellValue(value.toString())
                        } else {
                            cell.setCellValue("")
                        }
                        cellNum++
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                }
            }

            // Write the workbook to the OutputStream
            workbook.write(outputStream)
        }
    }

}