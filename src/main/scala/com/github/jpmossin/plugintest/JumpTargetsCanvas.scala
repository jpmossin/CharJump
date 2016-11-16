package com.github.jpmossin.plugintest

import java.awt._
import javax.swing.JComponent

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager

/**
  *
  */
class JumpTargetsCanvas(charsToPosition: Map[Point, Char], editor: Editor) extends JComponent {

  private val colorScheme = EditorColorsManager.getInstance.getGlobalScheme
  private val charFont = new Font(colorScheme.getEditorFontName, Font.BOLD, colorScheme.getEditorFontSize)
  private val fontSize = charFont.getSize
  private val fontMetrics = getFontMetrics(charFont)
  private val lineHeight = fontMetrics.getHeight
  private val charWidth = fontMetrics.charWidth('W')
  setFont(charFont)

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2d = g.asInstanceOf[Graphics2D]

    g2d.setColor(Color.WHITE)
    charsToPosition.foreach({ case (position, jumpChar) =>
      drawChar(g2d, jumpChar, position)
    })
  }

  def drawChar(g2d: Graphics2D, jumpChar: Char, position: Point): Unit = {
    g2d.setColor(Color.WHITE)
    g2d.fillRect(position.x, position.y, charWidth, lineHeight + 1)
    g2d.setColor(Color.RED)
    g2d.drawString(jumpChar.toString, position.x, position.y + fontSize)
  }

}
