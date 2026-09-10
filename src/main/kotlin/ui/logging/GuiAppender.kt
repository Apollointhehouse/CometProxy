package dev.apollointhehouse.ui.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.classic.PatternLayout

class GuiAppender : AppenderBase<ILoggingEvent>() {
    private lateinit var layout: PatternLayout

    override fun start() {
        layout = PatternLayout().apply {
            context = this@GuiAppender.context
            pattern = "%d{HH:mm:ss} [%level] %logger{0} - %msg%n"
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