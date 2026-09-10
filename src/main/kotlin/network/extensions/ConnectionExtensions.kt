package dev.apollointhehouse.network.extensions

import io.ktor.network.sockets.*

fun Connection.close() {
    socket.close()
}