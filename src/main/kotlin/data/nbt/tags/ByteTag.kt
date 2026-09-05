package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class ByteTag(name: String?, value: Byte = 0.toByte()) : Tag<Byte>(name, value) {
    override fun write(dos: DataOutput) {
        dos.writeByte(value.toInt())
    }

    override val id get() = TagID.TAG_BYTE.id

    companion object : TagFactory<ByteTag> {
        override fun create(name: String?, dis: DataInput): ByteTag {
            val value = dis.readByte()

            return ByteTag(name, value)
        }
    }
}
