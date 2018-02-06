package com.arpan.musicplayer.fragment

import android.content.Context

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arpan.musicplayer.R
import com.arpan.musicplayer.adapter.AlbumListAdapter
import com.arpan.musicplayer.model.Album
import kotlinx.android.synthetic.main.fragment_artist_details_artist_albums.*
import kotlinx.android.synthetic.main.fragment_artist_list.*

class ArtistDescriptionFragment_Albums : Fragment() {

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ArtistDescriptionFragment_Albums().apply {
                    arguments = Bundle().apply {
                        putString(FRAGMENT_TITLE, param1)
                        putString(CURRENT_ARTIST, param2)
                    }
                }
    }
    // TODO: Rename parameter arguments, choose names that match
   // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private val FRAGMENT_TITLE = "FRAGMENT_TITLE"
    private val CURRENT_ARTIST = "CURRENT_ARTIST"

    private val mAlbumsList = ArrayList<Album>()

    private var fragmentTitle: String? = null
    private var currentArtistName: String? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentTitle = it.getString(FRAGMENT_TITLE)
            currentArtistName = it.getString(CURRENT_ARTIST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_details_artist_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val loadAlbumsTask = LoadAlbumsTask()
        loadAlbumsTask.execute(currentArtistName)
    }

    inner class LoadAlbumsTask: AsyncTask<String, Void, Void>() {

        override fun onPreExecute() {
            artistAlbumsProgressBar.visibility = View.VISIBLE
            albumsListRecyclerView_artistActivity.visibility = View.INVISIBLE
        }

        override fun doInBackground(vararg artistNames: String?): Void? {
            loadAlbums(artistNames[0])
            return null
        }

        override fun onPostExecute(result: Void?) {
            artistAlbumsProgressBar.visibility = View.GONE
            setUpRecyclerView()
            albumsListRecyclerView_artistActivity.visibility = View.VISIBLE
        }
    }

    fun setUpRecyclerView() {
        val adapter = AlbumListAdapter(context, mAlbumsList)
        albumsListRecyclerView_artistActivity.adapter = adapter
        albumsListRecyclerView_artistActivity.layoutManager = LinearLayoutManager(context)
    }

    fun loadAlbums(artistName: String?) {

        val projection = arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.NUMBER_OF_SONGS)
        val sortOrder = MediaStore.Audio.Media.ALBUM + " ASC"
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Albums.ARTIST} = \"$artistName\""
        val cursor = activity!!.contentResolver.query(uri, projection, selection, null, sortOrder)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM))
                    val noOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS))
//                    val albumId = cursor.getLong(cursor.getColumnIndex(ALBUM_ID))
                    val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST))
                    val albumUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART))
//                    val albumArtUri = Uri.parse(albumUri)

                    val album = Album(albumName, null, artistName, "ABC")
                    mAlbumsList.add(album)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
    }

    // TODO: Rename method and hook method into UI event
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
