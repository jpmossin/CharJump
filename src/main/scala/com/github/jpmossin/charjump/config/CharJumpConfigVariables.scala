package com.github.jpmossin.charjump.config

import java.awt.Color

import com.intellij.ide.util.PropertiesComponent

/**
  * Defines and exposes all of CharJump's ConfigVariable objects.
  */
trait CharJumpConfigVariables {

  def highlightColor: ConfigVariable[Color]

  def all = List(highlightColor)

}

object CharJumpConfigVariables {

  // A CharJumpConfigVariables using the PropertiesComponent storage.
  // This is the instance to use when running in IDEA.
  val propertiesComponentBackedConfig: CharJumpConfigVariables = new CharJumpConfigVariables {

    override val highlightColor: ConfigVariable[Color] = createHighlightColorConfigVar

    private def createHighlightColorConfigVar: ConfigVariable[Color] = {
      val defaultColor = Color.RED
      val highlightColorKey = "charjump.highlight.color"

      new ConfigVariable[Color](
        retriever = () => {
          val rgb = PropertiesComponent.getInstance().getInt(highlightColorKey, defaultColor.getRGB)
          new Color(rgb)
        },
        saver = color => {
          PropertiesComponent.getInstance().setValue(highlightColorKey, color.getRGB, defaultColor.getRGB)
        }
      )

    }


  }

}
