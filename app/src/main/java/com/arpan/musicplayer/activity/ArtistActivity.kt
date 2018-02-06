package com.arpan.musicplayer.activity

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.view.View
import com.arpan.musicplayer.R
import com.arpan.musicplayer.adapter.HorizontalViewPagerAdapter
import com.arpan.musicplayer.fragment.ArtistDescriptionFragment_About
import com.arpan.musicplayer.fragment.ArtistDescriptionFragment_Albums
import com.arpan.musicplayer.fragment.ArtistDescriptionFragment_Songs
import com.arpan.musicplayer.service.PlayerService
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_artists.*
import kotlinx.android.synthetic.main.fragment_music_controller.*

class ArtistActivity : AppCompatActivity() {

    private val mFragmentArray : ArrayList<Fragment> = ArrayList()
    private val mVerticalFragmentArrat : ArrayList<Fragment> = ArrayList()

    private lateinit var mCurrentArtistName: String

    var mBound = false

    lateinit var mPlayerService: PlayerService

    private val mServiceConnection : ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as PlayerService.LocalBinder
            mPlayerService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artists)

        bindService(Intent(this, PlayerService::class.java), mServiceConnection, Service.BIND_AUTO_CREATE)

        mCurrentArtistName = intent.getStringExtra("ARTIST_NAME")

        artistNameLabel_ArtistActivity.text = mCurrentArtistName

//        Toast.makeText(this, "ArtistName: " + mCurrentArtistName, Toast.LENGTH_SHORT).show()

        setupViewPager()

        setupSlidingPanel()

    }

    private fun setupViewPager() {

        mFragmentArray.add(ArtistDescriptionFragment_About.newInstance("ABOUT", mCurrentArtistName))
        mFragmentArray.add(ArtistDescriptionFragment_Songs.newInstance("SONGS", mCurrentArtistName))
        mFragmentArray.add(ArtistDescriptionFragment_Albums.newInstance("ALBUMS", mCurrentArtistName))

        val adapter = HorizontalViewPagerAdapter(supportFragmentManager, mFragmentArray)

        viewPager_artistActivity.adapter = adapter
    }

    private fun setupSlidingPanel() {
        slidingPanel.addPanelSlideListener(object : SlidingUpPanelLayout.SimplePanelSlideListener() {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                collapsed_holder.alpha = 1F - 3 * slideOffset
                expanded_holder.alpha = 3 * slideOffset
            }

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {

                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    expanded_holder.visibility = View.VISIBLE
                    collapsed_holder.visibility = View.INVISIBLE
                }

                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    collapsed_holder.visibility = View.VISIBLE
                    expanded_holder.visibility = View.INVISIBLE
                }

                if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    expanded_holder.visibility = View.VISIBLE
                    collapsed_holder.visibility = View.VISIBLE
                }
            }
        })

        slidingPanel.panelHeight = MainActivity.dp2Px(60F, this)!!.toInt()
        slidingPanel.shadowHeight = 10
    }
}
