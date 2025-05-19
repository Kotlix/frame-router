package ru.kotlix.frame.router.server.hash

import org.springframework.stereotype.Component
import java.util.zip.CRC32

@Component
class ReadableHashValueRepresent : ValueRepresent {
    override fun representInt(value: Int): String = representLong(value.toLong())

    override fun representLong(value: Long): String {
        val bytes =
            ByteArray(8) {
                (value ushr (it * 8)).toByte()
            }

        val crc = CRC32()
        crc.update(bytes)
        val hashValue = crc.value

        val base36 = "0123456789abcdefghijklmnopqrstuvwxyz"
        val result = CharArray(12)
        var remaining = hashValue

        for (i in 11 downTo 0) {
            result[i] = base36[(remaining % 36).toInt()]
            remaining /= 36
        }

        return String(result)
    }
}
