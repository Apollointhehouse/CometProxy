package dev.apollointhehouse.model

import io.ktor.utils.io.*

class MapWaypoint(
    val mapX: Byte = 0,
    val mapZ: Byte = 0,
    val xPos: Int = 0,
    val yPos: Int = 0,
    val zPos: Int = 0,
    val colors: ByteArray = ByteArray(9)
) {
    suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(mapX)
        channel.writeByte(mapZ)
        channel.writeInt(xPos)
        channel.writeInt(yPos)
        channel.writeInt(zPos)

        for (color in colors) {
            channel.writeByte(color)
        }
    }

    companion object {
        suspend fun create(channel: ByteReadChannel): MapWaypoint {
            val mapCoordX = channel.readByte()
            val mapCoordZ = channel.readByte()
            val xPos = channel.readInt()
            val yPos = channel.readInt()
            val zPos = channel.readInt()
            val colors = ByteArray(9)

            for (i in colors.indices) {
                colors[i] = channel.readByte()
            }

            return MapWaypoint(mapCoordX, mapCoordZ, xPos, yPos, zPos, colors)
        }
    }
}