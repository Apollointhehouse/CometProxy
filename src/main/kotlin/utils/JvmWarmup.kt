package dev.apollointhehouse.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import dev.apollointhehouse.net.packet.Packet
import dev.apollointhehouse.net.packet.PacketDisconnect
import dev.apollointhehouse.net.packet.PacketPingHandshake
import dev.apollointhehouse.net.packet.PacketRegistry
import dev.apollointhehouse.net.proxy.handlers.ChatMessageHandler
import dev.apollointhehouse.net.proxy.handlers.ProxyAesKeyHandler
import dev.apollointhehouse.net.proxy.handlers.ProxyLoginHandler
import dev.apollointhehouse.net.proxy.pipeline.ConnectionContext
import dev.apollointhehouse.net.proxy.pipeline.PacketContext
import dev.apollointhehouse.net.proxy.pipeline.PacketPipeline
import dev.apollointhehouse.utils.crypt.AES
import dev.apollointhehouse.utils.crypt.RSA
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.slf4j.LoggerFactory

object JvmWarmup {
    fun warmup() = runBlocking(Dispatchers.IO) {
        val context = LoggerFactory.getILoggerFactory() as? LoggerContext
        val rootLogger = context?.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        val originalLevel = rootLogger?.level
        rootLogger?.level = Level.OFF

        try {
            PacketRegistry.getPacketFactory(254)
            PacketRegistry.getPacketFactory(255)

            val keyPair = RSA.generateKeyPair()
            val pubKeyStr = RSA.getPublicKey(keyPair.public)
            val pubKey = RSA.getPublicKey(pubKeyStr)
            val encrypted = RSA.encrypt("warmup", pubKey)
            RSA.decrypt(encrypted, keyPair.private)

            val aesKey = AES.generateKey()
            val aesKeyStr = AES.getKey(aesKey)
            val parsedAesKey = AES.getKey(aesKeyStr)
            val aesEncrypted = AES.encrypt("warmup", parsedAesKey)
            if (aesEncrypted != null) {
                AES.decrypt(aesEncrypted, parsedAesKey)
            }

            val pipeline = PacketPipeline().apply {
                register(ProxyLoginHandler(keyPair))
                register(ProxyAesKeyHandler(keyPair))
                register(ChatMessageHandler())
            }

            val dummyOut1 = ByteChannel()
            val dummyOut2 = ByteChannel()
            val connCtx = ConnectionContext(dummyOut1, dummyOut2)
            val c2sContext = PacketContext(PacketContext.Direction.CLIENT_TO_SERVER, connCtx)
            val s2cContext = PacketContext(PacketContext.Direction.SERVER_TO_CLIENT, connCtx)

            val pingPacket = PacketPingHandshake(
                payload = 1u,
                identifier = 220u,
                pingHostString = "CometProxyWarmup",
                protocolVersion = 69u,
                hostname = "localhost",
                port = 25565
            )
            val defaultPingPacket = PacketPingHandshake()

            val disconnectPacket = PacketDisconnect(
                reason = "§1\u0000127\u0000Comet Proxy\u0000JVM Warmup Session\u00000\u000020"
            )
            val defaultDisconnectPacket = PacketDisconnect()

            repeat(10_500) {
                val channel = ByteChannel()
                Packet.writePacket(channel, pingPacket)
                Packet.readPacket(channel) as PacketPingHandshake

                val channel2 = ByteChannel()
                Packet.writePacket(channel2, disconnectPacket)
                Packet.readPacket(channel2) as PacketDisconnect

                val channel3 = ByteChannel()
                Packet.writePacket(channel3, defaultPingPacket)
                Packet.readPacket(channel3) as PacketPingHandshake

                val channel4 = ByteChannel()
                Packet.writePacket(channel4, defaultDisconnectPacket)
                Packet.readPacket(channel4) as PacketDisconnect

                pipeline.process(c2sContext, pingPacket)
                pipeline.process(s2cContext, disconnectPacket)
            }

            connCtx.close()
            dummyOut1.close()
            dummyOut2.close()

            try {
                val selectorManager = ActorSelectorManager(Dispatchers.IO)
                val serverSocket = aSocket(selectorManager).tcp().bind(hostname = "localhost", port = 0)
                val localAddress = serverSocket.localAddress as InetSocketAddress

                coroutineScope {
                    val serverJob = async {
                        val client = serverSocket.accept()
                        val readChan = client.openReadChannel()
                        val writeChan = client.openWriteChannel()

                        val receivedPing = Packet.readPacket(readChan)
                        if (receivedPing is PacketPingHandshake) {
                            Packet.writePacket(writeChan, disconnectPacket)
                        }
                        client.close()
                    }

                    val clientJob = async {
                        val clientSocket = aSocket(selectorManager).tcp().connect(localAddress)
                        val readChan = clientSocket.openReadChannel()
                        val writeChan = clientSocket.openWriteChannel()

                        Packet.writePacket(writeChan, pingPacket)
                        Packet.readPacket(readChan)
                        clientSocket.close()
                    }

                    serverJob.await()
                    clientJob.await()
                }

                serverSocket.close()
                selectorManager.close()
            } catch (_: Exception) {
            }
        } finally {
            rootLogger?.level = originalLevel
            logger.debug { "Warmup Completed!" }
        }
    }
}