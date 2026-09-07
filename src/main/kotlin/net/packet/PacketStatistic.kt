package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readJavaStringUTF8
import dev.apollointhehouse.utils.extensions.writeJavaStringUTF8
import io.ktor.utils.io.*

class PacketStatistic(
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
