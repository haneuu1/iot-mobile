package com.example.smartdoor

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mylib.openapi.smartdoorapi.data.RecordingDataItem
import com.google.android.exoplayer2.ExoPlayerFactory
//import com.norulab.exofullscreen.MediaPlayer
//import com.norulab.exofullscreen.preparePlayer
//import com.norulab.exofullscreen.setSource
import kotlinx.android.synthetic.main.activity_video_play.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoPlayActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    val baseUrl = "http://172.30.1.126:8000/media/"
    lateinit var URL: String

    private lateinit var simpleExoPlayer: SimpleExoPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        val data = intent.getSerializableExtra(VideoListActivity.KEY_DATA) as RecordingDataItem
        URL = baseUrl + data.video_root.toString()

//        val playerView: PlayerView = findViewById(R.id.playerView)
//        val simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
//        playerView.player = simpleExoPlayer
//
//        val mediaItem = MediaItem.fromUri(Uri.parse(url))
//
//        val userAgent = Util.getUserAgent(this, this.applicationInfo.name)
//        val factory = DefaultDataSourceFactory(this, userAgent)
//        val progressiveMediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
//
//        simpleExoPlayer.setMediaSource(progressiveMediaSource)
//        simpleExoPlayer.prepare()
//        simpleExoPlayer.play()

//        MediaPlayer.initialize(applicationContext)
//        MediaPlayer.exoPlayer?.preparePlayer(playerView, true)
//        MediaPlayer.exoPlayer?.setSource(applicationContext, url)
//        MediaPlayer.startPlayer()

    }

    override fun onStart() {
        super.onStart()

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        videoPlayerView.player = simpleExoPlayer

        var factory: DataSource.Factory = DefaultDataSourceFactory(this, "SmartDoorExoPlayer")
        var mediaSource: ProgressiveMediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(Uri.parse(URL))

        simpleExoPlayer.prepare(mediaSource)
        simpleExoPlayer.playWhenReady = true

        Log.d(TAG, URL)
    }

    override fun onStop() {
        super.onStop()

        videoPlayerView.player = null
        simpleExoPlayer.release()
    }



//    public override fun onPause() {
//        super.onPause()
//        MediaPlayer.pausePlayer()
//    }
//
//    public override fun onDestroy() {
//        MediaPlayer.stopPlayer()
//        super.onDestroy()
//    }

}