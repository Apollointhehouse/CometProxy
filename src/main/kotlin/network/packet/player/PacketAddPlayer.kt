package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.*
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import java.util.*

data class PacketAddPlayer(
    val entityId: Int = 0,
    val name: String,
    val uuid: UUID,
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val zPosition: Int = 0,
    val rotation: Byte = 0,
    val pitch: Byte = 0,
    val currentItem: Short = 0,
    val nickname: String,
    val chatColor: Byte = 0,
    val playerConfig: Short = 0,
    val gamemode: String,
    val heldObjectTag: CompoundTag? = null
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeJavaStringUTF8(name)
        channel.writeUUID(uuid)
        channel.writeInt(xPosition)
        channel.writeInt(yPosition)
        channel.writeInt(zPosition)
        channel.writeByte(rotation)
        channel.writeByte(pitch)
        channel.writeShort(currentItem)
        channel.writeJavaStringUTF16BE(nickname)
        channel.writeByte(chatColor)
        channel.writeShort(playerConfig)
        channel.writeJavaStringUTF16BE(gamemode)

        val heldObjectTag = heldObjectTag
        if (heldObjectTag != null) {
            channel.writeByte(1)
            channel.writeCompressedCompoundTag(heldObjectTag)
        } else {
            channel.writeByte(0)
        }
    }

    override val estimatedSize: Int
        get() = 29

    companion object : StreamingPacketFactory<PacketAddPlayer> {
		override suspend fun create(channel: ByteReadChannel): PacketAddPlayer {
            val entityId = channel.readInt()
            val name = channel.readJavaStringUTF8(16)
            val uuid = channel.readUUID()
            val xPosition = channel.readInt()
            val yPosition = channel.readInt()
            val zPosition = channel.readInt()
            val rotation = channel.readByte()
            val pitch = channel.readByte()
            val currentItem = channel.readShort()
            val nickname = channel.readJavaStringUTF16BE(256)
            val chatColor = channel.readByte()
            val playerConfig = channel.readShort()
            val gamemode = channel.readJavaStringUTF16BE(256)

            val heldObjectTag: CompoundTag? = if (channel.readByte().toInt() == 1) {
                channel.readCompressedCompoundTag()
            } else {
                null
            }

            return PacketAddPlayer(entityId, name, uuid, xPosition, yPosition, zPosition, rotation, pitch, currentItem, nickname, chatColor, playerConfig, gamemode, heldObjectTag)
        }

    }
}
