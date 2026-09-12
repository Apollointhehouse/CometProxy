package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.extensions.readBoolean
import dev.apollointhehouse.network.extensions.writeBoolean
import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.*

sealed interface PacketMovePlayer : Packet {
    var onGround: Boolean

    data class NoPosition(
        override var onGround: Boolean = false
    ) : PacketMovePlayer {
        override fun write(sink: Sink) {
            sink.writeBoolean(onGround)
        }

        override val estimatedSize: Int
            get() = size

        companion object : BufferedPacketFactory<NoPosition> {
            override val size: Int = 1

            override fun create(buffer: Source): NoPosition =
                NoPosition(onGround = buffer.readBoolean())
        }
    }

    data class Position(
        var x: Double,
        var y: Double,
        var z: Double,
        override var onGround: Boolean,
    ) : PacketMovePlayer {
        override fun write(sink: Sink) {
            sink.writeDouble(x)
            sink.writeDouble(y)
            sink.writeDouble(z)
            sink.writeBoolean(onGround)
        }

        override val estimatedSize: Int
            get() = size

        companion object : BufferedPacketFactory<Position> {
            override val size: Int = 25

            override fun create(buffer: Source): Position {
                val x = buffer.readDouble()
                val y = buffer.readDouble()
                val z = buffer.readDouble()
                val onGround = buffer.readBoolean()

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
        override fun write(sink: Sink) {
            sink.writeDouble(x)
            sink.writeDouble(y)
            sink.writeDouble(z)
            sink.writeFloat(yaw)
            sink.writeFloat(pitch)
            sink.writeBoolean(onGround)
        }

        override val estimatedSize: Int
            get() = size

        companion object : BufferedPacketFactory<PosRot> {
            override val size: Int = 33

            override fun create(buffer: Source): PosRot {
                val x = buffer.readDouble()
                val y = buffer.readDouble()
                val z = buffer.readDouble()
                val yaw = buffer.readFloat()
                val pitch = buffer.readFloat()
                val onGround = buffer.readBoolean()

                return PosRot(x, y, z, yaw, pitch, onGround)
            }
        }
    }

    data class Rotation(
        var yaw: Float,
        var pitch: Float,
        override var onGround: Boolean,
    ) : PacketMovePlayer {
        override fun write(sink: Sink) {
            sink.writeFloat(yaw)
            sink.writeFloat(pitch)
            sink.writeBoolean(onGround)
        }

        override val estimatedSize: Int
            get() = size

        companion object : BufferedPacketFactory<Rotation> {
            override val size: Int = 9

            override fun create(buffer: Source): Rotation {
                val yaw = buffer.readFloat()
                val pitch = buffer.readFloat()
                val onGround = buffer.readByte().toInt() != 0

                return Rotation(yaw, pitch, onGround)
            }
        }
    }
}
