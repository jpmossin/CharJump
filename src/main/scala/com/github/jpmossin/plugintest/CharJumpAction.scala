package com.github.jpmossin.plugintest

import com.github.jpmossin.plugintest.CharJumpAction._
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.actionSystem.EditorActionManager

class CharJumpAction extends AnAction("Scala!") {
  
  override def actionPerformed(event: AnActionEvent): Unit = {
    val project = event.getData(CommonDataKeys.PROJECT)
    val editor = event.getData(CommonDataKeys.EDITOR)
    new SearchRunner(keyPressedHandler, project, editor).runSearch()
  }
  
}

object CharJumpAction {

  private val typedAction = EditorActionManager.getInstance.getTypedAction
  private val keyPressedHandler = new JumpKeyPressedHandler(typedAction.getHandler)
  typedAction.setupHandler(keyPressedHandler)

}
