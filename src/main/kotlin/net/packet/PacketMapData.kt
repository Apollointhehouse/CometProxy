package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.MapWaypoint
import io.ktor.utils.io.*
import kotlin.experimental.and

class PacketMapData(
    var itemId: Short = 0,
    var meta: Short = 0,
    var scale: Byte = 0,
    var mapData: ByteArray = byteArrayOf()
) : Packet {
    val waypoints: MutableList<MapWaypoint> = mutableListOf()

    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeShort(itemId)
        channel.writeShort(meta)
        channel.writeByte(scale)
        channel.writeByte(waypoints.size.toByte())

        for (i in waypoints.indices) {
            waypoints[i].writeToOutputStream(channel)
        }

        channel.writeByte(mapData.size.toByte())
        channel.writeFully(mapData)
    }

    override val estimatedSize: Int
        get() = 4 + mapData.size

    companion object : PacketFactory<PacketMapData> {
        override val packetID = 131

        override suspend fun create(channel: ByteReadChannel): PacketMapData {
            val itemId = channel.readShort()
            val meta = channel.readShort()
            val scale = channel.readByte()
            val wayPointAmount = channel.readByte()
            val waypoints = mutableListOf<MapWaypoint>()

            repeat(wayPointAmount.toInt()) {
                val waypoint = MapWaypoint()
                waypoint.readInputStream(channel)

                waypoints.add(waypoint)
            }

            val mapData = ByteArray((channel.readByte() and 0xFF.toByte()).toInt())
            channel.readFully(mapData)

            return PacketMapData(itemId, meta, scale, mapData)
        }

    }
}
