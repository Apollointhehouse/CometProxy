package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.model.ItemStack
import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketContainerSetSlot(
    var windowId: Byte = 0,
    var stateId: Int = 0,
    var itemSlot: Short = 0,
    var myItemStack: ItemStack? = null
) : Packet {


    override fun write(sink: Sink) {
        sink.writeByte(windowId)
        sink.writeInt(stateId)
        sink.writeShort(itemSlot)
        if (this.myItemStack == null) {
            sink.writeShort(-1)
        } else {
            sink.writeShort(myItemStack!!.itemID)
            sink.writeByte(myItemStack!!.size)
            sink.writeShort(myItemStack!!.meta)
            sink.writeCompressedCompoundTag(this.myItemStack!!.tag!!)
        }
    }

    override val estimatedSize: Int
        get() = 12

    companion object : StreamingPacketFactory<PacketContainerSetSlot> {
        override suspend fun create(channel: ByteReadChannel): PacketContainerSetSlot {
            val windowId = channel.readByte()
            val stateId = channel.readInt()
            val itemSlot = channel.readShort()
            val itemID = channel.readShort()

            var stack: ItemStack?

            if (itemID >= 0) {
                val size = channel.readByte()
                val meta = channel.readShort()
                val tag: CompoundTag? = channel.readCompressedCompoundTag()
                stack = ItemStack(itemID, size, meta, tag)
            } else {
                stack = null
            }

            return PacketContainerSetSlot(windowId, stateId, itemSlot, stack)
        }
    }
}
