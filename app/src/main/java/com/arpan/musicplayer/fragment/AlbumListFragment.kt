package com.arpan.musicplayer.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AlbumColumns.*
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arpan.musicplayer.R
import com.arpan.musicplayer.activity.MainActivity
import com.arpan.musicplayer.adapter.AlbumListAdapter
import com.arpan.musicplayer.model.Album
import kotlinx.android.synthetic.main.fragment_album_list.*

// Created on 12/19/2017

class AlbumListFragment: Fragment() {
    private var mAlbumsList: ArrayList<Album> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        while (ContextCompat.checkSelfPermission((activity as MainActivity),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {}

        val loadAlbumsTask = LoadAlbumsTask()
        loadAlbumsTask.execute()
    }



    private inner class LoadAlbumsTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
//            val mainActivity = activity as MainActivity
            loadAlbumInArray()
            return null
        }

        override fun onPreExecute() {
            loadingProgressBar_albums.visibility = View.VISIBLE
            albumsListRecyclerView.visibility = View.INVISIBLE
            loadingProgressBar_albums.isIndeterminate = true
        }

        override fun onPostExecute(result: Void?) {
            loadingProgressBar_albums.visibility = View.GONE
            setUpRecyclerView()
            albumsListRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        val adapter = AlbumListAdapter(context, mAlbumsList)
        albumsListRecyclerView.adapter = adapter
        albumsListRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun loadAlbumInArray() {

        val projection = arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.NUMBER_OF_SONGS)
        val sortOrder = MediaStore.Audio.Media.ALBUM + " ASC"
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val cursor = activity!!.contentResolver.query(uri, projection, null, null, sortOrder)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val albumName = cursor.getString(cursor.getColumnIndex(ALBUM))
                    val noOfSongs = cursor.getInt(cursor.getColumnIndex(NUMBER_OF_SONGS))
//                    val albumId = cursor.getLong(cursor.getColumnIndex(ALBUM_ID))
                    val artistName = cursor.getString(cursor.getColumnIndex(ARTIST))
                    val albumUri = cursor.getString(cursor.getColumnIndex(ALBUM_ART))
//                    val albumArtUri = Uri.parse(albumUri)

                    val album = Album(albumName, null, artistName, "ABC")
                    mAlbumsList.add(album)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
    }
}