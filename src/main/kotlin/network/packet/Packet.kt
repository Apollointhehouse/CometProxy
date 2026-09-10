package dev.apollointhehouse.network.packet

import io.ktor.utils.io.*
import kotlinx.io.EOFException
import org.apache.logging.log4j.kotlin.logger
import java.io.IOException

interface Packet {
    val packetID: Int get() = PacketRegistry.classToPacketID.getValue(this::class.java)
    val estimatedSize: Int

    suspend fun write(channel: ByteWriteChannel)

    companion object {
        suspend fun readPacket(channel: ByteReadChannel): Packet? {
            try {
                if (channel.isClosedForRead) return null
                val id = channel.readByte().toUByte().toInt()

                val packetFactory = PacketRegistry.getPacketFactory(id) ?: throw IOException("Unregistered packet id: $id")
                val packet = packetFactory.create(channel)
                logger.debug { "READ id=$id class=${packet::class.simpleName}" }

                return packet
            } catch (_: EOFException) {
                logger.debug { "Connection closed while reading packet" }
                return null
            }
        }

        suspend fun writePacket(channel: ByteWriteChannel, packet: Packet) {
            try {
                channel.writeByte(packet.packetID.toByte())
                packet.write(channel)
                channel.flush()
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
