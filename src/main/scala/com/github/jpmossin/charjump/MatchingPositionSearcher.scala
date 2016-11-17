package com.github.jpmossin.charjump

import com.github.jpmossin.charjump.MatchingPositionSearcher._
import com.intellij.openapi.editor.{Editor, VisualPosition}

/**
  * Helps with finding the matching positions for a given search character,
  * and mapping each position to a sequence of keys for jumping to the position.
  */
class MatchingPositionSearcher(editor: Editor) {

  /**
    * Find all positions in the currently visible area of the editor
    * matching the given searchChar, and return a map of:
    * (position -> sequence of chars to press for jumping to position)
    */
  def getKeysForMatchingPositions(searchChar: Char): Map[Int, Seq[Char]] = {
    val caretOffset = editor.getCaretModel.getCurrentCaret.getOffset
    val (startOffset, endOffset) = currentVisibleOffsets()
    val keyCharLow = searchChar.toLower
    val matchingPositions = editor.getDocument.getCharsSequence
      .subSequence(startOffset, endOffset).toString
      .zipWithIndex
      .filter({ case (chr, index) => chr.toLower == keyCharLow })
      .map({ case (chr, index) => index + startOffset })
      .sortBy(relativeOffset => Math.abs(relativeOffset - caretOffset))  // Simple heuristic for prefering single-key jumps for the closest positions.
    mapPositionsToJumpKeys(matchingPositions)
  }

  private def currentVisibleOffsets(): (Int, Int) = {
    val visualArea = editor.getScrollingModel.getVisibleArea
    val firstLine = pixelPositionToLogicalLine(visualArea.y) - 1
    val lastLine = pixelPositionToLogicalLine(visualArea.y + visualArea.height)
    (lineStartOffset(firstLine), lineStartOffset(lastLine) - 1)
  }


  private def pixelPositionToLogicalLine(yPixel: Int): Int = {
    val lh = editor.getLineHeight
    val visualLine = (yPixel + lh - 1) / lh
    val logicalLine = editor.visualToLogicalPosition(new VisualPosition(visualLine, 0)).line
    logicalLine
  }

  private def lineStartOffset(line: Int): Int = {
    val totalLines = editor.getDocument.getLineCount - 1
    val normalizedLine = Math.max(0, Math.min(totalLines, line))
    editor.getDocument.getLineStartOffset(normalizedLine)
  }

}

object MatchingPositionSearcher {

  // Map each of the given positions to a unique sequence of characters
  // that must be typed to jump to the respective position.
  private def mapPositionsToJumpKeys(positions: Seq[Int]): Map[Int, Seq[Char]] = {
    var currentIndex = 0
    var jumpKeys = List[List[Char]]()
    for (_ <- positions.indices) {
      if (currentIndex == 15) {
        currentIndex = 416 // when reaching 'p' (14), continue from 'pa' (416) instead
      } else if (currentIndex == 621) {
        currentIndex = 16250 // when reaching 'ww', continue from 'xaa' instead
      }
      jumpKeys = pos2Keys(currentIndex) :: jumpKeys
      currentIndex += 1
    }

    positions.zip(jumpKeys.reverse).toMap
  }


  private def pos2Keys(pos: Int): List[Char] = {
    if (pos < 26) {
      List(('a' + pos).toChar)
    } else {
      pos2Keys((pos / 26) - 1) ::: pos2Keys(pos % 26)
    }
  }

}
