package com.example.qr.fileList

import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qr.R
import com.example.qr.data.FileChunkSet
import com.example.qr.data.IMAGE_TYPES
import java.io.File
import kotlin.math.floor

class FileAdapter(private val onClick: (FileChunkSet) -> Unit) :
    ListAdapter<FileChunkSet, FileAdapter.FileViewHolder>(FlowerDiffCallback) {

    class FileViewHolder(itemView: View, val onClick: (FileChunkSet) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val fileTextView: TextView = itemView.findViewById(R.id.file_name)
        private val fileImageView: ImageView = itemView.findViewById(R.id.file_image)
        private val filePercentView: TextView = itemView.findViewById(R.id.file_percent)
        private var currentFile: FileChunkSet? = null

        init {
            itemView.setOnClickListener {
                currentFile?.let {
                    onClick(it)
                }
            }
        }

        /* Bind file name and image. */
        fun bind(fileset: FileChunkSet) {
            currentFile = fileset

            fileTextView.text = fileset.filename
            val ext = File(fileset.filename).extension
            if (IMAGE_TYPES.contains(ext)) {
                fileImageView.setImageResource(android.R.drawable.ic_menu_gallery)
            } else {
                fileImageView.setImageResource(android.R.drawable.ic_dialog_email)
            }

            val percent = floor((fileset.partsFound.toDouble() / fileset.partsTotal.toDouble()) * 100.0).toInt()
            filePercentView.text = "${percent}%"

        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_row_item, parent, false)
        return FileViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val flower = getItem(position)
        holder.bind(flower)

    }
}

object FlowerDiffCallback : DiffUtil.ItemCallback<FileChunkSet>() {
    override fun areItemsTheSame(oldItem: FileChunkSet, newItem: FileChunkSet): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FileChunkSet, newItem: FileChunkSet): Boolean {
        return oldItem.fileid == newItem.fileid && oldItem.partsFound == newItem.partsFound
    }
}
