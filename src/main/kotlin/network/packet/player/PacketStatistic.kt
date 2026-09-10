package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketStatistic(
    val statID: String = "",
    val valueChange: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(statID)
        channel.writeByte(valueChange)
    }

    override val estimatedSize: Int
        get() = 6

    companion object : PacketFactory<PacketStatistic> {
		override suspend fun create(channel: ByteReadChannel): PacketStatistic {
            val statID = channel.readJavaStringUTF8(Integer.MAX_VALUE)
            val valueChange = channel.readByte()

            return PacketStatistic(statID = statID, valueChange = valueChange)
        }
    }
}
