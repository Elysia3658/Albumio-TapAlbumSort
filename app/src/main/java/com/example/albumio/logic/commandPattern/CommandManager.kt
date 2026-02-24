package com.example.albumio.logic.commandPattern

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommandManager {
    private val undoStack = ArrayDeque<Command>()//TODO:限制大小并加入清理机制
    private val waitingForExecutionQueue = ArrayDeque<BaseLogicRunner>()

    private val _isUndoAvailable = MutableStateFlow(false)
    val isUndoAvailable: StateFlow<Boolean> = _isUndoAvailable.asStateFlow()
    private val _isConfirmAvailable = MutableStateFlow(false)
    val isConfirmAvailable: StateFlow<Boolean> = _isConfirmAvailable.asStateFlow()


    fun isListEmpty() {
        _isUndoAvailable.value = undoStack.isNotEmpty()
        _isConfirmAvailable.value = waitingForExecutionQueue.isNotEmpty()
    }

    fun addCommand(command: Command) {
        undoStack.addLast(command)
        if (command is BaseLogicRunner) {
            waitingForExecutionQueue.addLast(command)

        }

        isListEmpty()
    }

    fun undoLastCommand(): Command {
        val lastCommand = undoStack.removeLast()
        if(lastCommand is BaseLogicRunner) {
            waitingForExecutionQueue.removeLast()
        }

        isListEmpty()

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