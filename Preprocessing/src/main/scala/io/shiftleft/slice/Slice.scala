package io.shiftleft.slice

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.semanticcpg.layers.{LayerCreator, LayerCreatorContext, LayerCreatorOptions}
import io.shiftleft.slice.services.GraphService
import io.shiftleft.slice.services.exports.ExportService

object Slice {

  /**
    * This is the extensions official name as will be shown in the table
    * one obtains when running `run` on the Ocular shell.
    * */
  val overlayName = "Slice Extension"

  /**
    * A short description to be shown in the table obtained when
    * running `run` on the Ocular shell.
    * */
  val description = "An extension that can perform inter-procedural program slicing."

  /**
    * Option object initialize to defaults. This object will be made
    * accessible to the user via `opts.slice`.
    * */
  def defaultOpts: SliceOpts = SliceOpts(outFolder = "graphs", mode = "method")
}

/**
  * Options can be passed to the extension via a custom options
  * class that derives from `LayerCreatorOptions`. In our example,
  * we use the option class below to pass the slicing criterion
  * from the user to the extension.
  * */
case class SliceOpts(var outFolder: String, var mode: String)
  extends LayerCreatorOptions {}

class Slice(options: SliceOpts) extends LayerCreator {
  override val overlayName: String = Slice.overlayName
  override val description: String = Slice.description

  /**
    * This method is executed when the user issues the command `run.slice`.
    * */
  override def create(context: LayerCreatorContext, storeUndoInfo: Boolean): Unit = {
    val cpg = context.cpg
    run(cpg)
  }

  def run(cpg: Cpg): Unit = {
    val exportService = new ExportService(options.outFolder)

    val graphService = new GraphService(cpg, exportService, options.mode)
    graphService.run()
  }
}
