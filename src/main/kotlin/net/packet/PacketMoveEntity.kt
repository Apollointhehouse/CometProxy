package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

open class PacketMoveEntity(
    val id: Int = 0,
    val x: Byte = 0,
    val y: Byte = 0,
    val z: Byte = 0,
    val yaw: Byte = 0,
    val pitch: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(id)
    }

    override val estimatedSize: Int
        get() = 4

    class Pos(
        id: Int,
        x: Byte,
        y: Byte,
        z: Byte,
    ) : PacketMoveEntity(
        id = id,
        x = x,
        y = y,
        z = z,
    ) {
        override suspend fun write(channel: ByteWriteChannel) {
            super.write(channel)
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

    class PosRot(
        id: Int,
        x: Byte,
        y: Byte,
        z: Byte,
        yaw: Byte,
        pitch: Byte,
    ) : PacketMoveEntity(id, x, y, z, yaw, pitch) {
        override suspend fun write(channel: ByteWriteChannel) {
            super.write(channel)
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

    class Rot(
        id: Int,
        yaw: Byte,
        pitch: Byte,
    ) : PacketMoveEntity(
        id = id,
        yaw = yaw,
        pitch = pitch,
    ) {
        override suspend fun write(channel: ByteWriteChannel) {
            super.write(channel)
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

    companion object : PacketFactory<PacketMoveEntity> {
		override suspend fun create(channel: ByteReadChannel): PacketMoveEntity = PacketMoveEntity(id = channel.readInt())
    }
}