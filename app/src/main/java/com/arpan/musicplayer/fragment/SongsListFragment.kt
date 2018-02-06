package com.arpan.musicplayer.fragment

import android.content.ContentUris
import android.content.SharedPreferences
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
import com.arpan.musicplayer.activity.MainActivity
import com.arpan.musicplayer.R
import com.arpan.musicplayer.adapter.SongsListAdapter
import com.arpan.musicplayer.model.Song

import kotlinx.android.synthetic.main.fragment_songs_list.*

class SongsListFragment : Fragment() {

    private val TAG = SongsListFragment::class.java.simpleName

    private var mSharedPreferences: SharedPreferences? = null
    private val FIRST_TIME = "FIRST_TIME"

    private var mSongsList : ArrayList<Song> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_songs_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val editor = mSharedPreferences?.edit()
        editor?.putBoolean(FIRST_TIME, true)
        editor?.apply()

        val task = LoadTask()
        task.execute()

    }

    private inner class LoadTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            val mainActivity = activity as MainActivity
            mSongsList = mainActivity.getSongListFromService()
            return null
        }

        override fun onPreExecute() {
            loadingProgressBar_songs.visibility = View.VISIBLE
            songsRecyclerView.visibility = View.INVISIBLE
            loadingProgressBar_songs.isIndeterminate = true
        }

        override fun onPostExecute(result: Void?) {
            loadingProgressBar_songs.visibility = View.GONE
            setUpRecyclerView()
            songsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        val adapter = SongsListAdapter(context, activity as MainActivity, mSongsList)
        songsRecyclerView.adapter = adapter
        songsRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    private fun loadSongsInArrayList() {

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"

        val cursor = activity!!.contentResolver.query(uri, null, selection, null, null)

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

                    mSongsList!!.add(song)
                    Log.d(TAG, "Song: Song Added" + song)

                    //Song song = new Song(name, albumName, albumId, albumArtUri, artistName, url, duration);
                    //Instead, put the values in ContentValues values = new ContentValues() Obj.
                    //mSongs.add(song);

                } while (cursor.moveToNext())

                cursor.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSongsList.clear()
    }
}
