package com.example.qr.data

import java.util.*

fun IsFileChunk(s:String): Boolean {
    val parts = s.split(":")

    if (parts.size != 5) {
        return false
    }

    if (parts[0].isEmpty()) {
        return false
    }

    if (UUID.fromString(parts[1]) == null) {
        return false
    }

    try {
        val part = parts[2].toInt(16)
        val total = parts[3].toInt(16)
        if (part < 0) {
            return false
        }
        if (total < 1) {
            return false
        }
        if (part >= total) {
            return false
        }
    } catch (e: Throwable) {
        return false
    }

    if (parts[4].isEmpty()) {
        return false
    }

    return true
}

fun ParseFileChunk(s: String): FileChunk? {
    if (!IsFileChunk(s)) {
        return null
    }
    val parts = s.split(":")
    return FileChunk(
        name = parts[0],
        uuid = parts[1],
        part = parts[2].toInt(16),
        total = parts[3].toInt(16),
        base64Chunk = parts[4]
    )
}