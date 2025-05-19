package ru.kotlix.frame.router.server.hash

interface ValueRepresent {
    fun representInt(value: Int): String

    fun representLong(value: Long): String
}
