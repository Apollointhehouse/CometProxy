package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.model.TilePos
import dev.apollointhehouse.network.extensions.readBoolean
import dev.apollointhehouse.network.extensions.writeBoolean
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
import kotlinx.io.writeDouble
import kotlinx.io.writeFloat

data class PacketExplosion(
    val explosionX: Double = 0.0,
    val explosionY: Double = 0.0,
    val explosionZ: Double = 0.0,
    val explosionSize: Float = 0f,
    val isCannonball: Boolean = false,
    val destroyedBlockPositions: MutableSet<TilePos> = mutableSetOf()
) : Packet {
    override fun write(sink: Sink) {
        sink.writeDouble(explosionX)
        sink.writeDouble(explosionY)
        sink.writeDouble(explosionZ)
        sink.writeFloat(explosionSize)
        sink.writeInt(destroyedBlockPositions.size)
        val i = this.explosionX.toInt()
        val j = this.explosionY.toInt()
        val k = this.explosionZ.toInt()

        for ((x, y, z) in destroyedBlockPositions) {
            sink.writeByte((x - i).toByte())
            sink.writeByte((y - j).toByte())
            sink.writeByte((z - k).toByte())
        }

        sink.writeBoolean(isCannonball)
    }

    override val estimatedSize: Int
        get() = 32 + this.destroyedBlockPositions.size * 3 + 1

    companion object : StreamingPacketFactory<PacketExplosion> {
        override suspend fun create(channel: ByteReadChannel): PacketExplosion {
            val explosionX = channel.readDouble()
            val explosionY = channel.readDouble()
            val explosionZ = channel.readDouble()
            val explosionSize = channel.readFloat()
            val times = channel.readInt()
            val x = explosionX.toInt()
            val y = explosionY.toInt()
            val z = explosionZ.toInt()

            val destroyedBlockPositions: MutableSet<TilePos> = mutableSetOf()

            repeat(times) {
                val x1 = channel.readByte() + x
                val y1 = channel.readByte() + y
                val z1 = channel.readByte() + z
                destroyedBlockPositions.add(TilePos(x1, y1, z1))
            }

            val isCannonball = channel.readBoolean()

            return PacketExplosion(
                explosionX,
                explosionY,
                explosionZ,
                explosionSize,
                isCannonball,
                destroyedBlockPositions
            )
        }
    }
}
