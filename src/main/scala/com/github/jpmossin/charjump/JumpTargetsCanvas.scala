package com.github.jpmossin.charjump

import java.awt._
import javax.swing.JComponent

import com.github.jpmossin.charjump.config.ConfigVariable
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.impl.EditorImpl

/**
  * A JComponent that draws a character label for each of
  * the given (Point, Char) pairs. It uses the current editor font settings to try
  * to draw the labels in such a way that they appear to replace the actual characters.
  *
  * (note: we actually need an EditorImpl and not an Editor, to get some font info.)
  */
class JumpTargetsCanvas(charsToPosition: Seq[(Point, Char)],
                        editor: EditorImpl,
                        highlightColor: ConfigVariable[Color]) extends JComponent {

  private val editorFont = editor.getColorsScheme.getFont(EditorFontType.BOLD)
  private val lineHeight = editor.getLineHeight
  private val charWidth = editor.getFontMetrics(Font.BOLD).charWidth('W')

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2d = g.asInstanceOf[Graphics2D]

    g2d.setFont(editorFont)
    val color = highlightColor.getSavedValue
    charsToPosition.foreach({ case (position, jumpChar) =>
      drawChar(g2d, jumpChar, position, color)
    })
  }

  def drawChar(g2d: Graphics2D, jumpChar: Char, position: Point, highlightColor: Color): Unit = {
    g2d.setColor(editor.getBackgroundColor)
    g2d.fillRect(position.x, position.y, charWidth, lineHeight + 1)
    g2d.setColor(highlightColor)
    g2d.drawString(jumpChar.toString, position.x, position.y + editorFont.getSize)
  }

}
