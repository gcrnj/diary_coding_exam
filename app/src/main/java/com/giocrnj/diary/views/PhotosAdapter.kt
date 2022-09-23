package com.giocrnj.diary.views

import android.app.Activity
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.giocrnj.diary.databinding.LayoutPhotosItemBinding

class PhotosAdapter(private val uris: MutableList<Uri>, private val removedListener: (removedItem: Int) -> Unit) : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    class PhotosViewHolder(binding: LayoutPhotosItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val mViewBinding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        return PhotosViewHolder(
            LayoutPhotosItemBinding.inflate((parent.context as Activity).layoutInflater)
        )
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val uri = uris[position]
        //Set image
        holder.mViewBinding.imgDiary.setImageURI(uri)
        //Set close click listener
        holder.mViewBinding.imgBtnClose.setOnClickListener {
            removedListener.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return uris.size
    }
}