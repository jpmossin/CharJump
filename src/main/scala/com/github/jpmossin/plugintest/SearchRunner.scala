package com.github.jpmossin.plugintest

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class SearchRunner(keyPressedHandler: JumpKeyPressedHandler, project: Project, editor: Editor) {

  private val highlighter = new PositionHighlighter(project, editor)

  def runSearch(): Unit = {
    val searchBox = new SearchBox(editor, onSearchKeyPressed)
    searchBox.setupAndShow()
  }

  private def onSearchKeyPressed(searchKey: Char): Unit = {
    val documentChars = editor.getDocument.getCharsSequence
    val positionSearcher = new MatchingPositionSearcher(editor)
    val keysForMatchingPositions = positionSearcher.getKeysForMatchingPositions(searchKey, documentChars)
    highlightMatchingPositions(keysForMatchingPositions)
  }

  private def highlightMatchingPositions(positionKeys: Map[Int, Seq[Char]]): Unit = {
    if (positionKeys.nonEmpty) {
      val highlightState = highlighter.showMatchingPositions(positionKeys)
      keyPressedHandler.setKeyPressedListener(pressedChar => {
        highlighter.resetPrevious(highlightState)
        jumpOrHighlightRemaining(positionKeys, pressedChar)
      })
    }
  }

  def jumpOrHighlightRemaining(positionKeys: Map[Int, Seq[Char]], pressedChar: Char): Unit = {
    val byFirstChar = positionKeys.groupBy({ case (_, jumpChars) => jumpChars.head })
    val forPressedChar = byFirstChar.getOrElse(pressedChar, Map())
    val singleMatchingPosition = forPressedChar.find({ case (pos, chars) => chars.size == 1})
    singleMatchingPosition match {
      case Some((pos, chars)) => finishJump(pos)
      case _ =>
        val nextJumpChars = forPressedChar.mapValues(_.tail)
        highlightMatchingPositions(nextJumpChars)
    }
  }

  def finishJump(position: Int): Unit = {
    keyPressedHandler.removeKeyPressedListener()
    editor.getCaretModel.moveToOffset(position)
  }

}