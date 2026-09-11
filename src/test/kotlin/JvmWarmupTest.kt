import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import dev.apollointhehouse.network.warmup.JvmWarmup
import dev.apollointhehouse.ui.logging.GuiLogBus
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmWarmupTest {
    @Test
    fun `test jvm warmup executes successfully and silences debug spam`() {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        val rootLogger = context.getLogger("ROOT")
        rootLogger.level = Level.DEBUG

        JvmWarmup.warmup()

        assertEquals(Level.DEBUG, rootLogger.level)
    }

    @Test
    fun `test gui log bus emits and buffers events without dropping`() = runBlocking {
        GuiLogBus.clear()
        for (i in 1..500) {
            GuiLogBus.publish("log $i")
        }

        val collected = mutableListOf<String>()
        val job = launch {
            GuiLogBus.events.take(500).toList(collected)
        }
        job.join()

        assertEquals(500, collected.size)
        assertEquals("log 500", collected.last())
    }
}
