package dev.apollointhehouse.proxy.pipeline

import dev.apollointhehouse.packet.Packet

fun interface PacketHandler<T : Packet> {
    suspend fun handle(context: PacketContext, packet: T): Packet?
}

