package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readBoolean
import dev.apollointhehouse.net.packet.Packet.Companion.writeBoolean
import io.ktor.utils.io.*

open class PacketMovePlayer(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    var onGround: Boolean = false
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeBoolean(onGround)
    }

    override val estimatedSize: Int
        get() = 1

    class Pos(
        x: Double,
        y: Double,
        z: Double,
        onGround: Boolean,
    ) : PacketMovePlayer(
        x = x,
        y = y,
        z = z,
        onGround = onGround,
    ) {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeDouble(x)
            channel.writeDouble(y)
            channel.writeDouble(z)
            super.write(channel)
        }

        override val estimatedSize: Int
            get() = 33

        companion object : PacketFactory<Pos> {
		    override val packetID = 11

            override suspend fun create(channel: ByteReadChannel): Pos {
                val x = channel.readDouble()
                val y = channel.readDouble()
                val z = channel.readDouble()
                val onGround = channel.readBoolean()

                return Pos(x, y, z, onGround)
            }
        }
    }

    class PosRot(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        onGround: Boolean,
    ) : PacketMovePlayer(
        x = x,
        y = y,
        z = z,
        yaw = yaw,
        pitch = pitch,
        onGround = onGround,
    ) {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeDouble(x)
            channel.writeDouble(y)
            channel.writeDouble(z)
            channel.writeFloat(yaw)
            channel.writeFloat(pitch)
            super.write(channel)
        }

        override val estimatedSize: Int
            get() = 41

        companion object : PacketFactory<PosRot> {
		    override val packetID = 13

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

    class Rot(
        yaw: Float,
        pitch: Float,
        onGround: Boolean,
    ) : PacketMovePlayer(
        yaw = yaw,
        pitch = pitch,
        onGround = onGround,
    ) {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeFloat(yaw)
            channel.writeFloat(pitch)
            super.write(channel)
        }

        override val estimatedSize: Int
            get() = 9

        companion object : PacketFactory<Rot> {
		    override val packetID = 12
            override suspend fun create(channel: ByteReadChannel): Rot {
                val yaw = channel.readFloat()
                val pitch = channel.readFloat()
                val onGround = channel.readByte().toInt() != 0

                return Rot(yaw, pitch, onGround)
            }
        }
    }

    companion object : PacketFactory<PacketMovePlayer> {
		override val packetID = 10
        override suspend fun create(channel: ByteReadChannel): PacketMovePlayer = PacketMovePlayer(onGround = channel.readBoolean())
    }
}
