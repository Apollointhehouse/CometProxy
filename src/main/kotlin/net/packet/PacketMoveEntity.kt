package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

interface PacketMoveEntity : Packet {
    val id: Int

    data class None(
        override val id: Int = 0,
    ) : PacketMoveEntity {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeInt(id)
        }

        override val estimatedSize: Int
            get() = 4

        companion object : PacketFactory<None> {
            override suspend fun create(channel: ByteReadChannel): None =
                None(id = channel.readInt())
        }
    }

    data class Pos(
        override val id: Int,
        val x: Byte,
        val y: Byte,
        val z: Byte,
    ) : PacketMoveEntity {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeInt(id)
            channel.writeByte(x)
            channel.writeByte(y)
            channel.writeByte(z)
        }

        override val estimatedSize: Int
            get() = 7

        companion object : PacketFactory<Pos> {
		    
            override suspend fun create(channel: ByteReadChannel): Pos = Pos(
                id = channel.readInt(),
                x = channel.readByte(),
                y = channel.readByte(),
                z = channel.readByte()
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
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeInt(id)
            channel.writeByte(x)
            channel.writeByte(y)
            channel.writeByte(z)
            channel.writeByte(yaw)
            channel.writeByte(pitch)
        }

        override val estimatedSize: Int
            get() = 9

        companion object : PacketFactory<PosRot> {
            override suspend fun create(channel: ByteReadChannel): PosRot = PosRot(
                id = channel.readInt(),
                x = channel.readByte(),
                y = channel.readByte(),
                z = channel.readByte(),
                yaw = channel.readByte(),
                pitch = channel.readByte()
            )
        }
    }

    data class Rot(
        override val id: Int,
        val yaw: Byte,
        val pitch: Byte,
    ) : PacketMoveEntity {
        override suspend fun write(channel: ByteWriteChannel) {
            channel.writeInt(id)
            channel.writeByte(yaw)
            channel.writeByte(pitch)
        }

        override val estimatedSize: Int
            get() = 6

        companion object : PacketFactory<Rot> {
            override suspend fun create(channel: ByteReadChannel): Rot = Rot(
                id = channel.readInt(),
                yaw = channel.readByte(),
                pitch = channel.readByte()
            )
        }
    }
}