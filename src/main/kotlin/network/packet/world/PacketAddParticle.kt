package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.extensions.readBoolean
import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeBoolean
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
import kotlinx.io.writeDouble
import kotlinx.io.writeFloat

data class PacketAddParticle(
    var particleKey: String,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var motionX: Double = 0.0,
    var motionY: Double = 0.0,
    var motionZ: Double = 0.0,
    var data: Int = 0,
    var maxDistance: Double = 0.0,
    var amount: Byte = 0,
    var randOffX: Float = 0f,
    var randOffY: Float = 0f,
    var randOffZ: Float = 0f,
    var randMotionX: Float = 0f,
    var randMotionY: Float = 0f,
    var randMotionZ: Float = 0f,
    var isGroup: Boolean = false
) : Packet {
    override fun write(sink: Sink) {
        sink.writeJavaStringUTF8(particleKey)
        sink.writeDouble(x)
        sink.writeDouble(y)
        sink.writeDouble(z)
        sink.writeDouble(motionX)
        sink.writeDouble(motionY)
        sink.writeDouble(motionZ)
        sink.writeInt(data)
        sink.writeDouble(maxDistance)
        sink.writeBoolean(isGroup)
        if (isGroup) {
            sink.writeByte(amount)
            sink.writeFloat(randOffX)
            sink.writeFloat(randOffY)
            sink.writeFloat(randOffZ)
            sink.writeFloat(randMotionX)
            sink.writeFloat(randMotionY)
            sink.writeFloat(randMotionZ)
        }
    }

    override val estimatedSize: Int
        get() = 40

    companion object : StreamingPacketFactory<PacketAddParticle> {
        override suspend fun create(channel: ByteReadChannel): PacketAddParticle {
            val particleKey = channel.readJavaStringUTF8(100)
            val x = channel.readDouble()
            val y = channel.readDouble()
            val z = channel.readDouble()
            val motionX = channel.readDouble()
            val motionY = channel.readDouble()
            val motionZ = channel.readDouble()
            val data = channel.readInt()
            val maxDistance = channel.readDouble()

            var amount: Byte = 0
            var randOffX = 0f
            var randOffY = 0f
            var randOffZ = 0f
            var randMotionX = 0f
            var randMotionY = 0f
            var randMotionZ = 0f
            var isGroup: Boolean

            if (channel.readBoolean()) {
                isGroup = true
                amount = channel.readByte()
                randOffX = channel.readFloat()
                randOffY = channel.readFloat()
                randOffZ = channel.readFloat()
                randMotionX = channel.readFloat()
                randMotionY = channel.readFloat()
                randMotionZ = channel.readFloat()
            } else {
                isGroup = false
            }

            return PacketAddParticle(
                particleKey,
                x,
                y,
                z,
                motionX,
                motionY,
                motionZ,
                data,
                maxDistance,
                amount,
                randOffX,
                randOffY,
                randOffZ,
                randMotionX,
                randMotionY,
                randMotionZ,
                isGroup
            )
        }

    }
}
