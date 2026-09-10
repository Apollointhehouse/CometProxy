package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.extensions.*
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*
import java.util.*

data class PacketUpdatePlayerProfile(
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
