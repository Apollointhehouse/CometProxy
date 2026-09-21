package dev.apollointhehouse.network.packet

interface PacketSink {
    fun sendPacket(packet: Packet)
}