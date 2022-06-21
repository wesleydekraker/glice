package io.shiftleft.slice.models

import scala.collection.mutable

class BuildContext(val graph: CGraph) {
  private var currentId: Long = Long.MaxValue
  private val variableNameMap: mutable.Map[String, String] = mutable.Map()

  def renameVariable(variableName: String, isDefinition: Boolean = false): String = {
    if (variableNameMap.contains(variableName)) {
      variableNameMap(variableName)
    } else if (isDefinition) {
      val newVariableName = s"variable_${variableNameMap.size}"
      variableNameMap(variableName) = newVariableName
      newVariableName
    } else {
      variableName
    }
  }


    def getNewId: Long = {
    currentId -= 1
    currentId
  }
}
