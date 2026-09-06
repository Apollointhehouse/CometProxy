package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF16BE
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF16BE
import io.ktor.utils.io.*

class PacketEntityNickname(
    var entityId: Int = 0,
    var nickname: String,
    var chatColor: Byte = 0
) : Packet {
    override val estimatedSize: Int
        get() = 6 + nickname.length * 2 + 1

    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeJavaStringUTF16BE(nickname)
        channel.writeByte(chatColor)
    }

    companion object : PacketFactory<PacketEntityNickname> {
        override val packetID = 35

        override suspend fun create(channel: ByteReadChannel): PacketEntityNickname {
            val entityId = channel.readInt()
            val nickname = channel.readJavaStringUTF16BE(256)
            val chatColor = channel.readByte()

            return PacketEntityNickname(entityId, nickname, chatColor)
        }
    }
}