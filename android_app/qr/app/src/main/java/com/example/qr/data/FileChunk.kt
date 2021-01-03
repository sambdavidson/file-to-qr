package com.example.qr.data

import android.util.Base64
import android.util.Log

class FileChunk(val name: String,
                val uuid: String,
                val part: Int,
                val total: Int,
                val base64Chunk: String){
    override fun toString(): String {
        return "$name:$uuid:${part.toString()}:${total.toString()}:{DATA}"
    }

}