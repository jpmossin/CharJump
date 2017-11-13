package com.github.jpmossin.charjump.config

import java.awt.Insets
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JLabel, JPanel}

import com.intellij.ui.ColorPanel
import com.intellij.uiDesigner.core
import com.intellij.uiDesigner.core.{GridConstraints, GridLayoutManager, Spacer}

/**
  * The UI showed in the IDEA settings for configuring CharJump
  */
class CharJumpConfigUI(configVariables: CharJumpConfigVariables) {

  private val uIComponents = createUI()

  def getRootPanel: JPanel = uIComponents.rootPanel

  def resetUIToSavedSettings(): Unit = {
    resetColorChooserToSavedColor()
  }

  private def createUI(): UIComponents = {
    val root = new JPanel
    root.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1))
    val colorChooser = createAndAddColorChooser(root)
    new UIComponents(root, colorChooser)
  }

  /**
    * Creates the UI for selecting the highlight color
    */
  private def createAndAddColorChooser(root: JPanel): ColorPanel = {
    val colorChooser = createColorChooser()
    // some IDEA generated swing code
    root.add(new Spacer, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false))
    root.add(createHighlightColorLabel(), new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false))
    root.add(colorChooser, new core.GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false))
    colorChooser
  }

  private def createColorChooser(): ColorPanel = {
    val colorPanel = new ColorPanel
    val colorConf = configVariables.highlightColor
    colorPanel.setSelectedColor(colorConf.getSavedValue)
    colorPanel.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        colorConf.setCurrentUIValue(colorPanel.getSelectedColor)
      }
    })
    colorPanel
  }

  private def createHighlightColorLabel(): JLabel = {
    val highlightLabel = new JLabel
    highlightLabel.setText("Highlight color")
    highlightLabel.setVerticalAlignment(1)
    highlightLabel.setVerticalTextPosition(1)
    highlightLabel
  }

  private def resetColorChooserToSavedColor(): Unit = {
    val savedColor = configVariables.highlightColor.getSavedValue
    uIComponents.colorChooser.setSelectedColor(savedColor)
  }

  /**
    * Wraps the different ui components we need access to
    */
  private class UIComponents(val rootPanel: JPanel,
                             val colorChooser: ColorPanel)

}
