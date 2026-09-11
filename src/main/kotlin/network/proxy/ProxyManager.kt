package dev.apollointhehouse.network.proxy

import dev.apollointhehouse.network.proxy.config.ProxyConfig
import kotlinx.coroutines.*
import org.apache.logging.log4j.kotlin.logger

class ProxyManager {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var proxyJob: Job? = null
    private val log = logger()

    fun start(host: String, port: Int) {
        if (proxyJob?.isActive == true) return
        proxyJob = scope.launch {
            try {
                val proxy = Proxy(ProxyConfig(targetServer = host, targetPort = port, motd = "Proxy Server"))

                proxy.start()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.error(e) { "Error starting proxy" }
            }
        }
    }

    suspend fun stop() {
        proxyJob?.cancelAndJoin()
        proxyJob = null
    }
}
