# CometProxy

A MITM proxy for [Better Than Adventure](http://betterthanadventure.net/) (BTA!) servers, written in Kotlin. 
It sits between a BTA client and a real server, decodes every packet that passes through, and lets you inspect or modify the connection.

## How

CometProxy poses as a BTA! server, accepts a client connection, opens its own connection to the target server, and relays packets in both directions. 
Packets are fully parsed and inspected through a handler pipeline before being resent.

CometProxy is also able to read otherwise encrypted chat packets:
- On `PacketLogin`, the proxy swaps the client's real RSA public key for its own before forwarding to the server. It also keeps the client's real key.
- When the server responds with the AES session key, the proxy decrypts it with its own private key, then re-encrypts it with the client's real public key before forwarding.
- The proxy now holds the shared AES key for that session, so it can decrypt chat messages passing through in either direction.

## Architecture

- `network/proxy/Proxy.kt`: accepts incoming TCP connections and spawns a `Bridge` per session.
- `network/proxy/Bridge.kt`: runs two coroutines per connection (client->server, server->client), each reading packets and pushing them through the pipeline.
- `network/pipeline/`: a packet processing pipeline (middleware), which allows the proxy to insepct/modify packets.
- `network/pipeline/handlers/`: the built-in handlers: `HandlerLogin`, `HandlerAESSendKey`, `HandlerMessage`, `HandlerPingHandshake`.
- `network/packet/`: packet protocol. Each packet implements its serialization strategy, as BTA! does not have packet length headers, most packets must be read field by field from the stream, however a known/fixed size optimization is implemented for reading certain packets of known size.
- `nbt/`: a standalone NBT reader/writer (Minecraft/BTA! binary data format).
- `network/warmup/JvmWarmup.kt`: runs dummy serialization/crypto/socket work at startup to warm up the JIT.
- `ui/`: a desktop GUI (Compose Multiplatform + Jewel) showing active connections (with player heads), a log panel, and the ability to send arbitrary messages to client/server.

## Building and running

Requires JDK 25.

```bash
./gradlew packageUberJarForCurrentOS
```

Run with the GUI:

```bash
./gradlew run
```

Or headless, with CLI args (`-h` to see all args):

```bash
./gradlew run --args="--headless --target-server example.com --target-port 25565 --host-port 25566"
```

In headless mode, typing `stop` shuts the proxy down.

## Testing

Unit tests for packet serialization, NBT encoding, and JVM warmup routine. Run with:

```bash
./gradlew test
```

## License

MIT. See [LICENSE](LICENSE).