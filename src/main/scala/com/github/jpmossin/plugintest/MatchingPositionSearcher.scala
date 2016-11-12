package com.github.jpmossin.plugintest

import com.github.jpmossin.plugintest.MatchingPositionSearcher._
import com.intellij.openapi.editor.{Editor, VisualPosition}

/**
  * Helps with finding the matching positions for a given search character,
  * and mapping each position to a sequence of keys for jumping to the position.
  */
class MatchingPositionSearcher(editor: Editor) {


  def getKeysForMatchingPositions(keyChar: Char, documentChars: CharSequence): Map[Int, Seq[Char]] = {
    val (startOffset, endOffset) = currentVisibleOffsets()
    val keyCharLow = keyChar.toLower
    val matchingPositions = documentChars.subSequence(startOffset, endOffset).toString
      .zipWithIndex
      .filter({ case (chr, position) => chr.toLower == keyCharLow })
      .map({ case (chr, pos) => pos + startOffset })
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

  def main(args: Array[String]): Unit = {
    for (i <- 1 to 100000) {
      pos2Keys(i) match {
        case List('o') => println(i)
        case List('p', 'a') => println(i)
        case List('w', 'w') => println(i)
        case List('x', 'a', 'a') => println(i)
        case _ =>
      }
    }
  }

  // Map each of the matching positions to the sequence of characters
  // that must be typed to jump to the respective position 
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
