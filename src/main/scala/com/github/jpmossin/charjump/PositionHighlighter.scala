package com.github.jpmossin.charjump

import java.awt.Point
import javax.swing.SwingUtilities

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.ui.awt.RelativePoint

/**
  * Helper for highlighting matching positions in the document.
  */
class PositionHighlighter(editor: Editor) {

  var lastShownTargetCanvas: Option[JumpTargetsCanvas] = None

  /**
    * Create and show a JumpTargetsCanvas for displaying the given
    * positions graphically in the editor.
    */
  def showMatchingPositions(matchingPositions: Map[Int, Char]): Unit = {
    clearCurrentHighlighting()
    ApplicationManager.getApplication.invokeLater(new Runnable {
      override def run(): Unit = {
        val targetPoints = matchingPositions.map({case (pos, char) => (offsetToPoint(pos), char)})
        lastShownTargetCanvas = Some(new JumpTargetsCanvas(targetPoints.toSeq, editor.asInstanceOf[EditorImpl]))
        addTargetCanvas(lastShownTargetCanvas.get)
      }
    })
  }

  def clearCurrentHighlighting(): Unit = {
    lastShownTargetCanvas.foreach(removeTargetCanvas)
    lastShownTargetCanvas = None
  }

  private def offsetToPoint(offset: Int): Point = {
    val visualPosition = editor.offsetToVisualPosition(offset)
    val relativePoint = new RelativePoint(editor.getContentComponent, editor.visualPositionToXY(visualPosition))
    relativePoint.getOriginalPoint
  }

  private def addTargetCanvas(targetCanvas: JumpTargetsCanvas): Unit = {
    val contentComponent = editor.getContentComponent
    contentComponent.add(targetCanvas)
    val rootPane = editor.getComponent.getRootPane
    targetCanvas.setBounds(0, 0, rootPane.getWidth, rootPane.getHeight)
    val locationOnScreen = SwingUtilities.convertPoint(targetCanvas, targetCanvas.getLocation(), rootPane)
    targetCanvas.setLocation(-locationOnScreen.x, -locationOnScreen.y)
    contentComponent.repaint()
  }

  private def removeTargetCanvas(targetCanvas: JumpTargetsCanvas): Unit = {
    val contentComponent = editor.getContentComponent
    contentComponent.remove(targetCanvas)
    contentComponent.repaint()
  }

}
