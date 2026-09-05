package dev.apollointhehouse.packet

import io.ktor.utils.io.*
import kotlin.experimental.and

class PacketPlaySoundDirect(
    val soundId: Short = 0,
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
    val volume: Float = 0f,
    val pitch: Float = 0f,
    val soundType: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeShort(soundId)
        channel.writeByte(soundType)
        channel.writeFloat(x.toFloat())
        channel.writeFloat(y.toFloat())
        channel.writeFloat(z.toFloat())
        channel.writeFloat(volume)
        channel.writeFloat(pitch)
    }

    override val estimatedSize: Int
        get() = 23

    companion object : PacketFactory<PacketPlaySoundDirect> {
		override val packetID = 62
        override suspend fun create(channel: ByteReadChannel): PacketPlaySoundDirect {
            val soundId = (channel.readShort() and '\uffff'.code.toShort())
            val soundType = channel.readByte()
            val x = channel.readFloat().toDouble()
            val y = channel.readFloat().toDouble()
            val z = channel.readFloat().toDouble()
            val volume = channel.readFloat()
            val pitch = channel.readFloat()

            return PacketPlaySoundDirect(soundId = soundId, x = x, y = y, z = z, volume = volume, pitch = pitch, soundType = soundType)
        }
    }
}
