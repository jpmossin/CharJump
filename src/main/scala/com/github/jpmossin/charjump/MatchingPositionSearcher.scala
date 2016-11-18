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
      .sortBy(relativeOffset => Math.abs(relativeOffset - caretOffset)) // Simple heuristic for prefering single-key jumps for the closest positions.
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

  val azAZ = (('a' to 'z') ++ ('A' to 'Z')).toArray

  // Map each of the given positions to a unique sequence of characters
  // that must be typed to jump to the respective position.
  // The first positions are given a length one sequence, then a length two sequence,
  // and finally a length three sequence if there are many positions.
  // Sequences of different length must have unique prefixes, e.g if there
  // is a length one sequence of just 'a', no length two or length three sequences
  // can start with 'a', since then when the users presses 'a' to jump we would not
  // know whether to jump to the length one sequence or continue with the length 2 sequences.
  private def mapPositionsToJumpKeys(positions: Seq[Int]): Map[Int, Seq[Char]] = {
    var currentIndex = 0
    var jumpKeys = List[List[Char]]()
    for (_ <- positions.indices) {
      if (currentIndex == 37) {
        // when reaching 'L' (37 in azAZ), continue from
        // 'La' (1976) instead to ensure unique prefixes
        currentIndex = 1976
      } else if (currentIndex == 2597) {
        currentIndex = 135252 // 'WW' -> 'Xaa'
      }
      jumpKeys = indexToKeys(currentIndex) :: jumpKeys
      currentIndex += 1
    }

    positions.zip(jumpKeys.reverse).toMap
  }


  // Given the alphabet azAZ, and a sequence a, b, ...z, A, ...Z, aa, ab, ...aZ, ...ZZ, etc,
  // map the given position the corresponding element in this sequence. The sequence
  // is similar to the column headers in a spreadsheet, only that we use a-zA-Z instead
  // of just a-z.
  private def indexToKeys(pos: Int): List[Char] = {
    val numKeys = azAZ.length
    if (pos < numKeys) {
      List(azAZ(pos))
    } else {
      indexToKeys((pos / numKeys) - 1) ::: indexToKeys(pos % numKeys)
    }
  }

}
