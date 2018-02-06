package com.arpan.musicplayer.fragment

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arpan.musicplayer.R
import kotlinx.android.synthetic.main.fragment_artist_details_about_artist.*

// Created on 18-01-2018
class ArtistDescriptionFragment_About : Fragment() {

    private val FRAGMENT_TITLE = "FRAGMENT_TITLE"
    private val CURRENT_ARTIST = "CURRENT_ARTIST"
    private val TAG = ArtistDescriptionFragment_About::class.java.simpleName
    private val API_KEY = "8e71dd998e907fa081fdf059691e10c9"

    private var fragmentTitle: String? = null
    private var currentArtistName: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ArtistDescriptionFragment_About().apply {
                    arguments = Bundle().apply {
                        putString(FRAGMENT_TITLE, param1)
                        putString(CURRENT_ARTIST, param2)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_artist_details_about_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.let {
            fragmentTitle = it.getString("FRAGMENT_TITLE")
            currentArtistName = it.getString("CURRENT_ARTIST")
        }

        val fetchTask = FetchArtistImageUrlTask()
        fetchTask.execute(currentArtistName)

    }

    @SuppressLint("StaticFieldLeak")
    inner class FetchArtistImageUrlTask : AsyncTask<String, Void, Void>() {

        override fun onPreExecute() {
            artistDetailsProgressBar.visibility = View.VISIBLE
            artistDetailTextView.visibility = View.GONE
        }

        override fun doInBackground(vararg artistNames: String?): Void? {

            val artistName = artistNames[0]

            val artistNameWithoutSpaces = artistName?.replace("\\s", "+")

            val response = khttp.get("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=$artistNameWithoutSpaces&api_key=$API_KEY&format=json")
            val artistObj = response.jsonObject

            try {
                val content: String? = artistObj.getJSONObject("artist").getJSONObject("bio").getString("content")
                artistDetailTextView.text = content
            } catch (exception: Exception) {
                exception.printStackTrace()
//                val errorCode = artistObj.getInt("error")
//                val message = artistObj.getString("message")
//                Log.d(TAG, "artistName: ${artistName} errorCode: $errorCode message: $message")
                artistDetailTextView.text = getString(R.string.artist_not_found, artistName)
            }
            return null
        }

        override fun onPostExecute(result: Void?) {

            artistDetailsProgressBar.visibility = View.GONE
            artistDetailTextView.visibility = View.VISIBLE
        }
    }
}