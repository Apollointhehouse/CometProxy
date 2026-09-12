package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.model.ItemStack
import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketContainerClick(
    val windowID: Byte = 0,
    val action: Byte = 0,
    val args: IntArray = intArrayOf(),
    val stateId: Int = 0,
    val changedSlots: MutableMap<Short, ItemStack?> = mutableMapOf(),
    val carriedItem: ItemStack? = null
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(windowID)
        channel.writeByte(action)
        if (this.args.isNotEmpty()) {
            if (this.args.size > 255) {
                System.err.println("Too many args!")
                Thread.dumpStack()
                channel.writeByte(0)
                return
            }

            channel.writeByte(args.size.toByte())

            for (i in this.args.indices) {
                channel.writeInt(args[i])
            }
        } else {
            channel.writeByte(0)
        }

        channel.writeInt(stateId)
        if (changedSlots.isEmpty()) {
            channel.writeShort(0)
        } else {
            channel.writeShort(changedSlots.size.toShort())

            for ((key, value) in changedSlots) {
                channel.writeShort(key)
                writeStack(channel, value)
            }
        }

        writeStack(channel, this.carriedItem)
    }

    override val estimatedSize: Int
        get() = 11 + (if (this.args.isNotEmpty()) this.args.size * 4 else 0) + (if (this.changedSlots.isNotEmpty()) this.changedSlots.size * 7 else 0)

    companion object : StreamingPacketFactory<PacketContainerClick> {
		override suspend fun create(channel: ByteReadChannel): PacketContainerClick {
            val windowID = channel.readByte()
            val action = channel.readByte()
            val size = channel.readByte()
            val args = IntArray(size.toInt())

            for (i in 0..<size) {
                args[i] = channel.readInt()
            }

            val stateId = channel.readInt()
            val changedCount = channel.readShort().toInt()
            val changedSlots = mutableMapOf<Short, ItemStack?>()

            repeat(changedCount) {
                val slot = channel.readShort()
                changedSlots[slot] = readStack(channel)
            }

            val carriedItem = readStack(channel)

            return PacketContainerClick(windowID, action, args, stateId, changedSlots, carriedItem)
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
                channel.writeCompressedCompoundTag(stack.tag)
            }
        }
    }
}
