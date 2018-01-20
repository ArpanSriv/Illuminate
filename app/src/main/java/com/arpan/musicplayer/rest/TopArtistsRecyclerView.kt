package com.arpan.musicplayer.rest

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import java.util.jar.Attributes

// Created on 21-01-2018
class TopArtistsRecyclerView(context: Context, attributes: AttributeSet): RecyclerView(context, attributes) {

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
            return false
    }

}