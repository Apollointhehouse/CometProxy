package dev.apollointhehouse.network.pipeline

import dev.apollointhehouse.network.packet.Packet

fun interface PacketHandler<T : Packet> {
    suspend fun handle(context: PacketContext, packet: T): Packet?
}

