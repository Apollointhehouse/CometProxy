package dev.apollointhehouse.utils

import dev.apollointhehouse.net.packet.Packet
import dev.apollointhehouse.net.packet.PacketPingHandshake
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking

object JvmWarmup {
    fun warmup(): Int = runBlocking {
        var sum = 0

        val classes: MutableList<Class<*>> = mutableListOf()
        val ids: MutableList<Int> = mutableListOf()

        for ((clazz, id) in Packet.classToPacketID) {
            classes.add(clazz)
            ids.add(id)
            sum += id
        }

        val packet = PacketPingHandshake()
        val channel = ByteChannel()

        Packet.writePacket(channel, packet)
        val readPacket = Packet.readPacket(channel) as PacketPingHandshake

        sum += readPacket.port

        sum
    }
}