package com.arpan.musicplayer.model

import android.net.Uri
import java.util.ArrayList

// Created on 12/19/2017

class Artist(
        val artistName: String,
        val albums: ArrayList<Album>?,
        var artistImageLink: Uri?) {

    override fun toString(): String {
        return "Artist Name: $artistName, Albums: ${albums!!.size}, ImageLink: $artistImageLink"
    }


}
