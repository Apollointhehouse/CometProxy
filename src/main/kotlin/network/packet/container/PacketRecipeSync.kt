package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketRecipeSync(
    val recipe: String = "",
    val maxRecipes: Long = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(recipe)
        channel.writeLong(maxRecipes)
    }

    override val estimatedSize: Int
        get() = recipe.toByteArray().size

    companion object : StreamingPacketFactory<PacketRecipeSync> {
		override suspend fun create(channel: ByteReadChannel): PacketRecipeSync {
            val recipe = channel.readJavaStringUTF8(65532)
            val maxRecipes = channel.readLong()

            return PacketRecipeSync(recipe = recipe, maxRecipes = maxRecipes)
        }
    }
}
