package dev.apollointhehouse.nbt

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.nbt.tags.Tag
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object NbtIO {
    fun readCompressed(input: InputStream): CompoundTag {
        return DataInputStream(BufferedInputStream(GZIPInputStream(input))).use { dis ->
            read(dis)
        }
    }

    fun writeCompressed(tag: CompoundTag, output: OutputStream) {
        DataOutputStream(BufferedOutputStream(GZIPOutputStream(output))).use { dos ->
            write(tag, dos)
            dos.flush()
        }
    }

    fun read(input: DataInput): CompoundTag {
        val tag: Tag<*> = Tag.read(input)
        if (tag !is CompoundTag) {
            throw IOException("Root tag must be a named compound tag! (received ${tag::class.simpleName})")
        }
        return tag
    }

    fun write(tag: CompoundTag, output: DataOutput) {
        Tag.write(tag, output)
    }

    fun read(bytes: ByteArray): CompoundTag {
        return DataInputStream(ByteArrayInputStream(bytes)).use { dis ->
            read(dis)
        }
    }

    fun write(tag: CompoundTag): ByteArray {
        val baos = ByteArrayOutputStream()
        DataOutputStream(baos).use { dos ->
            write(tag, dos)
            dos.flush()
        }
        return baos.toByteArray()
    }
}
