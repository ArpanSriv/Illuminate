package com.arpan.musicplayer.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.*
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.arpan.musicplayer.GlideApp
import com.arpan.musicplayer.R
import com.arpan.musicplayer.activity.MainActivity
import com.arpan.musicplayer.adapter.ArtistListAdapter
import com.arpan.musicplayer.model.Artist
import kotlinx.android.synthetic.main.fragment_artist_list.*
import com.arpan.musicplayer.rest.AutoFitGridLayoutManager
import kotlinx.android.synthetic.main.artist_list_item.*
import java.io.IOException
import java.io.InputStream
import khttp.get
import org.json.JSONObject

// Created on 19/12/2017

class ArtistListFragment: Fragment(), ArtistListAdapter.HandleCallbackFromAdapter {

    private val MOVE_DEFAULT_TIME = 1000L
    private val FADE_DEFAULT_TIME = 300L

    lateinit var sceneA : Scene
    lateinit var sceneB : Scene

//    var ABOUT_ARTIST = resources.getString(R.string.lorem_ipsum)

    //TODO IMPLEMENT BACK PRESSED ACTION

    override fun handleClick(artist: Artist) {

        val detailsBundle = Bundle()
//        detailsBundle.putString("Details", ABOUT_ARTIST)

        val fm = fragmentManager
        val previousFragment = fm!!.findFragmentById(R.id.RootFrame)
        val nextFragment = ArtistDescriptionFragment()
        val fragmentTransaction = fm.beginTransaction()

        val exitFade = Fade()
        exitFade.duration = FADE_DEFAULT_TIME
        previousFragment.exitTransition = exitFade

        val enterTransitionSet = TransitionSet()
        enterTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
        enterTransitionSet.duration = MOVE_DEFAULT_TIME
        enterTransitionSet.startDelay = FADE_DEFAULT_TIME
        nextFragment.sharedElementEnterTransition = enterTransitionSet

        val enterTransitionSetForDescription = TransitionSet()
        val slideDownTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.slide_top)
        slideDownTransition.duration = FADE_DEFAULT_TIME
        slideDownTransition.addTarget(R.id.imageViewArtistDescription)
        enterTransitionSet.addTransition(slideDownTransition)

        val enterFade = Fade()
        enterFade.addTarget(R.id.floatingActionButtonArtistDescription)
        enterFade.startDelay = MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME
        enterFade.duration = FADE_DEFAULT_TIME
        enterTransitionSetForDescription.addTransition(enterFade)

        val slideUpTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.slide_bottom)
        slideUpTransition.duration = FADE_DEFAULT_TIME
        slideUpTransition.addTarget(R.id.artistTabsViewPager)

        enterTransitionSetForDescription.addTransition(slideUpTransition)
        enterTransitionSet.ordering = TransitionSet.ORDERING_SEQUENTIAL

        nextFragment.enterTransition = enterTransitionSetForDescription
        fragmentTransaction.addSharedElement(artistArt_artistList, artistArt_artistList.transitionName)
        nextFragment.arguments = detailsBundle
        fragmentTransaction.replace(R.id.RootFrame, nextFragment)
        fragmentTransaction.commitAllowingStateLoss()

        //TODO("TRANSFER DATA BETWEEN FRAGMENTS")

    }

    private var mArtistsList: ArrayList<Artist> = ArrayList()
    private var mTopArtistsList: ArrayList<Artist> = ArrayList(9)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        while (ContextCompat.checkSelfPermission((activity as MainActivity),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {}

        artistFragmentHolder.isSmoothScrollingEnabled = true

//        setUpTransitions()
        try {
            val inputStream: InputStream = (activity as MainActivity).assets.open("img/artist_header.jpg")
            val drawable: Drawable = Drawable.createFromStream(inputStream, null)
            artistFragmentHeaderIV.setImageDrawable(drawable)
            val inputStream2: InputStream = (activity as MainActivity).assets.open("img/artist_footer.jpeg")
            val drawable2: Drawable = Drawable.createFromStream(inputStream2, null)
            artistFragmentFooterIV.setImageDrawable(drawable2)
        } catch (exception: IOException) {
//            Toast.makeText(context, "Not Found.", Toast.LENGTH_SHORT).show()
            Log.d("Error", "Exception: " + exception.stackTrace)
        }

        artistFragmentHolder.smoothScrollTo(0, 0)

        val loadArtistsTask = LoadArtistsTask()
        loadArtistsTask.execute()
    }

//    private fun setUpTransitions() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    private inner class LoadArtistsTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
//            val mainActivity = activity as MainActivity
            loadArtists()
            return null
        }

        override fun onPreExecute() {
            loadingProgressBar_artists.visibility = View.VISIBLE
            artistRecyclerView.visibility = View.INVISIBLE
            loadingProgressBar_artists.isIndeterminate = true
        }

        override fun onPostExecute(result: Void?) {
            loadingProgressBar_artists.visibility = View.GONE
            setUpRecyclerView()
            artistRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        val adapter = ArtistListAdapter(context, mTopArtistsList)
        adapter.setAdapterCallback(this@ArtistListFragment)
        artistRecyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(context, 3, LinearLayout.VERTICAL, false)
        artistRecyclerView.layoutManager = layoutManager
        artistRecyclerView.isClickable = false

        artistRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
                artistFragmentHolder.onInterceptTouchEvent(e)
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
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
//                    val noOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS))
////                    val albumId = cursor.getLong(cursor.getColumnIndex(ALBUM_ID))
//                    val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST))
//                    val albumUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART))
////                    val albumArtUri = Uri.parse(albumUri)

                    val artist = Artist(artistName, null)
                    mArtistsList.add(artist)
                } while (cursor.moveToNext())

                //Add items top Artists List

                for (i in 0..8) {
                    val artist = mArtistsList[i]
                    val response = get("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + artist.artistName +  "&api_key=8e71dd998e907fa081fdf059691e10c9&format=json")
                    val jsonArray = response.jsonArray
                    //TODO("CONTINUE HERE")
                    val imageURL = imageObject.
                    mTopArtistsList.add(mArtistsList[i])
                }

                cursor.close()
            }
        }
    }


}