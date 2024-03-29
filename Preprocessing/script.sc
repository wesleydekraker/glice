import io.shiftleft.slice.services.GraphService
import io.shiftleft.slice.services.exports.ExportService

@main def exec(sourceFolder: String, outputFolder: String, exportMode: String, depth: String) = {
    importCode(sourceFolder)
    run.ossdataflow

    val exportService = new ExportService(outputFolder)

    val graphService = new GraphService(cpg, exportService, depth.toInt, exportMode.contains("slice"))
    graphService.run()
}
