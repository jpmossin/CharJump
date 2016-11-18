package com.github.jpmossin.charjump

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedActionHandler

/**
  * This class can be hooked on to the chain of TypedActionHandlers that are executed
  * on keypress in an editor. It will either forward the typed key to
  * a registered listener, or pass on the event to the given next handler in the chain.
  */
class JumpKeyPressedHandler(nextHandler: TypedActionHandler) extends TypedActionHandler {

  type KeyPressedListener = Char => Unit

  private var keyPressedListener: Option[KeyPressedListener] = None

  override def execute(editor: Editor, charTyped: Char, dataContext: DataContext): Unit = {
    keyPressedListener match {
      case Some(listener) => listener(charTyped)
      case None => nextHandler.execute(editor, charTyped, dataContext)
    }
  }

  def setKeyPressedListener(keyPressedListener: KeyPressedListener): Unit = {
    this.keyPressedListener = Some(keyPressedListener)
  }

  def removeKeyPressedListener(): Unit = {
    keyPressedListener = None
  }

}
