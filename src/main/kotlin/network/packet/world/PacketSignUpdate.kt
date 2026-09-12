package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.network.extensions.writeJavaStringUTF16BE
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketSignUpdate(
    var xPosition: Int = 0,
    var yPosition: Short = 0,
    var zPosition: Int = 0,
    var signLines: Array<String?> = arrayOf(),
    var picture: Int = 0,
    var color: Int = 0
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(xPosition)
        channel.writeShort(yPosition)
        channel.writeInt(zPosition)

        for (i in 0..<4) {
            channel.writeJavaStringUTF16BE(signLines[i]!!)
        }

        channel.writeInt(picture)
        channel.writeInt(color)
    }

    override val estimatedSize: Int
        get() = signLines.size*16 + 4

    companion object : StreamingPacketFactory<PacketSignUpdate> {
		override suspend fun create(channel: ByteReadChannel): PacketSignUpdate {
            val xPosition = channel.readInt()
            val yPosition = channel.readShort()
            val zPosition = channel.readInt()
            val signLines = arrayOfNulls<String?>(4)

            for (i in 0..<4) {
                signLines[i] = channel.readJavaStringUTF16BE(15)
            }

            val picture = channel.readInt()
            val color = channel.readInt()

            return PacketSignUpdate(xPosition, yPosition, zPosition, signLines, picture, color)
        }

    }
}
