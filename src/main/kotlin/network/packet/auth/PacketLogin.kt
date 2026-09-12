package dev.apollointhehouse.network.packet.auth

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.readUUID
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeUUID
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
import java.util.*

data class PacketLogin(
    val playerEntityIdAndProtocolVersion: Int = 0,
    val username: String,
    val uuid: UUID,
    val worldSeed: Long = 0,
    val dimensionId: Int = 0,
    val worldTypeId: Int = 0,
    val packetDelay: Byte = 0,
    var publicKey: String
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(playerEntityIdAndProtocolVersion)
        sink.writeJavaStringUTF8(username)
        sink.writeUUID(uuid)
        sink.writeJavaStringUTF8(publicKey)
        sink.writeLong(worldSeed)
        sink.writeInt(dimensionId)
        sink.writeInt(worldTypeId)
        sink.writeByte(packetDelay)
    }

    override val estimatedSize: Int
        get() = 4 + username.length + 8 + 1 + 4 + 4

    companion object : StreamingPacketFactory<PacketLogin> {
        override suspend fun create(channel: ByteReadChannel): PacketLogin {
            val playerEntityIdAndProtocolVersion = channel.readInt()
            val username = channel.readJavaStringUTF8(16)
            val uuid = channel.readUUID()
            val publicKey = channel.readJavaStringUTF8(392)
            val worldSeed = channel.readLong()
            val dimensionId = channel.readInt()
            val worldTypeId = channel.readInt()
            val packetDelay = channel.readByte()

            return PacketLogin(
                playerEntityIdAndProtocolVersion,
                username,
                uuid,
                worldSeed,
                dimensionId,
                worldTypeId,
                packetDelay,
                publicKey
            )
        }
    }
}