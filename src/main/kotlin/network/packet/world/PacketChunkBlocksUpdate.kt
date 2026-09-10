package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*
import kotlin.experimental.and

data class PacketChunkBlocksUpdate(
    val xChunk: Int = 0,
    val zChunk: Int = 0,
    val coordinateArray: IntArray = intArrayOf(),
    val typeArray: ShortArray = shortArrayOf(),
    val metadataArray: ByteArray = byteArrayOf(),
    val size: Short = 0
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(xChunk)
        channel.writeInt(zChunk)
        channel.writeShort(size)

        for (i in 0..<size) {
           channel.writeInt(coordinateArray[i])
        }

        for (i in 0..<size) {
           channel.writeShort(typeArray[i])
        }

        channel.writeFully(metadataArray)
    }

    override val estimatedSize: Int
        get() = 10 + size * 4

    companion object : PacketFactory<PacketChunkBlocksUpdate> {
		override suspend fun create(channel: ByteReadChannel): PacketChunkBlocksUpdate {
            val xChunk = channel.readInt()
            val zChunk = channel.readInt()
            val size = channel.readShort() and '\uffff'.code.toShort()
            val coordinateArray = IntArray(size.toInt())
            val typeArray = ShortArray(size.toInt())
            val metadataArray = ByteArray(size.toInt())

            for (i in 0..<size) {
                coordinateArray[i] = channel.readInt()
            }

            for (i in 0..<size) {
                typeArray[i] = channel.readShort()
            }

            channel.readFully(metadataArray)

            return PacketChunkBlocksUpdate(xChunk, zChunk, coordinateArray, typeArray, metadataArray, size)
        }
    }
}
