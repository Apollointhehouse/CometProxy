import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.handshake.PacketPingHandshake
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs


class PacketTest {
    @Test
    fun `serialize packet`() = runTest {
        val packet = PacketPingHandshake(
            payload = 0u,
            identifier = 0u,
            pingHostString = "",
            protocolVersion = 0u,
            hostname = "",
            port = 0,
        )

        val channel = ByteChannel()
        Packet.writePacket(channel, packet)
        channel.close()

        val refBytes = byteArrayOf(-2, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0)
        val bytes = channel.toByteArray()

        assert(bytes.isNotEmpty())
        assertContentEquals(refBytes, bytes, "Packet data incorrectly serialized")
    }

    @Test
    fun `deserialize packet`() = runTest {
        val channel = ByteReadChannel(byteArrayOf(-2, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0))

        val packet = Packet.readPacket(channel)
        val refPacket = PacketPingHandshake(
            payload = 0u,
            identifier = 0u,
            pingHostString = "",
            protocolVersion = 0u,
            hostname = "",
            port = 0,
        )

        assertIs<PacketPingHandshake>(packet)
        assertEquals(refPacket, packet)
    }
}
