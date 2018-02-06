package com.arpan.musicplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arpan.musicplayer.GlideApp
import com.arpan.musicplayer.R
import com.arpan.musicplayer.activity.ArtistActivity
import com.arpan.musicplayer.activity.MainActivity
import com.arpan.musicplayer.model.Artist
import kotlinx.android.synthetic.main.list_item_artist.view.*

// Created on 12/19/2017

class ArtistListAdapter (
        val mainActivity: MainActivity,
        val mContext: Context?,
        val mArtistList: ArrayList<Artist>) : RecyclerView.Adapter<ArtistListAdapter.ViewHolder>() {

    private val TAG: String? = ArtistListAdapter::class.java.simpleName

    lateinit var mCurrentArtist: Artist

    private val API_KEY = "8e71dd998e907fa081fdf059691e10c"

    private lateinit var mCallbackHandler: HandleCallbackFromAdapter

    interface HandleCallbackFromAdapter {
        fun handleClick(artist: Artist)
    }

    fun setAdapterCallback(callbackFromAdapter: HandleCallbackFromAdapter) {
        mCallbackHandler = callbackFromAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item_artist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = mArtistList[position]
        mCurrentArtist = artist
        holder.bindViews(artist) //FIXME: DO A LAST.FM REQUEST AND GET ARTIST IMAGE
    }

    override fun getItemCount(): Int {
        return mArtistList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, MainActivity.FetchUriResponse  {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
//            mCallbackHandler.handleClick(mCurrentArtist)
            val intent = Intent(mContext, ArtistActivity::class.java)
            intent.putExtra("ARTIST_NAME", mArtistList[adapterPosition].artistName)
            mContext!!.startActivity(intent)
        }

        fun bindViews(artist: Artist) {
            itemView.artistNameLabel_artistList.text = artist.artistName
            val fetchTask = FetchArtistImageUrlTask()
            fetchTask.delegate = this@ViewHolder
            fetchTask.execute(mCurrentArtist)
        }

        override fun processFinish(artistImageUri: Uri?) {

            mainActivity.runOnUiThread {
                GlideApp
                        .with(mContext)
                        .load(artistImageUri)
                        .placeholder(R.mipmap.hqdefault)
                        .into(itemView.artistArt_artistList)
            }
        }

        @SuppressLint("StaticFieldLeak")
        inner class FetchArtistImageUrlTask : AsyncTask<Artist, Void, Void>() {

            var delegate: MainActivity.FetchUriResponse? = null

            override fun doInBackground(vararg artists: Artist?): Void? {

                val artist = artists[0]

                val artistNameWithoutSpaces = artist?.artistName?.replace("\\s", "+")

                val response = khttp.get("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=$artistNameWithoutSpaces&api_key=$API_KEY&format=json")
                val artistObj = response.jsonObject

                val imgUri: Uri? = try {
                    val imgLink = artistObj.getJSONObject("artist").getJSONArray("image").getJSONObject(3).getString("#text")
                    Log.d(TAG, "artist: ${artist?.artistName} imgLink: $imgLink")
                    Uri.parse(imgLink)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    val errorCode = artistObj.getInt("error")
                    val message = artistObj.getString("message")
                    Log.d(TAG, "artistName: ${artist?.artistName} errorCode: $errorCode message: $message")
                    null
                }

                delegate?.processFinish(imgUri)
                return null
            }
        }
    }
}