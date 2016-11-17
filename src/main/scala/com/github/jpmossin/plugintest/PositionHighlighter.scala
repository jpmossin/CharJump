package com.github.jpmossin.plugintest

import java.awt.Point
import javax.swing.SwingUtilities

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.ui.awt.RelativePoint

/**
  * Helper for highlighting matching positions in the document.
  */
class PositionHighlighter(editor: Editor) {

  var lastShownTargetCanvas: Option[JumpTargetsCanvas] = None

  def showMatchingPositions(matchingPositions: Map[Int, Char]): Unit = {
    ApplicationManager.getApplication.invokeLater(new Runnable {
      override def run(): Unit = {
        val targetPoints = matchingPositions.map({case (pos, char) => (offsetToPoint(pos), char)})
        lastShownTargetCanvas = Some(new JumpTargetsCanvas(targetPoints, editor.asInstanceOf[EditorImpl]))
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
    val viewport = editor.asInstanceOf[EditorEx].getScrollPane.getViewport
    targetCanvas.setBounds(0, 0, viewport.getRootPane.getWidth, viewport.getRootPane.getHeight)
    val rootPane = editor.getComponent.getRootPane
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
