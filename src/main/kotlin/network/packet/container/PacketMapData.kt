package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.model.MapWaypoint
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.Sink
import kotlin.experimental.and

data class PacketMapData(
    var itemId: Short = 0,
    var meta: Short = 0,
    var scale: Byte = 0,
    var mapData: ByteArray = byteArrayOf()
) : Packet {
    val waypoints: MutableList<MapWaypoint> = mutableListOf()

    override fun write(sink: Sink) {
        sink.writeShort(itemId)
        sink.writeShort(meta)
        sink.writeByte(scale)
        sink.writeByte(waypoints.size.toByte())

        for (i in waypoints.indices) {
            waypoints[i].write(sink)
        }

        sink.writeByte(mapData.size.toByte())
        sink.writeFully(mapData)
    }

    override val estimatedSize: Int
        get() = 4 + mapData.size

    companion object : StreamingPacketFactory<PacketMapData> {
        override suspend fun create(channel: ByteReadChannel): PacketMapData {
            val itemId = channel.readShort()
            val meta = channel.readShort()
            val scale = channel.readByte()
            val wayPointAmount = channel.readByte()
            val waypoints = mutableListOf<MapWaypoint>()

            repeat(wayPointAmount.toInt()) {
                val waypoint = MapWaypoint.create(channel)
                waypoints.add(waypoint)
            }

            val mapData = ByteArray((channel.readByte() and 0xFF.toByte()).toInt())
            channel.readFully(mapData)

            return PacketMapData(itemId, meta, scale, mapData)
        }

    }
}
