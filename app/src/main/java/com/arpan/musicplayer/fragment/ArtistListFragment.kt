package com.arpan.musicplayer.fragment

import android.Manifest
import android.content.Intent
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
import com.arpan.musicplayer.activity.ArtistActivity
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

    private val TAG = ArtistListFragment::class.java.simpleName

    private val MOVE_DEFAULT_TIME = 1000L
    private val FADE_DEFAULT_TIME = 300L

    private val API_KEY = "8e71dd998e907fa081fdf059691e10c9"

    lateinit var sceneA : Scene
    lateinit var sceneB : Scene

//    var ABOUT_ARTIST = resources.getString(R.string.lorem_ipsum)

    //TODO IMPLEMENT BACK PRESSED ACTION

    override fun handleClick(artist: Artist) {

        val detailsBundle = Bundle()
//        detailsBundle.putString("Details", ABOUT_ARTIST)

        val fm = fragmentManager
        val previousFragment = fm!!.findFragmentById(R.id.RootFrame)
        val nextFragment = AllArtistsFragment()
        val fragmentTransaction = fm.beginTransaction()

        val exitFade = Fade()
        exitFade.duration = FADE_DEFAULT_TIME
        previousFragment.exitTransition = exitFade

        val enterTransitionSet = TransitionSet()
        enterTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
        enterTransitionSet.duration = MOVE_DEFAULT_TIME
//        enterTransitionSet.startDelay = FADE_DEFAULT_TIME
        nextFragment.sharedElementEnterTransition = enterTransitionSet
//
//        val enterTransitionSetForAllArtists = TransitionSet()
//        val fade = TransitionInflater.from(context).inflateTransition(android.R.transition.slide_top)
//        slideDownTransition.duration = FADE_DEFAULT_TIME
//        slideDownTransition.addTarget(R.id.imageViewArtistDescription)
//        enterTransitionSet.addTransition(slideDownTransition)

        val enterFade = Fade()
        enterFade.addTarget(R.id.allArtistsLabel)
        enterFade.addTarget(R.id.allArtistsRecyclerView)
//        enterFade.startDelay = MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME
        enterFade.duration = FADE_DEFAULT_TIME
//        enterTransitionSetForDescription.addTransition(enterFade)

//        val slideUpTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.slide_bottom)
//        slideUpTransition.duration = FADE_DEFAULT_TIME
//        slideUpTransition.addTarget(R.id.artistTabsViewPager)

//        enterTransitionSetForDescription.addTransition(slideUpTransition)
        enterTransitionSet.ordering = TransitionSet.ORDERING_SEQUENTIAL

        nextFragment.enterTransition = enterTransitionSet
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

        browseAllArtistsLabel.setOnClickListener {
            performTransition()
//            val artistActivityIntent = Intent(context, ArtistActivity::class.java)
//            startActivity(artistActivityIntent)
        }

        artistFragmentFooterIV.setOnClickListener {
//            val artistActivityIntent = Intent(context, ArtistActivity::class.java)
//            startActivity(artistActivityIntent)
        }
    }

    private fun performTransition() {
        val detailsBundle = Bundle()
//        detailsBundle.putString("Details", ABOUT_ARTIST)

        val fm = fragmentManager
        val previousFragment = fm!!.findFragmentById(R.id.RootFrame)
        val nextFragment = AllArtistsFragment()
        val fragmentTransaction = fm.beginTransaction()

        val exitFade = Fade()
        exitFade.duration = FADE_DEFAULT_TIME
        previousFragment.exitTransition = exitFade

        val enterTransitionSet = TransitionSet()
        enterTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
        enterTransitionSet.duration = MOVE_DEFAULT_TIME
//        enterTransitionSet.startDelay = FADE_DEFAULT_TIME
        nextFragment.sharedElementEnterTransition = enterTransitionSet
//
//        val enterTransitionSetForAllArtists = TransitionSet()
//        val fade = TransitionInflater.from(context).inflateTransition(android.R.transition.slide_top)
//        slideDownTransition.duration = FADE_DEFAULT_TIME
//        slideDownTransition.addTarget(R.id.imageViewArtistDescription)
//        enterTransitionSet.addTransition(slideDownTransition)

        val enterFade = Fade()
        enterFade.addTarget(R.id.allArtistsLabel)
        enterFade.addTarget(R.id.allArtistsRecyclerView)
//        enterFade.startDelay = MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME
        enterFade.duration = FADE_DEFAULT_TIME
//        enterTransitionSetForDescription.addTransition(enterFade)

//        val slideUpTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.slide_bottom)
//        slideUpTransition.duration = FADE_DEFAULT_TIME
//        slideUpTransition.addTarget(R.id.artistTabsViewPager)

//        enterTransitionSetForDescription.addTransition(slideUpTransition)
        enterTransitionSet.ordering = TransitionSet.ORDERING_SEQUENTIAL

        nextFragment.enterTransition = enterTransitionSet
        fragmentTransaction.addSharedElement(artistArt_artistList, artistArt_artistList.transitionName)
        nextFragment.arguments = detailsBundle
        fragmentTransaction.replace(R.id.RootFrame, nextFragment)
        fragmentTransaction.commitAllowingStateLoss()

        //TODO("TRANSFER DATA BETWEEN FRAGMENTS")
    }

    private inner class LoadArtistsTask : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            loadingProgressBar_artists.visibility = View.VISIBLE
            artistRecyclerView.visibility = View.INVISIBLE
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            loadArtists()
            return null
        }

        override fun onPostExecute(result: Void?) {
            loadingProgressBar_artists.visibility = View.GONE
            setUpRecyclerView()
            artistRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {

        val adapter = ArtistListAdapter(activity as MainActivity, context, mTopArtistsList)
        adapter.setAdapterCallback(this@ArtistListFragment)
        artistRecyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(context, 3, LinearLayout.VERTICAL, false)
        artistRecyclerView.layoutManager = layoutManager
    }

    private fun loadArtists() {
        val projection = arrayOf(MediaStore.Audio.Artists.ARTIST)
        val sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC"
        val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
        val cursor = activity!!.contentResolver.query(uri, projection, null, null, sortOrder)
        var count = 0
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                    val artist = Artist(artistName, null, null)

                    if (count < 9) {

//                        if (artist.artistName == "Selena Gomez" || artist.artistName == "Demi Lovato" || artist.artistName == "Enrique Iglesias" || artist.artistName == "G-Eazy" || artist.artistName == "Chris Brown" || artist.artistName == "Taylor Swift" || artist.artistName == "ZAYN"|| artist.artistName == "NAV"|| artist.artistName == "Post Malone") {
                            mTopArtistsList.add(artist)
                            count++
//                        }
                    }

                    mArtistsList.add(artist)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
    }


}