package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
import kotlinx.io.writeFloat
import kotlin.experimental.and

data class PacketPlaySoundDirect(
    val soundId: Short = 0,
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
    val volume: Float = 0f,
    val pitch: Float = 0f,
    val soundType: Byte = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeShort(soundId)
        sink.writeByte(soundType)
        sink.writeFloat(x.toFloat())
        sink.writeFloat(y.toFloat())
        sink.writeFloat(z.toFloat())
        sink.writeFloat(volume)
        sink.writeFloat(pitch)
    }

    override val estimatedSize: Int
        get() = 23

    companion object : StreamingPacketFactory<PacketPlaySoundDirect> {
        override suspend fun create(channel: ByteReadChannel): PacketPlaySoundDirect {
            val soundId = (channel.readShort() and '\uffff'.code.toShort())
            val soundType = channel.readByte()
            val x = channel.readFloat().toDouble()
            val y = channel.readFloat().toDouble()
            val z = channel.readFloat().toDouble()
            val volume = channel.readFloat()
            val pitch = channel.readFloat()

            return PacketPlaySoundDirect(
                soundId = soundId,
                x = x,
                y = y,
                z = z,
                volume = volume,
                pitch = pitch,
                soundType = soundType
            )
        }
    }
}
