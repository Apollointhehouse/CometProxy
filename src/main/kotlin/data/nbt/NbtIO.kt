package dev.apollointhehouse.data.nbt

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.data.nbt.tags.Tag
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object NbtIO {
    fun readCompressed(input: InputStream): CompoundTag {
        DataInputStream(GZIPInputStream(input)).use { dis ->
            return read(dis)
        }
    }

    fun writeCompressed(tag: CompoundTag, output: OutputStream) {
        DataOutputStream(GZIPOutputStream(output)).use { dos ->
            write(tag, dos)
        }
    }

    fun read(input: DataInput): CompoundTag {
        val tag: Tag<*> = Tag.read(input)
        if (tag !is CompoundTag) {
            throw IOException("Root tag must be a named compound tag!")
        }

        return tag
    }

    fun write(tag: CompoundTag, output: DataOutput) {
        Tag.write(tag, output)
    }
}
