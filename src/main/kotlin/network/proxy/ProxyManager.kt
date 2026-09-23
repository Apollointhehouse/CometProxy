package dev.apollointhehouse.network.proxy

import dev.apollointhehouse.network.proxy.config.ProxyConfig
import kotlinx.coroutines.*
import org.apache.logging.log4j.kotlin.logger

class ProxyManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var proxyJob: Job? = null
    private val log = logger()

    fun start(proxyConfig: ProxyConfig) {
        if (proxyJob?.isActive == true) return
        proxyJob = scope.launch {
            try {
                val proxy = Proxy(proxyConfig)

                proxy.start()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.error(e) { "Proxy closed" }
            }
        }
    }

    fun stop() {
        proxyJob?.cancel()
        proxyJob = null
    }
}
