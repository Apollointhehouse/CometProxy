package dev.apollointhehouse.ui.repo

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.ImageBitmap
import dev.apollointhehouse.network.API
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger
import kotlin.uuid.Uuid

class PlayerHeadRepository(private val scope: CoroutineScope) {
    private val log = logger()
    private val loadingUuids = mutableSetOf<Uuid>()
    val playerHeads = mutableStateMapOf<Uuid, ImageBitmap>()

    fun loadPlayerHead(uuid: Uuid) {
        if (loadingUuids.add(uuid)) {
            scope.launch {
                try {
                    val bitmap = withContext(Dispatchers.Default) {
                        API.fetchPlayerHead(uuid)
                    }
                    playerHeads[uuid] = bitmap
                } catch (e: Exception) {
                    log.error(e) { "Failed to fetch head" }
                } finally {
                    loadingUuids.remove(uuid)
                }
            }
        }
    }
}
