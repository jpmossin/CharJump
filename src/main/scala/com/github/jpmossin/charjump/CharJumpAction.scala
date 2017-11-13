package com.github.jpmossin.charjump

import com.github.jpmossin.charjump.CharJumpAction._
import com.github.jpmossin.charjump.config.CharJumpConfigVariables
import com.intellij.openapi.actionSystem.{AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.project.DumbAwareAction

class CharJumpAction extends DumbAwareAction("CharJump") {

  private var currentCharJump: Option[CharJumpRunner] = None
  private val configVariables = CharJumpConfigVariables.propertiesComponentBackedConfig

  /**
    * The entry point for when the user activates CharJump.
    */
  override def actionPerformed(event: AnActionEvent): Unit = {
    currentCharJump.foreach(_.stop())
    val editor = event.getData(CommonDataKeys.EDITOR)
    if (editor != null) {
      val charJumpRunner = new CharJumpRunner(typingCanceller, editor, configVariables)
      charJumpRunner.runCharJump()
      currentCharJump = Some(charJumpRunner)
    }
  }
}

object CharJumpAction {

  private val typedAction = EditorActionManager.getInstance.getTypedAction
  private val typingCanceller = new TypingCanceller(typedAction.getHandler)
  typedAction.setupHandler(typingCanceller)

}
