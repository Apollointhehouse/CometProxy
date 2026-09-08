package dev.apollointhehouse.utils

import dev.apollointhehouse.net.packet.Packet
import dev.apollointhehouse.net.packet.PacketPingHandshake
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking

object JvmWarmup {
    fun warmup() = runBlocking {
        val packet = PacketPingHandshake()

        repeat(10_500) {
            val channel = ByteChannel()

            Packet.writePacket(channel, packet)
            Packet.readPacket(channel) as PacketPingHandshake
        }
    }
}