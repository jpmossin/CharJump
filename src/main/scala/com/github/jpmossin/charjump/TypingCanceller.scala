package com.github.jpmossin.charjump

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedActionHandler

/**
  * A TypingCanceller is hooked on to the chain of TypedActionHandlers that are executed
  * on keypress in an editor to suppress changes in the document when CharJump is
  * activated.
  */
class TypingCanceller(nextHandler: TypedActionHandler) extends TypedActionHandler {

  var active = false

  override def execute(editor: Editor, charTyped: Char, dataContext: DataContext): Unit = {
    if (!active) {
      nextHandler.execute(editor, charTyped, dataContext)
    }
  }

  def setActive(active: Boolean): Unit = {
    this.active = active
  }

}
