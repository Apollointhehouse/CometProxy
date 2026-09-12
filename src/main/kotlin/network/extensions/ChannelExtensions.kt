package dev.apollointhehouse.network.extensions

import dev.apollointhehouse.nbt.NbtIO
import dev.apollointhehouse.nbt.tags.CompoundTag
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readByte
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readLong
import io.ktor.utils.io.readShort
import io.ktor.utils.io.writeByte
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeLong
import io.ktor.utils.io.writeShort
import kotlinx.io.Source
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.UUID

suspend fun ByteWriteChannel.writeJavaStringUTF8(string: String) {
    if (string.length > 32767) {
        throw IOException("String too big")
    }

    val buf = string.toByteArray(StandardCharsets.UTF_8)
    writeShort(buf.size.toShort())
    writeFully(buf)
}

suspend fun ByteWriteChannel.writeJavaStringUTF16BE(string: String) {
    if (string.length > 32767) {
        throw IOException("String too big")
    }

    val buf = string.toByteArray(StandardCharsets.UTF_16BE)
    writeShort(string.length.toShort())
    writeFully(buf)
}

suspend fun ByteWriteChannel.writeUUID(uuid: UUID) {
    writeLong(uuid.mostSignificantBits)
    writeLong(uuid.leastSignificantBits)
}

suspend fun ByteReadChannel.readJavaStringUTF8(maxLength: Int): String {
    val length = readShort()
    if (length < 0) {
        throw IOException("Received string length is less than zero! Weird string!")
    }

    if (length > maxLength) {
        throw IOException("Received string length longer than maximum allowed ($length > $maxLength)")
    }

    val data = ByteArray(length.toInt())
    readFully(data)
    return String(data, StandardCharsets.UTF_8)
}

suspend fun ByteReadChannel.readJavaStringUTF16BE(maxLength: Int): String {
    val length = readShort()
    if (length < 0) {
        throw IOException("Received string length is less than zero! Weird string!")
    }

    if (length > maxLength) {
        throw IOException("Received string length longer than maximum allowed ($length > $maxLength)")
    }

    val data = ByteArray(length * 2)
    readFully(data)
    val result = String(data, StandardCharsets.UTF_16BE)
    return result
}

suspend fun ByteReadChannel.readUUID(): UUID {
    val msb = readLong()
    val lsb = readLong()
    return UUID(msb, lsb)
}

suspend fun ByteWriteChannel.writeCompressedCompoundTag(tag: CompoundTag?) {
    if (tag == null) return

    val output = ByteArrayOutputStream()
    NbtIO.writeCompressed(tag, output)
    val buffer = output.toByteArray()
    writeShort(buffer.size.toShort())
    writeFully(buffer)
}

suspend fun ByteReadChannel.readCompressedCompoundTag(): CompoundTag? {
    val length = readShort().toUShort().toInt()
    if (length == 0) {
        return null
    } else {
        val data = ByteArray(length)
        readFully(data)
        return NbtIO.readCompressed(ByteArrayInputStream(data))
    }
}

suspend fun ByteWriteChannel.writeBoolean(bool: Boolean) {
    writeByte(if (bool) 1 else 0)
}

suspend fun ByteReadChannel.readBoolean(): Boolean {
    return readByte() != 0.toByte()
}

fun Source.readBoolean(): Boolean {
    return readByte() != 0.toByte()
}