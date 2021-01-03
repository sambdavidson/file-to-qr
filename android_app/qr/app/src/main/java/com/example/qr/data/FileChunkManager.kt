package com.example.qr.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FileChunkManager private constructor() {
    private val idToSet = mutableMapOf<String, FileChunkSet>()
    private val setLiveData =  MutableLiveData<List<FileChunkSet>>()

    init {
        setLiveData.postValue(idToSet.values.toList())
    }

    fun addChunkString(s: String): Boolean {
        val chunk = ParseFileChunk(s) ?: return false

        val set = idToSet[chunk.uuid]
        if (set == null) {
            idToSet[chunk.uuid] = FileChunkSet(chunk)
            setLiveData.postValue(idToSet.values.toList())
        } else {
            set.AddChunk(chunk)
            setLiveData.postValue(setLiveData.value)
        }
        return true
    }


    fun removeFileSet(uuid: String) {
        idToSet.remove(uuid)
        setLiveData.postValue(idToSet.values.toList())
    }

    fun getSetForId(uuid: String): FileChunkSet? {
        return idToSet[uuid]
    }

    fun getFileSetList(): LiveData<List<FileChunkSet>> {
        return setLiveData
    }


    companion object {
        private var INSTANCE: FileChunkManager? = null

        fun getFileChunkManager(): FileChunkManager {
            return synchronized(FileChunkManager::class) {
                val newInstance = INSTANCE ?: FileChunkManager()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}