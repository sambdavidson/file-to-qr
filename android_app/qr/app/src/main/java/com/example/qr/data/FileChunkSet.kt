package com.example.qr.data

import android.util.Base64
import android.util.Log

class FileChunkSet {
    val chunkList: MutableList<FileChunk?>
    val filename: String
    val fileid: String
    var partsFound: Int
    val partsTotal: Int

    constructor(chunk: FileChunk) {
        Log.i("Main", chunk.toString())
        this.filename = chunk.name
        this.fileid = chunk.uuid
        this.partsTotal = chunk.total
        this.chunkList = MutableList(this.partsTotal) { null }
        this.chunkList[chunk.part] = chunk
        this.partsFound = 1
    }

    fun AddChunk(chunk: FileChunk) {
        if (this.chunkList[chunk.part] == null) {
            this.chunkList[chunk.part] = chunk
            this.partsFound++
        }
    }

    fun IsComplete(): Boolean {
        return partsFound == partsTotal
    }

    fun CombineChunks(): ByteArray? {
        if (!IsComplete()) {
            return null
        }
        var base64EncFile = ""
        chunkList.forEach { fileChunk ->
            if (fileChunk != null) {
                base64EncFile += fileChunk.base64Chunk
            } else {
                return null
            }
        }
        try {
            return Base64.decode(base64EncFile, Base64.DEFAULT)
        } catch (e: Throwable) {
            Log.e("Main", e.toString())
        }
        return null
    }

    override fun toString(): String {
        return "${this.filename} : ${this.partsFound} / ${this.partsTotal} = ${Math.round((this.partsFound.toFloat()/this.partsTotal.toFloat()) * 100.0)}%"
    }

}