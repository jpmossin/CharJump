package com.github.jpmossin.charjump.config

/**
  * Represents a configurable value that can be edited in the config UI.
  *
  * All updates to charjump config values are expected to go through
  * a ConfigVariable for that value, so that we can safely cache it.
  *
  * This class is thread safe as reading and writing config variables can
  * probably happen on different threads.
  *
  * @param retriever Fetches the current saved config value from the
  *                  backing config storage
  * @param saver     Saves the provided value as the current value for this
  *                  config variable in the config storage
  * @tparam T The configuration value's type
  */
class ConfigVariable[T](retriever: () => T,
                        saver: T => Unit) {

  private var currentSavedValue: T = retriever.apply()
  private val initialValue: T = currentSavedValue
  private var currentUIValue: T = currentSavedValue


  def getSavedValue: T = synchronized {
    currentSavedValue
  }

  def setSavedValue(newVal: T): Unit = synchronized {
    if (newVal != currentSavedValue) {
      currentSavedValue = newVal
      saver.apply(newVal)
    }
  }

  /**
    * This method should be invoked by the UI when the value
    * is changed by the user. At this point the value may be different
    * from the saved value until the change is applied through saveCurrentUIValue
    */
  def setCurrentUIValue(uiValue: T): Unit = synchronized {
    currentUIValue = uiValue
  }

  def saveCurrentUIValue(): Unit = synchronized {
    setSavedValue(currentUIValue)
  }

  def reset(): Unit = synchronized {
    setSavedValue(initialValue)
  }

  def isModified: Boolean = synchronized {
    currentUIValue != currentSavedValue
  }

}
