package dev.apollointhehouse.network.packet

interface PacketSource {
    suspend fun receivePacket(): Packet?
}