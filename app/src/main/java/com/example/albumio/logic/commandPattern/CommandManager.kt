package com.example.albumio.logic.commandPattern

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommandManager {
    private val undoStack = ArrayDeque<Command>()//TODO:限制大小并加入清理机制
    private val _undoAvailable = MutableStateFlow(false)
    val undoAvailable: StateFlow<Boolean> = _undoAvailable.asStateFlow()
    private val waitingForExecutionQueue = ArrayDeque<BaseLogicRunner>()


    fun isUndoEmpty() {
        _undoAvailable.value = undoStack.isNotEmpty()
    }

    fun addCommand(command: Command) {
        undoStack.addLast(command)
        isUndoEmpty()
        if (command is BaseLogicRunner) {
            waitingForExecutionQueue.addLast(command)
        }
    }

    fun undoLastCommand(): Command {
        val lastCommand = undoStack.removeLast()
        isUndoEmpty()
        if(lastCommand is BaseLogicRunner) {
            waitingForExecutionQueue.removeLast()
        }
        return lastCommand
    }

    fun getAllLogicToExecute() : Iterator<BaseLogicRunner>? {
        return if (waitingForExecutionQueue.isNotEmpty()) {
            waitingForExecutionQueue.iterator()
        } else {
            null
        }
    }


}