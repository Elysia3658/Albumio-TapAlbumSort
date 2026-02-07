package com.example.albumio.logic.commandPattern

import android.content.ContentResolver


interface UiMutator<T> {
    fun uiExecute(oldState: T): T
    fun uiUndo(): T
}

interface UiRecordByUser<T> {
    fun uiRecord(oldState: T): T
    fun uiUndoRecord(): T
}

interface BaseLogicRunner {
    fun logicExecute(resolver: ContentResolver)
}

interface UndoLogicRunner : BaseLogicRunner {
    fun logicUndo(resolver: ContentResolver)
}
