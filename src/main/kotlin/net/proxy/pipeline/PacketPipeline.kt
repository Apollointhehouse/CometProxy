package dev.apollointhehouse.net.proxy.pipeline

import dev.apollointhehouse.net.packet.Packet
import kotlin.reflect.KClass

class PacketPipeline {
    private val handlers = mutableMapOf<KClass<out Packet>, MutableList<PacketHandler<Packet>>>()

    inline operator fun <reified T : Packet> plusAssign(handler: PacketHandler<T>) =
        register(handler)

    inline fun <reified T : Packet> register(handler: PacketHandler<T>) =
        register(T::class, handler)

    fun <T : Packet> register(type: KClass<T>, handler: PacketHandler<T>) {
        @Suppress("UNCHECKED_CAST")
        handlers.getOrPut(type) { mutableListOf() }.add(handler as PacketHandler<Packet>)
    }

    suspend fun process(context: PacketContext, packet: Packet): Packet? {
        var current: Packet = packet
        val chain = handlers[packet::class] ?: return packet
        for (handler in chain) {
            current = handler.handle(context, current) ?: return null
        }

        return current
    }
}