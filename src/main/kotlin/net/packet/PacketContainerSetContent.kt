package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.ItemStack
import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.net.packet.Packet.Companion.readCompressedCompoundTag
import dev.apollointhehouse.net.packet.Packet.Companion.writeCompressedCompoundTag
import io.ktor.utils.io.*

class PacketContainerSetContent(
    val windowId: Byte = 0,
    val stateId: Int = 0,
    val stackList: Array<ItemStack?> = arrayOf(),
    val carriedItem: ItemStack? = null
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(windowId)
        channel.writeInt(stateId)
        writeStack(channel, carriedItem)
        channel.writeShort(stackList.size.toShort())

        for (i in this.stackList.indices) {
            writeStack(channel, this.stackList[i])
        }
    }

    override val estimatedSize: Int
        get() = 12 + this.stackList.size * 5

    companion object : PacketFactory<PacketContainerSetContent> {
		override val packetID = 104
        override suspend fun create(channel: ByteReadChannel): PacketContainerSetContent {
            val windowId = channel.readByte()
            val stateId = channel.readInt()
            val carriedItem = readStack(channel)
            val listSize = channel.readShort()
            val stackList = arrayOfNulls<ItemStack>(listSize.toInt())

            for (i in 0..<listSize) {
                stackList[i] = readStack(channel)
            }

            return PacketContainerSetContent(windowId, stateId, stackList, carriedItem)
        }

        private suspend fun readStack(channel: ByteReadChannel): ItemStack? {
            val itemId = channel.readShort()
            if (itemId >= 0) {
                val size = channel.readByte()
                val meta = channel.readShort()
                val tag: CompoundTag? = channel.readCompressedCompoundTag()
                return ItemStack(itemId, size, meta, tag)
            } else {
                return null
            }
        }

        private suspend fun writeStack(channel: ByteWriteChannel, stack: ItemStack?) {
            if (stack == null) {
                channel.writeShort(-1)
            } else {
                channel.writeShort(stack.itemID)
                channel.writeByte(stack.size)
                channel.writeShort(stack.meta)
                channel.writeCompressedCompoundTag(stack.tag!!)
            }
        }
    }
}
