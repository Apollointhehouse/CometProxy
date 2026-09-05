package dev.apollointhehouse.utils

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.classic.PatternLayout
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GuiLogBus {
    private val _events = MutableSharedFlow<String>(replay = 200, extraBufferCapacity = 500)
    val events = _events.asSharedFlow()

    fun publish(line: String) {
        _events.tryEmit(line)
    }
}

class GuiAppender : AppenderBase<ILoggingEvent>() {
    private lateinit var layout: PatternLayout

    override fun start() {
        layout = PatternLayout().apply {
            context = this@GuiAppender.context
            pattern = "%d{HH:mm:ss} [%thread] %level %logger{0} - %msg%n"
            start()
        }
        super.start()
    }

    override fun append(event: ILoggingEvent) {
        val formatted = layout.doLayout(event).trimEnd('\n')
        GuiLogBus.publish(formatted)
    }

    override fun stop() {
        layout.stop()
        super.stop()
    }
}