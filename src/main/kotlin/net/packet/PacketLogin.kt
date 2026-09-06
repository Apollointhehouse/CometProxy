package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF8
import dev.apollointhehouse.net.packet.Packet.Companion.readUUID
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF8
import dev.apollointhehouse.net.packet.Packet.Companion.writeUUID
import io.ktor.utils.io.*
import java.util.*

class PacketLogin(
    val playerEntityIdAndProtocolVersion: Int = 0,
    val username: String,
    val uuid: UUID,
    val worldSeed: Long = 0,
    val dimensionId: Int = 0,
    val worldTypeId: Int = 0,
    val packetDelay: Byte = 0,
    var publicKey: String
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(playerEntityIdAndProtocolVersion)
        channel.writeJavaStringUTF8(username)
        channel.writeUUID(uuid)
        channel.writeJavaStringUTF8(publicKey)
        channel.writeLong(worldSeed)
        channel.writeInt(dimensionId)
        channel.writeInt(worldTypeId)
        channel.writeByte(packetDelay)
    }

    override val estimatedSize: Int
        get() = 4 + username.length + 8 + 1 + 4 + 4

    companion object : PacketFactory<PacketLogin> {
		override val packetID = 1
        override suspend fun create(channel: ByteReadChannel): PacketLogin {
            val playerEntityIdAndProtocolVersion = channel.readInt()
            val username = channel.readJavaStringUTF8(16)
            val uuid = channel.readUUID()
            val publicKey = channel.readJavaStringUTF8(392)
            val worldSeed = channel.readLong()
            val dimensionId = channel.readInt()
            val worldTypeId = channel.readInt()
            val packetDelay = channel.readByte()

            return PacketLogin(playerEntityIdAndProtocolVersion, username, uuid, worldSeed, dimensionId, worldTypeId, packetDelay, publicKey)
        }
    }
}