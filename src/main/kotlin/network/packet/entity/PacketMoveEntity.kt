package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

interface PacketMoveEntity : Packet {
    val id: Int

    data class None(
        override val id: Int = 0,
    ) : PacketMoveEntity {
        override fun write(sink: Sink) {
            sink.writeInt(id)
        }

        override val estimatedSize: Int
            get() = size

        companion object : BufferedPacketFactory<None> {
            override val size: Int = 4

            override fun create(buffer: Source): None =
                None(id = buffer.readInt())
        }
    }

    data class Pos(
        override val id: Int,
        val x: Byte,
        val y: Byte,
        val z: Byte,
    ) : PacketMoveEntity {
        override fun write(sink: Sink) {
            sink.writeInt(id)
            sink.writeByte(x)
            sink.writeByte(y)
            sink.writeByte(z)
        }

        override val estimatedSize: Int
            get() = size

        companion object : BufferedPacketFactory<Pos> {
            override val size: Int = 7

            override fun create(buffer: Source): Pos = Pos(
                id = buffer.readInt(),
                x = buffer.readByte(),
                y = buffer.readByte(),
                z = buffer.readByte()
            )
        }
    }

    data class PosRot(
        override val id: Int,
        val x: Byte,
        val y: Byte,
        val z: Byte,
        val yaw: Byte,
        val pitch: Byte,
    ) : PacketMoveEntity {
        override fun write(sink: Sink) {
            sink.writeInt(id)
            sink.writeByte(x)
            sink.writeByte(y)
            sink.writeByte(z)
            sink.writeByte(yaw)
            sink.writeByte(pitch)
        }

        override val estimatedSize: Int
            get() = size

        companion object : BufferedPacketFactory<PosRot> {
            override val size: Int = 9

            override fun create(buffer: Source): PosRot = PosRot(
                id = buffer.readInt(),
                x = buffer.readByte(),
                y = buffer.readByte(),
                z = buffer.readByte(),
                yaw = buffer.readByte(),
                pitch = buffer.readByte()
            )
        }
    }

    data class Rot(
        override val id: Int,
        val yaw: Byte,
        val pitch: Byte,
    ) : PacketMoveEntity {
        override fun write(sink: Sink) {
            sink.writeInt(id)
            sink.writeByte(yaw)
            sink.writeByte(pitch)
        }

        override val estimatedSize: Int
            get() = size

        companion object : BufferedPacketFactory<Rot> {
            override val size: Int = 6

            override fun create(buffer: Source): Rot = Rot(
                id = buffer.readInt(),
                yaw = buffer.readByte(),
                pitch = buffer.readByte()
            )
        }
    }
}