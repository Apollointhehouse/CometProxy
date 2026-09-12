package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.*
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
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


    override fun write(sink: Sink) {
        sink.writeInt(entityId)
        sink.writeJavaStringUTF8(name)
        sink.writeUUID(uuid)
        sink.writeInt(xPosition)
        sink.writeInt(yPosition)
        sink.writeInt(zPosition)
        sink.writeByte(rotation)
        sink.writeByte(pitch)
        sink.writeShort(currentItem)
        sink.writeJavaStringUTF16BE(nickname)
        sink.writeByte(chatColor)
        sink.writeShort(playerConfig)
        sink.writeJavaStringUTF16BE(gamemode)

        val heldObjectTag = heldObjectTag
        if (heldObjectTag != null) {
            sink.writeByte(1)
            sink.writeCompressedCompoundTag(heldObjectTag)
        } else {
            sink.writeByte(0)
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

            return PacketAddPlayer(
                entityId,
                name,
                uuid,
                xPosition,
                yPosition,
                zPosition,
                rotation,
                pitch,
                currentItem,
                nickname,
                chatColor,
                playerConfig,
                gamemode,
                heldObjectTag
            )
        }

    }
}
