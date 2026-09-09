package dev.apollointhehouse.utils.extensions

import io.ktor.network.sockets.*

fun Connection.close() {
    socket.close()
}