package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.extensions.*
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
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
    override fun write(sink: Sink) {
        sink.writeJavaStringUTF8(username)
        sink.writeJavaStringUTF16BE(nickname)
        sink.writeUUID(uuid)
        sink.writeInt(score)
        sink.writeByte(chatColor)
        sink.writeBoolean(isOnline)
        sink.writeBoolean(isOperator)
    }

    override val estimatedSize: Int
        get() = 10 + this.username.length + 4

    companion object : StreamingPacketFactory<PacketUpdatePlayerProfile> {
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
