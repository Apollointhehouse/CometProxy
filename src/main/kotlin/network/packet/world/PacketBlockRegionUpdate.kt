package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.Sink

data class PacketBlockRegionUpdate(
    val xPosition: Int = 0,
    val yPosition: Short = 0,
    val zPosition: Int = 0,
    val xSize: Int = 0,
    val ySize: Int = 0,
    val zSize: Int = 0,
    var chunk: ByteArray = byteArrayOf()
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(xPosition)
        sink.writeShort(yPosition)
        sink.writeInt(zPosition)
        sink.writeByte((xSize - 1).toByte())
        sink.writeByte((ySize - 1).toByte())
        sink.writeByte((zSize - 1).toByte())

        sink.writeInt(chunk.size)
        sink.writeFully(chunk)

//        val deflater = Deflater(-1)
//        try {
//            deflater.setInput(this.chunk)
//            deflater.finish()
//            val outBuffer = ByteArray(this.chunk.size + 64) // headroom, deflate can slightly expand
//            var compressedSize = 0
//            while (!deflater.finished()) {
//                compressedSize += deflater.deflate(outBuffer, compressedSize, outBuffer.size - compressedSize)
//            }
//            channel.writeInt(compressedSize)
//            channel.writeFully(outBuffer, 0, compressedSize)
//        } finally {
//            deflater.end()
//        }
    }

    override val estimatedSize: Int
        get() = 17 + this.chunk.size

    companion object : StreamingPacketFactory<PacketBlockRegionUpdate> {
//		        const val BYTES_PER_CELL = 8

        override suspend fun create(channel: ByteReadChannel): PacketBlockRegionUpdate {
            val xPosition = channel.readInt()
            val yPosition = channel.readShort()
            val zPosition = channel.readInt()
            val xSize = (channel.readByte().toUByte().toInt() + 1)
            val ySize = (channel.readByte().toUByte().toInt() + 1)
            val zSize = (channel.readByte().toUByte().toInt() + 1)

            val compressedSize = channel.readInt()
            val compressedBuffer = ByteArray(compressedSize)
            channel.readFully(compressedBuffer)

//            var chunk: ByteArray
//
//            val inflater = Inflater()
//            try {
//                inflater.setInput(compressedBuffer)
//                val decompressed = ByteArray(xSize * ySize * zSize * BYTES_PER_CELL)
//                var totalRead = 0
//                while (!inflater.finished() && totalRead < decompressed.size) {
//                    val n = inflater.inflate(decompressed, totalRead, decompressed.size - totalRead)
//                    if (n == 0 && inflater.needsInput()) break
//                    totalRead += n
//                }
//                chunk = decompressed
//            } catch (e: DataFormatException) {
//                throw IOException("Bad compressed data format", e)
//            } finally {
//                inflater.end()
//            }

            return PacketBlockRegionUpdate(xPosition, yPosition, zPosition, xSize, ySize, zSize, compressedBuffer)
        }
    }
}