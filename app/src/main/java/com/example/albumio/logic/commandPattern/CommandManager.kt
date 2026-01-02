package com.example.albumio.logic.commandPattern

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommandManager {
    private val undoStack = ArrayDeque<Command>()//TODO:限制大小并加入清理机制
    private val _undoAvailable = MutableStateFlow(false)
    val undoAvailable: StateFlow<Boolean> = _undoAvailable.asStateFlow()

    fun undoIsEmpty() {
        _undoAvailable.value = undoStack.isNotEmpty()
    }

    fun addCommand(command: Command) {
        undoStack.addLast(command)
        undoIsEmpty()
    }

    fun undoLastCommand(): Command {
        val lastCommand = undoStack.removeLast()
        undoIsEmpty()
        return lastCommand
    }
}