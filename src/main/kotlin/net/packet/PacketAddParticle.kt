package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readBoolean
import dev.apollointhehouse.utils.extensions.readJavaStringUTF8
import dev.apollointhehouse.utils.extensions.writeBoolean
import dev.apollointhehouse.utils.extensions.writeJavaStringUTF8
import io.ktor.utils.io.*

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
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(particleKey)
        channel.writeDouble(x)
        channel.writeDouble(y)
        channel.writeDouble(z)
        channel.writeDouble(motionX)
        channel.writeDouble(motionY)
        channel.writeDouble(motionZ)
        channel.writeInt(data)
        channel.writeDouble(maxDistance)
        channel.writeBoolean(isGroup)
        if (isGroup) {
           channel.writeByte(amount)
           channel.writeFloat(randOffX)
           channel.writeFloat(randOffY)
           channel.writeFloat(randOffZ)
           channel.writeFloat(randMotionX)
           channel.writeFloat(randMotionY)
           channel.writeFloat(randMotionZ)
        }
    }

    override val estimatedSize: Int
        get() = 40

    companion object : PacketFactory<PacketAddParticle> {
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

            return PacketAddParticle(particleKey, x, y, z, motionX, motionY, motionZ, data, maxDistance, amount, randOffX, randOffY, randOffZ, randMotionX, randMotionY, randMotionZ, isGroup)
        }

    }
}
