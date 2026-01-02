package com.example.albumio.logic.commandPattern

import android.content.Context


interface UiMutator<T> {
    fun uiExecute(oldState: T): T
    fun uiUndo(): T
}

interface UiRecordByUser<T> {
    fun uiRecord(oldState: T): T
    fun uiUndoRecord(): T
}

interface LogicRunner {
    fun logicExecute(context: Context)
    fun logicUndo()
}