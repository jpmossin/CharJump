package com.github.jpmossin.charjump

import java.awt.event._
import java.awt.{Dimension, Font, Graphics}
import javax.swing.JTextField

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.{Editor, VisualPosition}
import com.intellij.openapi.ui.popup.{ComponentPopupBuilder, JBPopupFactory}
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.popup.AbstractPopup

/**
  * Displays a small popup box for capturing the search input
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
    val popupBuilder: ComponentPopupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(this, this)
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
    val scheme = EditorColorsManager.getInstance().getGlobalScheme
    val editorFont = new Font(scheme.getEditorFontName, Font.BOLD, scheme.getEditorFontSize)
    val width = this.getFontMetrics(editorFont).stringWidth("w")
    val dimension = new Dimension(width * 2, editor.getLineHeight)
    if (SystemInfo.isMac) {
      dimension.setSize(dimension.width * 2, dimension.height * 2)
    }
    dimension
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

