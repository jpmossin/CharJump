package com.github.jpmossin.charjump

import java.awt.event.{KeyAdapter, KeyEvent, KeyListener}

import com.intellij.openapi.editor.Editor


/**
  * The highlevel flow and logic of a single CharJump activation.
  */
class CharJumpRunner(typingCanceller: TypingCanceller, editor: Editor) {

  private val highlighter = new PositionHighlighter(editor)

  private var jumpKeyPressedListener: KeyListener = _

  /**
    * Runs a new CharJump search by going through the following states:
    * 1: Read the search character
    * 2: Map each matching position in the editor's document to a unique sequence of characters
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
    highlighter.clearCurrentHighlighting()
    editor.getContentComponent.removeKeyListener(jumpKeyPressedListener)
    typingCanceller.setActive(false)
  }

  private def onSearchKeyPressed(searchKey: Char): Unit = {
    typingCanceller.setActive(true)
    val positionSearcher = new MatchingPositionSearcher(editor)
    val keysForMatchingPositions = positionSearcher.getKeysForMatchingPositions(searchKey)
    highlightMatchingPositions(keysForMatchingPositions)
  }

  private def highlightMatchingPositions(positionKeys: Map[Int, Seq[Char]]): Unit = {
    if (positionKeys.nonEmpty) {
      val nextCharsToShow = positionKeys.mapValues(_.head)
      highlighter.showMatchingPositions(nextCharsToShow)
      jumpKeyPressedListener = createKeyListenerForNextChar(positionKeys)
      editor.getContentComponent.addKeyListener(jumpKeyPressedListener)
    } else {
      stop()
    }
  }

  private def createKeyListenerForNextChar(positionKeys: Map[Int, Seq[Char]]) = {
    new KeyAdapter {
      override def keyTyped(e: KeyEvent): Unit = {
        editor.getContentComponent.removeKeyListener(this)
        highlighter.clearCurrentHighlighting()
        jumpOrHighlightRemaining(positionKeys, e.getKeyChar)
      }
    }
  }

  private def jumpOrHighlightRemaining(positionKeys: Map[Int, Seq[Char]], pressedChar: Char): Unit = {
    val byFirstChar = positionKeys.groupBy({ case (_, jumpChars) => jumpChars.head })
    val forPressedChar = byFirstChar.getOrElse(pressedChar, Map())
    val singleMatchingPosition = forPressedChar.find({ case (_, chars) => chars.size == 1})
    singleMatchingPosition match {
      case Some((pos, _)) => jump(pos)
      case _ =>
        val remainingJumpChars = forPressedChar.mapValues(_.tail)
        highlightMatchingPositions(remainingJumpChars)
    }
  }

  private def jump(position: Int): Unit = {
    editor.getCaretModel.moveToOffset(position)
    stop()
  }

}
