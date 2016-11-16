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
      val nextCharsToShow = positionKeys.mapValues(_.head)
      highlighter.showMatchingPositions(nextCharsToShow)
      keyPressedHandler.setKeyPressedListener(pressedChar => {
        highlighter.clearCurrentHighlighting()
        jumpOrHighlightRemaining(positionKeys, pressedChar)
      })
    }
  }

  def jumpOrHighlightRemaining(positionKeys: Map[Int, Seq[Char]], pressedChar: Char): Unit = {
    val byFirstChar = positionKeys.groupBy({ case (_, jumpChars) => jumpChars.head })
    val forPressedChar = byFirstChar.getOrElse(pressedChar, Map())
    val singleMatchingPosition = forPressedChar.find({ case (pos, chars) => chars.size == 1})
    singleMatchingPosition match {
      case Some((pos, chars)) => jump(pos)
      case _ =>
        val nextJumpChars = forPressedChar.mapValues(_.tail)
        if (nextJumpChars.nonEmpty) highlightMatchingPositions(nextJumpChars)
        else stop()
    }
  }

  def jump(position: Int): Unit = {
    stop()
    editor.getCaretModel.moveToOffset(position)
  }

  def stop(): Unit = {
    keyPressedHandler.removeKeyPressedListener()
    highlighter.clearCurrentHighlighting()
  }

}
