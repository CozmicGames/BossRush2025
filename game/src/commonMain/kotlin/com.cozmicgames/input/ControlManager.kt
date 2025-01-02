package com.cozmicgames.input

class ControlManager {
    var activeGamepad = 0

    private val actionsInternal = arrayListOf<ControlAction>()

    val actions get() = actionsInternal as List<ControlAction>

    fun add(name: String): ControlAction {
        val action = ControlAction(name)
        actionsInternal += action
        return action
    }

    fun find(name: String): ControlAction? {
        return actionsInternal.find { it.name == name }
    }

    fun remove(name: String): Boolean {
        return actionsInternal.removeAll { it.name == name }
    }

    fun clear() {
        actionsInternal.clear()
    }

    fun isEnabled(name: String): Boolean {
        return find(name)?.isEnabled == true
    }

    fun setEnabled(name: String, enabled: Boolean) {
        find(name)?.isEnabled = enabled
    }

    fun update(delta: Float) {
        actionsInternal.forEach {
            it.update(delta)
        }
    }
}