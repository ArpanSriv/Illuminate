package com.arpan.musicplayer.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.arpan.musicplayer.R
import com.arpan.musicplayer.activity.MainActivity
import com.arpan.musicplayer.adapter.ArtistListAdapter
import com.arpan.musicplayer.model.Artist
import kotlinx.android.synthetic.main.fragment_artist_all_artists.*
import kotlinx.android.synthetic.main.fragment_artist_list.*

// Created on 26-01-2018

class AllArtistsFragment : Fragment() {

    private var mArtistsList: ArrayList<Artist> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_all_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val loadArtistsTask = LoadArtistsTask()
        loadArtistsTask.execute()

    }

    inner class LoadArtistsTask : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            allArtistsProgressBar.visibility = View.VISIBLE
            allArtistsRecyclerView.visibility = View.INVISIBLE
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            loadArtists()
            return null
        }

        override fun onPostExecute(result: Void?) {
            allArtistsProgressBar.visibility = View.GONE
            setUpRecyclerView()
            allArtistsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        val adapter = ArtistListAdapter(activity as MainActivity, context, mArtistsList)
        allArtistsRecyclerView.adapter = adapter


        val layoutManager = GridLayoutManager(context, 3, LinearLayout.VERTICAL, false)
        allArtistsRecyclerView.layoutManager = layoutManager
    }

    private fun loadArtists() {
        val projection = arrayOf(MediaStore.Audio.Artists.ARTIST)
        val sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC"
        val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
        val cursor = activity!!.contentResolver.query(uri, projection, null, null, sortOrder)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                    val artist = Artist(artistName, null, null)
                    mArtistsList.add(artist)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
    }
}