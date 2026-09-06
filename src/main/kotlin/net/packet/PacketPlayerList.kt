package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF16BE
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF16BE
import io.ktor.utils.io.*

class PacketPlayerList(
    val players: Array<String> = arrayOf(),
    val scores: Array<String> = arrayOf(),
    val count: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(count)

        for (i in 0..<this.count) {
            channel.writeJavaStringUTF16BE(players[i])
            channel.writeJavaStringUTF16BE(scores[i])
        }
    }

    override val estimatedSize: Int
        get() {
            var size = 4

            for (i in this.scores.indices) {
                size += this.scores[i].length
            }

            for (i in this.players.indices) {
                size += this.players[i].length
            }

            return size
        }

    companion object : PacketFactory<PacketPlayerList> {
		override val packetID = 138
        override suspend fun create(channel: ByteReadChannel): PacketPlayerList {
            val count = channel.readInt()

            val (players, scores) = (0..<count)
                .map { channel.readJavaStringUTF16BE(256) to channel.readJavaStringUTF16BE(256) }
                .unzip()

            return PacketPlayerList(
                players = players.toTypedArray(),
                scores = scores.toTypedArray(),
                count = count
            )
        }
    }
}