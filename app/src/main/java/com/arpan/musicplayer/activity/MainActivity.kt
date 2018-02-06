package com.arpan.musicplayer.activity

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.View
import com.arpan.musicplayer.R
import com.arpan.musicplayer.service.PlayerService

import kotlinx.android.synthetic.main.activity_main.*
import com.arpan.musicplayer.adapter.HorizontalViewPagerAdapter
import com.arpan.musicplayer.adapter.VerticalViewPagerAdapter
import com.arpan.musicplayer.fragment.*
import com.arpan.musicplayer.model.Song
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.fragment_music_controller.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), MusicControllerFragment.ActivityListener {

    companion object {
        val PLAY = 1
        val PAUSE = 2
        val STOP = 0
        val RESUME = 4

        fun px2Dp(px: Float, ctx: Context): Float {
            return px / ctx.resources.displayMetrics.density
        }

        fun dp2Px(dp: Float?, ctx: Context?): Float? {
            return if (dp != null) {
                dp * ctx?.resources?.displayMetrics?.density!!
            } else {
                null
            }
        }

    }

    interface FetchUriResponse {
        fun processFinish(artistImageUri: Uri?)
    }

    lateinit var mHorizontalViewPagerAdapter: HorizontalViewPagerAdapter
    lateinit var mVerticalViewPagerAdapter: VerticalViewPagerAdapter
    lateinit var mPlayerService: PlayerService
    private lateinit var mSongsList: ArrayList<Song>

    var mBound: Boolean = false

    private val mHandler = Handler()
    private lateinit var mToolbar: android.support.v7.widget.Toolbar
    private val mServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val binder = iBinder as PlayerService.LocalBinder
            mPlayerService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolbar = findViewById(R.id.toolbar)
        mToolbar.title = resources.getString(R.string.app_name)
        setSupportActionBar(mToolbar)

//        actionBar.setTitle("ILLUMINATE")

        requestPermission()

        bindService(Intent(this, PlayerService::class.java), mServiceConnection, Service.BIND_AUTO_CREATE)

        initViews()

        setupSlidingPanel()

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> supportActionBar?.title = "Library".toUpperCase()
                    1 -> supportActionBar?.title = "Artists".toUpperCase()
                    2 -> supportActionBar?.title = "Albums".toUpperCase()

                }
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (slidingPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

        else
            super.onBackPressed()
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

        slidingPanel.panelHeight = dp2Px(60F, this)!!.toInt()
        slidingPanel.shadowHeight = 10
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            val MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 0

            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else requestPermissions(this@MainActivity,
                    arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE)
        }
    }

    private fun initViews() {
        val horizontalFragments: ArrayList<Fragment> = ArrayList()

        horizontalFragments.add(SongsListFragment())
        horizontalFragments.add(HolderFragment())
        horizontalFragments.add(AlbumListFragment())

        mHorizontalViewPagerAdapter = HorizontalViewPagerAdapter(supportFragmentManager, horizontalFragments)
        viewPager.adapter = mHorizontalViewPagerAdapter

        val verticalFragments: ArrayList<Fragment> = ArrayList()

        verticalFragments.add(MusicControllerFragment())
        verticalFragments.add(SongsListFragment())

        mVerticalViewPagerAdapter = VerticalViewPagerAdapter(supportFragmentManager, verticalFragments)
        viewPagerVertical.adapter = mVerticalViewPagerAdapter
    }

    private fun playMusic(song: Song, playInstantly : Boolean) {
        if (mBound) {
            mPlayerService.setCurrentSong(song)
            setCallBackInService(mVerticalViewPagerAdapter.getRegisteredFragment(viewPagerVertical.currentItem) as MusicControllerFragment)
            mPlayerService.playMusic()
//            mPlayerService.serviceCallbacks.updateViewsByStatusCode(MainActivity.PLAY, song)
            setSeekBarProgress(mPlayerService.getCurrentSong())


            //TODO("Check to change the present fragment")

        }
    }

    private fun setSeekBarProgress(song: Song?) {
        if (mBound) {
            val duration = song!!.duration
            val time = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong())))
            maxLabel!!.text = time

            val maxProgress = song.duration / 1000
            seekBar.max = maxProgress
            val runnable = object : Runnable {
                override fun run() {

                    val currentProgress = mPlayerService!!.mMediaPlayer.currentPosition / 1000
                    seekBar.progress = currentProgress

                    val elapsed = mPlayerService!!.mMediaPlayer.currentPosition

                    val time = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(elapsed.toLong()),
                            TimeUnit.MILLISECONDS.toSeconds(elapsed.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsed.toLong())))

                    elapsedLabel!!.text = time

                    if (currentProgress != maxProgress) mHandler.postDelayed(this, 1000)
                }
            }
            this@MainActivity.runOnUiThread(runnable)
        }
    }

    //Interface for Fragments to Communicate with service
    fun seekMusic(progress : Int) {
        if (mBound) {
            mPlayerService!!.mMediaPlayer.seekTo(progress * 1000)
        }
    }

    fun setCallBackInService(callbacks: PlayerService.ServiceCallbacks) {
        if (mBound) {
            mPlayerService.setCallbacks(callbacks)
        }
    }

    fun getCurrentPlayerStatusFromService() : Int {
        if (mBound) return mPlayerService.getCurrentPlayerStatus()
        else return PlayerService.STOPPED
    }

    fun controlPlayer(statusCode : Int, song: Song?, currentIndex: Int?) {
        if (mBound) {
            when (statusCode) {
                PLAY -> {
                    playMusic(song!!, true)
                    if (currentIndex != null) mPlayerService.mCurrentIndex = currentIndex
                }
                PAUSE -> mPlayerService.pauseMusic()
                RESUME -> mPlayerService.resumeMusic()
                STOP -> mPlayerService.stopMusic()
            }
        }
    }

    fun getFragmentFromHorizontalViewPager(index: Int): Fragment = mHorizontalViewPagerAdapter.getItem(index)

    fun getSongListFromService() : ArrayList<Song> {

        while (!mBound) {}

        mSongsList = mPlayerService.loadSongs()

        return mSongsList
    }

    fun notifyPager() {
        mHorizontalViewPagerAdapter.notifyDataSetChanged()
    }

}
