package io.shiftleft.slice.services

import scala.collection.mutable

class CodeWriter {
  private val stringBuilder: mutable.StringBuilder = new mutable.StringBuilder()
  private var indent = 0

  def addIndent(): Unit = {
    indent += 1
  }

  def removeIndent(): Unit = {
    indent -= 1
  }

  def append(line: String, isStatement: Boolean = false): Unit = {
    append(isStatement)
    stringBuilder.append(line)
  }

  def append(isStatement: Boolean): Unit = {
    if (isStatement)
      stringBuilder.append("  " * indent)
  }

  override def toString: String = {
    stringBuilder.toString()
  }
}
