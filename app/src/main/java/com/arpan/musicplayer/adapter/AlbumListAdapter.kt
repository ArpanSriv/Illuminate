package com.arpan.musicplayer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arpan.musicplayer.R
import com.arpan.musicplayer.model.Album
import kotlinx.android.synthetic.main.list_item_album.view.*
import kotlin.collections.ArrayList

// Created on 12/19/2017

class AlbumListAdapter(
        private val mContext: Context?,
        private val mAlbumsList: ArrayList<Album>
    ) : RecyclerView.Adapter<AlbumListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item_album, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = mAlbumsList[position]
        holder.bindViews(album.albumName, album.artistName, album.albumArtUri)
    }

    override fun getItemCount(): Int {
        return mAlbumsList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }


        override fun onClick(itemView: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        fun bindViews(albumName: String, artistName: String, albumArtUri: String?) {
            itemView.albumNameLabel.text = albumName
            itemView.artistNameLabel.text = artistName

            //FIXME (ALBUM ART NOT DISPLAYING)
//            GlideApp
//                    .with(mContext)
//                    .load(albumArtUri)
//                    .placeholder(R.drawable.ic_compact_disc)
//                    .into(itemView.albumAlbumArt)
        }

    }
}