package dev.apollointhehouse.utils

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GuiLogBus {
    private val _events = MutableSharedFlow<String>(
        replay = 1000,
        extraBufferCapacity = 1000,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun publish(line: String) {
        _events.tryEmit(line)
    }
}