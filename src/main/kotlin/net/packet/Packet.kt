package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.nbt.NbtIO
import dev.apollointhehouse.data.nbt.tags.CompoundTag
import io.ktor.utils.io.*
import kotlinx.io.EOFException
import org.apache.logging.log4j.kotlin.logger
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

interface Packet {
    val packetID: Int get() = classToPacketID.getValue(this::class.java)
    val estimatedSize: Int

    suspend fun write(channel: ByteWriteChannel)

    companion object {
        private val packetIDToFactory: MutableMap<Int, PacketFactory<*>> = mutableMapOf()
        private val classToPacketID: MutableMap<Class<out Packet>, Int> = mutableMapOf()
        private val packetFactoryToID: MutableMap<PacketFactory<*>, Int> = mutableMapOf()
        private val packetIDS: MutableSet<Int> = mutableSetOf()

        init {
            register(PacketKeepAlive)
            register(PacketLogin)
            register(PacketHandshake)
            register(PacketMessage)
            register(PacketSetTime)
            register(PacketSetEquippedItem)
            register(PacketSetSpawnPosition)
            register(PacketEntityInteract)
            register(PacketSetHealth)
            register(PacketRespawn)
            register(PacketMovePlayer)
            register(PacketMovePlayer.Pos)
            register(PacketMovePlayer.Rot)
            register(PacketMovePlayer.PosRot)
            register(PacketPlayerAction)
            register(PacketUseOrPlaceItemStack)
            register(PacketSetCarriedItem)
            register(PacketSleep)
            register(PacketAnimate)
            register(PacketUpdatePlayerState)
            register(PacketAddPlayer)
            register(PacketAddItemEntity)
            register(PacketTakeItemEntity)
            register(PacketAddEntity)
            register(PacketAddMob)
            register(PacketAddPainting)
            register(PacketVehicleControl)
            register(PacketSetHeldObject)
            register(PacketSetEntityMotion)
            register(PacketRemoveEntity)
            register(PacketMoveEntity)
            register(PacketMoveEntity.Pos)
            register(PacketMoveEntity.Rot)
            register(PacketMoveEntity.PosRot)
            register(PacketTeleportEntity)
            register(PacketEntityNickname)
            register(PacketPlayerConfig)
            register(PacketEntityFling)
            register(PacketEntityEvent)
            register(PacketSetRiding)
            register(PacketSetEntityData)
            register(PacketPlayerGamemode)
            register(PacketEntityTagData)
            register(PacketChunkVisibility)
            register(PacketBlockRegionUpdate)
            register(PacketChunkBlocksUpdate)
            register(PacketBlockUpdate)
            register(PacketBlockEvent)
            register(PacketExplosion)
            register(PacketPlaySound)
            register(PacketPlaySoundDirect)
            register(PacketAddParticle)
            register(PacketMessageTranslatable)
            register(PacketWeatherEffect)
            register(PacketUpdatePlayerProfile)
            register(PacketWeatherStatus)
            register(PacketGameRule)
            register(PacketRecipeSync)
            register(PacketContainerOpen)
            register(PacketContainerClose)
            register(PacketContainerClick)
            register(PacketContainerSetSlot)
            register(PacketContainerSetContent)
            register(PacketContainerSetData)
            register(PacketContainerAck)
            register(PacketSetHotbarOffset)
            register(PacketCommandManager)
            register(PacketRequestCommandManager)
            register(PacketSignUpdate)
            register(PacketMapData)
            register(PacketSetMobSpawner)
            register(PacketGuidebook)
            register(PacketAESSendKey)
            register(PacketSetItemName)
            register(PacketPlayerList)
            register(PacketSetPaintingArt)
            register(PacketTileEntityData)
            register(PacketFlagOpen)
            register(PacketPhotoMode)
            register(PacketStatistic)
            register(PacketSyncIDs)
            register(PacketCustomPayload)
            register(PacketServerIcon)
            register(PacketPingHandshake)
            register(PacketDisconnect)
        }

        inline fun <reified T : Packet> register(factory: PacketFactory<T>) {
            register(T::class.java, factory)
        }

        fun <T : Packet> register(clazz: Class<T>, factory: PacketFactory<T>) {
            require(factory.packetID !in packetIDToFactory) { "Duplicate packet id: ${factory.packetID}" }
            require(factory !in packetFactoryToID) { "Duplicate packet factory: ${factory::class.simpleName}" }

            packetIDToFactory[factory.packetID] = factory
            classToPacketID[clazz] = factory.packetID
            packetIDS.add(factory.packetID)
        }

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

            val baos = ByteArrayOutputStream()
            NbtIO.writeCompressed(tag, baos)
            val buffer = baos.toByteArray()
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

        fun getPacketFactory(id: Int): PacketFactory<*>? {
            return packetIDToFactory[id]
        }

        suspend fun ByteWriteChannel.writeBoolean(bool: Boolean) {
            writeByte(if (bool) 1 else 0)
        }

        suspend fun ByteReadChannel.readBoolean(): Boolean {
            return readByte() != 0.toByte()
        }

        suspend fun readPacket(channel: ByteReadChannel): Packet? {
            try {
                if (channel.isClosedForRead) return null
                val id = channel.readByte().toUByte().toInt()

                if (id !in packetIDS) {
                    throw IOException("Unregistered packet id $id")
                }

                val packetFactory = getPacketFactory(id) ?: throw IOException("Bad packet id $id")
                val packet = packetFactory.create(channel)
                logger.debug { "READ id=$id class=${packet::class.simpleName}" }

                return packet
            } catch (_: EOFException) {
                logger.debug { "Connection closed while reading packet" }
                return null
            }
        }

        suspend fun writePacket(channel: ByteWriteChannel, packet: Packet) {
            try {
                channel.writeByte(packet.packetID.toByte())
                packet.write(channel)
                channel.flush()
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
