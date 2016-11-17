package com.github.jpmossin.plugintest

import java.awt._
import javax.swing.JComponent

import com.intellij.openapi.editor.colors.{EditorColorsManager, EditorFontType}
import com.intellij.openapi.editor.impl.EditorImpl

/**
  *
  * note: we actually need an EditorImpl and not an Editor, to get some font info.
  */
class JumpTargetsCanvas(charsToPosition: Map[Point, Char], editor: EditorImpl) extends JComponent {

  private val editorFont = editor.getColorsScheme.getFont(EditorFontType.BOLD)
  private val lineHeight = editor.getLineHeight
  private val charWidth = editor.getFontMetrics(Font.BOLD).charWidth('W')

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2d = g.asInstanceOf[Graphics2D]

    g2d.setFont(editorFont)
    charsToPosition.foreach({ case (position, jumpChar) =>
      drawChar(g2d, jumpChar, position)
    })
  }

  def drawChar(g2d: Graphics2D, jumpChar: Char, position: Point): Unit = {
    g2d.setColor(Color.WHITE)
    g2d.fillRect(position.x, position.y, charWidth, lineHeight + 1)
    g2d.setColor(Color.RED)
    g2d.drawString(jumpChar.toString, position.x, position.y + editorFont.getSize)
  }

}
