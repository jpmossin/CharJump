package com.github.jpmossin.charjump.config

import javax.swing._

import com.intellij.openapi.options.Configurable

/**
  * This is the class that is registered with IDEA for the CharJump configuration.
  */
class CharJumpConfig extends Configurable {

  private var configUI: CharJumpConfigUI = _
  private val configVars = CharJumpConfigVariables.propertiesComponentBackedConfig

  override def getDisplayName: String = "CharJump"

  override def getHelpTopic: String = null

  override def apply(): Unit = {
    configVars.all.foreach(_.saveCurrentUIValue())
  }

  override def isModified: Boolean = {
    configVars.all.exists(_.isModified)
  }

  override def reset(): Unit = {
    configUI.resetUIToSavedSettings()
  }


  override def createComponent(): JComponent = {
    configUI = new CharJumpConfigUI(configVars)
    configUI.getRootPanel
  }

}
