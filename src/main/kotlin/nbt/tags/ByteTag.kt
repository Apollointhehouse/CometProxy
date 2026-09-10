package dev.apollointhehouse.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class ByteTag(
    override val name: String? = null,
    override val value: Byte = 0.toByte()
) : Tag<Byte> {

    override val type: TagType = TagType.Byte

    override fun write(dos: DataOutput) {
        dos.writeByte(value.toInt())
    }

    override fun copy(): ByteTag = ByteTag(name, value)

    companion object : TagFactory<ByteTag> {
        override fun create(name: String?, dis: DataInput): ByteTag =
            ByteTag(name, dis.readByte())
    }
}
