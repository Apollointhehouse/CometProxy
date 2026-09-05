package dev.apollointhehouse.data

import io.ktor.utils.io.*

class MapWaypoint {
    var mapCoordX: Byte = 0
    var mapCoordZ: Byte = 0
    var xPos: Int = 0
    var yPos: Int = 0
    var zPos: Int = 0
    var colors: ByteArray = ByteArray(9)

    suspend fun readInputStream(channel: ByteReadChannel) {
        mapCoordX = channel.readByte()
        mapCoordZ = channel.readByte()
        xPos = channel.readInt()
        yPos = channel.readInt()
        zPos = channel.readInt()
        colors = ByteArray(9)

        for (i in colors.indices) {
            colors[i] = channel.readByte()
        }
    }

    suspend fun writeToOutputStream(channel: ByteWriteChannel) {
        channel.writeByte(mapCoordX)
        channel.writeByte(mapCoordZ)
        channel.writeInt(xPos)
        channel.writeInt(yPos)
        channel.writeInt(zPos)

        for (color in colors) {
            channel.writeByte(color)
        }
    }
}