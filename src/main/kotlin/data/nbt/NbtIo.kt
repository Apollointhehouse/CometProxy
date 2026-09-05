package dev.apollointhehouse.data.nbt

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.data.nbt.tags.Tag
import java.io.DataInput
import java.io.DataInputStream
import java.io.DataOutput
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object NbtIo {
    fun readCompressed(inputStream: InputStream): CompoundTag {
        DataInputStream(GZIPInputStream(inputStream)).use { dis ->
            return read(dis)
        }
    }

    fun writeCompressed(tag: CompoundTag, outputStream: OutputStream) {
        DataOutputStream(GZIPOutputStream(outputStream)).use { dos ->
            write(tag, dos)
        }
    }

    fun read(dataInput: DataInput): CompoundTag {
        val tag: Tag<*> = Tag.readNamedTag(dataInput)
        if (tag !is CompoundTag) {
            throw IOException("Root tag must be a named compound tag!")
        }

        return tag
    }

    fun write(tag: CompoundTag, dataOutput: DataOutput) {
        Tag.writeNamedTag(tag, dataOutput)
    }
}
