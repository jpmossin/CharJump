package com.github.jpmossin.plugintest

import java.awt.{Color, Font}

import com.github.jpmossin.plugintest.PositionHighlighter._
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.{Document, Editor}
import com.intellij.openapi.editor.markup.{HighlighterLayer, HighlighterTargetArea, RangeHighlighter, TextAttributes}
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable

/**
  * Helper for highlighting matching positions in the document. 
  */
class PositionHighlighter(project: Project, editor: Editor) {

  def showMatchingPositions(matchingPositions: Map[Int, Seq[Char]]): HighlightState = {
    WriteCommandAction.runWriteCommandAction(project, new Computable[HighlightState] {
      override def compute(): HighlightState = {
        val document: Document = editor.getDocument
        val originalChars = matchingPositions.keys.map(p => (p, document.getCharsSequence.charAt(p))).toMap
        val rangeHighlighters = highlightPositions(matchingPositions)
        HighlightState(rangeHighlighters, originalChars)
      }
    })
  }

  def resetPrevious(previous: HighlightState): Unit = {
    previous.rangeHighlighters.foreach(_.dispose())
    previous.replacedChars.foreach({ case (pos, chr) =>
      editor.getDocument.replaceString(pos, pos + 1, chr + "")
    })
  }

  private def highlightPositions(matchingPositions: Map[Int, Seq[Char]]): Seq[RangeHighlighter] = {
    matchingPositions.map({ case (pos, chars) =>
      highlightPosition(pos, chars.head)
    }).toSeq
  }

  private def highlightPosition(pos: Int, jumpChar: Char) = {
    editor.getDocument.replaceString(pos, pos + 1, jumpChar.toString.toUpperCase)
    editor.getMarkupModel.addRangeHighlighter(pos, pos + 1,
      HighlighterLayer.LAST, highlightAttributes, HighlighterTargetArea.EXACT_RANGE)
  }

}


object PositionHighlighter {

  private val highlightAttributes = new TextAttributes(Color.BLACK, Color.LIGHT_GRAY, null, null, Font.BOLD)

  case class HighlightState(rangeHighlighters: Seq[RangeHighlighter], replacedChars: Map[Int, Char])

}