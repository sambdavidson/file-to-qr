package com.example.qr.fileDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qr.data.FileChunkManager
import com.example.qr.data.FileChunkSet

class FileDetailViewModel(private val fileChunkManager: FileChunkManager) : ViewModel() {

    fun getFileForId(id: String) : FileChunkSet? {
        return fileChunkManager.getSetForId(id)
    }

    fun removeFile(fileset: FileChunkSet) {
        fileChunkManager.removeFileSet(fileset.fileid)
    }
}

class FileDetailViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FileDetailViewModel(
                    fileChunkManager = FileChunkManager.getFileChunkManager()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}