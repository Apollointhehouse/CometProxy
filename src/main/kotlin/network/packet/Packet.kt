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
        private val log = logger()

        suspend fun readPacket(channel: ByteReadChannel): Packet? {
            try {
                if (channel.isClosedForRead) return null
                val id = channel.readByte().toUByte().toInt()
                val factory = PacketRegistry.getPacketFactory(id) ?: throw IOException("Unregistered packet id: $id")

                val packet = when (factory) {
                    is BufferedPacketFactory<*> -> factory.create(channel.readPacket(factory.size))
                    is StreamingPacketFactory<*> -> factory.create(channel)
                }

                log.debug { "READ id=$id class=${packet::class.simpleName}" }
                return packet
            } catch (_: EOFException) {
                log.debug { "Connection closed while reading packet" }
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
