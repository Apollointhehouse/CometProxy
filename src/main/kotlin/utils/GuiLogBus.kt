package dev.apollointhehouse.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GuiLogBus {
    private val _events = MutableSharedFlow<String>(replay = 200, extraBufferCapacity = 500)
    val events = _events.asSharedFlow()

    fun publish(line: String) {
        _events.tryEmit(line)
    }
}