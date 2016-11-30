package com.github.jpmossin.charjump

import com.github.jpmossin.charjump.CharJumpAction._
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.actionSystem.EditorActionManager

class CharJumpAction extends AnAction("CharJump") {

  private var currentCharJump: Option[CharJumpRunner] = None

  /**
    * The entry point for when the user activates CharJump.
    */
  override def actionPerformed(event: AnActionEvent): Unit = {
    currentCharJump.foreach(_.stop())
    val editor = event.getData(CommonDataKeys.EDITOR)
    if (editor != null) {
      val charJumpRunner = new CharJumpRunner(typingCanceller, editor)
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
