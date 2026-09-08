package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readBoolean
import dev.apollointhehouse.utils.extensions.writeBoolean
import io.ktor.utils.io.*

sealed interface PacketMovePlayer : Packet {
    var onGround: Boolean

    data class NoPosition(
        override var onGround: Boolean = false
    ) : PacketMovePlayer {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeBoolean(onGround)
        }

        override val estimatedSize: Int
            get() = 1

        companion object : PacketFactory<NoPosition> {
            override suspend fun create(channel: ByteReadChannel): NoPosition =
                NoPosition(onGround = channel.readBoolean())
        }
    }

    data class Position(
        var x: Double,
        var y: Double,
        var z: Double,
        override var onGround: Boolean,
    ) : PacketMovePlayer {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeDouble(x)
            channel.writeDouble(y)
            channel.writeDouble(z)
            channel.writeBoolean(onGround)
        }

        override val estimatedSize: Int
            get() = 33

        companion object : PacketFactory<Position> {
		    
            override suspend fun create(channel: ByteReadChannel): Position {
                val x = channel.readDouble()
                val y = channel.readDouble()
                val z = channel.readDouble()
                val onGround = channel.readBoolean()

                return Position(x, y, z, onGround)
            }
        }
    }

    data class PosRot(
        var x: Double,
        var y: Double,
        var z: Double,
        var yaw: Float,
        var pitch: Float,
        override var onGround: Boolean,
    ) : PacketMovePlayer {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeDouble(x)
            channel.writeDouble(y)
            channel.writeDouble(z)
            channel.writeFloat(yaw)
            channel.writeFloat(pitch)
            channel.writeBoolean(onGround)
        }

        override val estimatedSize: Int
            get() = 41

        companion object : PacketFactory<PosRot> {
		    
            override suspend fun create(channel: ByteReadChannel): PosRot {
                val x = channel.readDouble()
                val y = channel.readDouble()
                val z = channel.readDouble()
                val yaw = channel.readFloat()
                val pitch = channel.readFloat()
                val onGround = channel.readBoolean()

                return PosRot(x, y, z, yaw, pitch, onGround)
            }
        }
    }

    data class Rotation(
        var yaw: Float,
        var pitch: Float,
        override var onGround: Boolean,
    ) : PacketMovePlayer {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeFloat(yaw)
            channel.writeFloat(pitch)
            channel.writeBoolean(onGround)
        }

        override val estimatedSize: Int
            get() = 9

        companion object : PacketFactory<Rotation> {
            override suspend fun create(channel: ByteReadChannel): Rotation {
                val yaw = channel.readFloat()
                val pitch = channel.readFloat()
                val onGround = channel.readByte().toInt() != 0

                return Rotation(yaw, pitch, onGround)
            }
        }
    }
}
