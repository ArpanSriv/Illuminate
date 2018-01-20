package com.arpan.musicplayer.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arpan.musicplayer.R

class ArtistDescriptionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }

    inner class LoadTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}