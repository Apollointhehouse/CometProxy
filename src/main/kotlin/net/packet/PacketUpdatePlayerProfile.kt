package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readBoolean
import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF16BE
import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF8
import dev.apollointhehouse.net.packet.Packet.Companion.readUUID
import dev.apollointhehouse.net.packet.Packet.Companion.writeBoolean
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF16BE
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF8
import dev.apollointhehouse.net.packet.Packet.Companion.writeUUID
import io.ktor.utils.io.*
import java.util.*

class PacketUpdatePlayerProfile(
    val username: String = "",
    val nickname: String = "",
    val uuid: UUID = UUID(0, 0),
    val score: Int = 0,
    val chatColor: Byte = 0,
    val isOnline: Boolean = false,
    val isOperator: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(username)
        channel.writeJavaStringUTF16BE(nickname)
        channel.writeUUID(uuid)
        channel.writeInt(score)
        channel.writeByte(chatColor)
        channel.writeBoolean(isOnline)
        channel.writeBoolean(isOperator)
    }

    override val estimatedSize: Int
        get() = 10 + this.username.length + 4

    companion object : PacketFactory<PacketUpdatePlayerProfile> {
		override val packetID = 72
        override suspend fun create(channel: ByteReadChannel): PacketUpdatePlayerProfile {
            val username = channel.readJavaStringUTF8(16)
            val nickname = channel.readJavaStringUTF16BE(256)
            val uuid = channel.readUUID()
            val score = channel.readInt()
            val chatColor = channel.readByte()
            val isOnline = channel.readBoolean()
            val isOperator = channel.readBoolean()

            return PacketUpdatePlayerProfile(
                username = username,
                nickname = nickname,
                uuid = uuid,
                score = score,
                chatColor = chatColor,
                isOnline = isOnline,
                isOperator = isOperator
            )
        }
    }
}
