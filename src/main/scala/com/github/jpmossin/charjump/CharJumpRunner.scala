package com.github.jpmossin.charjump

import com.intellij.openapi.editor.Editor


/**
  * The highlevel flow and logic of a single CharJump activation.
  */
class CharJumpRunner(keyPressedHandler: JumpKeyPressedHandler, editor: Editor) {

  private val highlighter = new PositionHighlighter(editor)

  /**
    * Runs a new CharJump search by going through the following states:
    * 1: Read the search character
    * 2: Map each matching position to a sequence of characters
    * 3: Read the next character typed by the user and filter the matching positions
    * 4: - If only one remaining position: Jump!
    *    - If no remaining positions, Stop!
    *    - Else goto 3.
    */
  def runCharJump(): Unit = {
    val searchBox = new SearchBox(editor, onSearchKeyPressed)
    searchBox.setupAndShow()
  }

  def stop(): Unit = {
    keyPressedHandler.removeKeyPressedListener()
    highlighter.clearCurrentHighlighting()
  }

  private def onSearchKeyPressed(searchKey: Char): Unit = {
    val positionSearcher = new MatchingPositionSearcher(editor)
    val keysForMatchingPositions = positionSearcher.getKeysForMatchingPositions(searchKey)
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
    } else {
      stop()
    }
  }

  private def jumpOrHighlightRemaining(positionKeys: Map[Int, Seq[Char]], pressedChar: Char): Unit = {
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

  private def jump(position: Int): Unit = {
    stop()
    editor.getCaretModel.moveToOffset(position)
  }

}
