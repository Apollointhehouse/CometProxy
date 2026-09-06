package dev.apollointhehouse.net.proxy.pipeline

import dev.apollointhehouse.net.packet.Packet

fun interface PacketHandler<T : Packet> {
    suspend fun handle(context: PacketContext, packet: T): Packet?
}

