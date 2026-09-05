package dev.apollointhehouse.data.nbt

import java.io.IOException

class UnknownTagException : IOException {

    constructor(message: String?) : super(message)
}
