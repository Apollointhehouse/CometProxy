package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF8
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF8
import io.ktor.utils.io.*

class PacketRecipeSync(
    val recipe: String = "",
    val maxRecipes: Long = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(recipe)
        channel.writeLong(maxRecipes)
    }

    override val estimatedSize: Int
        get() = recipe.toByteArray().size

    companion object : PacketFactory<PacketRecipeSync> {
		override val packetID = 75
        override suspend fun create(channel: ByteReadChannel): PacketRecipeSync {
            val recipe = channel.readJavaStringUTF8(65532)
            val maxRecipes = channel.readLong()

            return PacketRecipeSync(recipe = recipe, maxRecipes = maxRecipes)
        }
    }
}
