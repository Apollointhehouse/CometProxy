package dev.apollointhehouse

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import dev.apollointhehouse.network.proxy.ProxyManager
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.network.warmup.JvmWarmup
import dev.apollointhehouse.ui.UI
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
import java.util.Scanner

class AppArgs : CliktCommand() {
    private val logger = logger()

    val debug: Boolean by option(help = "Debug mode").flag()
    val headless: Boolean by option(help = "Run without GUI").flag()
    val targetServer: String by option(help = "Target Server").default("example.com")
    val targetPort: Int by option(help = "Target Port")
        .int()
        .default(25565)
        .check("Port must be in range 1024..65535") { it in 1024..65535 }

    val hostPort: Int by option(help = "Host Port")
        .int()
        .default(25565)
        .check("Port must be in range 1024..65535") { it in 1024..65535 }
    val motd: String by option(help = "Message of the day").default("Proxy Server")

    override fun run()  {
        val isDebugging = ManagementFactory.getRuntimeMXBean()
            .inputArguments
            .toString()
            .contains("-agentlib:jdwp") || debug

        if (isDebugging) {
            val context: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
            context.getLogger("ROOT").setLevel(Level.DEBUG)
        }

        JvmWarmup.warmup()

        val proxyConfig = ProxyConfig(
            targetServer = targetServer,
            targetPort = targetPort,
            hostPort = hostPort,
            motd = motd,
        )

        if (headless) {
            val proxyManager = ProxyManager()

            proxyManager.start(proxyConfig)

            val scanner = Scanner(System.`in`)
            while (true) if (scanner.hasNextLine()) {
                val input = scanner.nextLine().trim().lowercase()

                if (input == "stop") {
                    logger.info("Stopping proxy server...")
                    runBlocking { proxyManager.stop() }
                    return
                }
            }
        }

        UI(proxyConfig)
    }
}