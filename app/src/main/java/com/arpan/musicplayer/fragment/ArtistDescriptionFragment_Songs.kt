package com.arpan.musicplayer.fragment

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arpan.musicplayer.R
import com.arpan.musicplayer.activity.ArtistActivity
import com.arpan.musicplayer.adapter.SongsListAdapter
import com.arpan.musicplayer.model.Song
import com.arpan.musicplayer.service.PlayerService
import kotlinx.android.synthetic.main.fragment_artist_details_artist_songs.*


public class ArtistDescriptionFragment_Songs : Fragment() {

    // TODO: Rename parameter arguments, choose names that match
    private val FRAGMENT_TITLE = "FRAGMENT_TITLE"
    private val CURRENT_ARTIST = "CURRENT_ARTIST"

    private val mSongsList = ArrayList<Song>()

    // TODO: Rename and change types of parameters
    private var fragmentTitle: String? = null
    private var currentArtistName: String? = null
    private var listener: OnFragmentInteractionListener? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ArtistDescriptionFragment_Songs().apply {
                    arguments = Bundle().apply {
                        putString(FRAGMENT_TITLE, param1)
                        putString(CURRENT_ARTIST, param2)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentTitle = it.getString(FRAGMENT_TITLE)
            currentArtistName= it.getString(CURRENT_ARTIST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_details_artist_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val loadSongsTask = LoadSongsTask()
        loadSongsTask.execute(currentArtistName)
    }

    @SuppressLint("StaticFieldLeak")
    inner class LoadSongsTask: AsyncTask<String, Void, Void>() {

        override fun onPreExecute() {
            artistSongsProgressBar.visibility = View.VISIBLE
            artistSongsRecyclerView.visibility = View.INVISIBLE
        }

        override fun doInBackground(vararg artistNames: String?): Void? {
            loadSongs(artistNames[0])
            return null
        }

        override fun onPostExecute(result: Void?) {
            artistSongsProgressBar.visibility = View.GONE
            setUpRecyclerView()
            artistSongsRecyclerView.visibility = View.VISIBLE
        }
    }

    fun setUpRecyclerView() {
        val adapter = SongsListAdapter(context, activity as ArtistActivity, mSongsList)
        artistSongsRecyclerView.adapter = adapter
        artistSongsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun loadSongs(artistName: String?): ArrayList<Song> {

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        var selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        selection += " AND "
        selection += MediaStore.Audio.Media.ARTIST + "=\"$artistName\""

        val cursor = context!!.contentResolver.query(uri, null, selection, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songDuration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)

                    val song = Song(songName, albumName, albumId, albumArtUri, artistName, songUrl, songDuration)

                    mSongsList.add(song)
                    Log.d(PlayerService.TAG, "Song: Song Added" + song)

                    //Song song = new Song(name, albumName, albumId, albumArtUri, artistName, url, duration);
                    //Instead, put the values in ContentValues values = new ContentValues() Obj.
                    //mSongs.add(song);

                } while (cursor.moveToNext())
                cursor.close()
                mSongsList.sortBy { song -> song.songName }
            }
        }
        return mSongsList
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)

    }
}
