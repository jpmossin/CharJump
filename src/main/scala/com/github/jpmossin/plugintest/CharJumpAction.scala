package com.github.jpmossin.plugintest

import com.github.jpmossin.plugintest.CharJumpAction._
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.actionSystem.EditorActionManager

class CharJumpAction extends AnAction("CharJump") {

  private var lastSearch: Option[SearchRunner] = None

  override def actionPerformed(event: AnActionEvent): Unit = {
    lastSearch.foreach(_.stop())
    val editor = event.getData(CommonDataKeys.EDITOR)
    val searchRunner = new SearchRunner(keyPressedHandler, editor)
    searchRunner.runSearch()
    lastSearch = Some(searchRunner)
  }

}

object CharJumpAction {

  private val typedAction = EditorActionManager.getInstance.getTypedAction
  private val keyPressedHandler = new JumpKeyPressedHandler(typedAction.getHandler)
  typedAction.setupHandler(keyPressedHandler)

}
