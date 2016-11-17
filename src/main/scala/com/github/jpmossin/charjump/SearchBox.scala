package com.github.jpmossin.charjump

import java.awt.event._
import java.awt.{Dimension, Graphics}
import javax.swing.JTextField

import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.{Editor, VisualPosition}
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint

/**
  * Displays a small popup box for capturing the search input.
  * Once an input char is captured, the search box will close itself
  * and call the registered listener.
  */
class SearchBox(editor: Editor, keyPressedHandler: Char => Unit) extends JTextField {

  private val popup = createPopup()

  def setupAndShow(): Unit = {
    setupKeyListener()
    popup.show(guessBestLocation())
    popup.setRequestFocus(true)
    requestFocus()
  }

  private def createPopup() = {
    val popupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(this, this)
    val popup = popupBuilder.createPopup()
    popup.setSize(computeDimensions())
    popup
  }

  private def guessBestLocation(): RelativePoint = {
    val logicalPosition = editor.getCaretModel.getVisualPosition
    getPointFromVisualPosition(logicalPosition)
  }

  private def getPointFromVisualPosition(logicalPosition: VisualPosition): RelativePoint = {
    val p = editor.visualPositionToXY(new VisualPosition(logicalPosition.line, logicalPosition.column))
    new RelativePoint(editor.getContentComponent, p)
  }

  private def computeDimensions() = {
    val editorFont = editor.getColorsScheme.getFont(EditorFontType.PLAIN)
    val width = getFontMetrics(editorFont).stringWidth("W")
    new Dimension(width + 1, editorFont.getSize + 1)
  }

  private def setupKeyListener(): Unit = {
    addKeyListener(new KeyAdapter {
      override def keyTyped(e: KeyEvent): Unit = {
        closePopup()
        keyPressedHandler(e.getKeyChar)
      }
    })
  }

  private def closePopup(): Unit = {
    popup.cancel()
    popup.dispose()
  }

  override def paintBorder(g: Graphics): Unit = {}

}

