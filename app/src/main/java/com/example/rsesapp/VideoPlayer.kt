package com.example.rsesapp

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView

class VideoPlayer : AppCompatActivity() {
    var simpleExoPlayer: SimpleExoPlayer? = null
    var flag = false
    var onDobleClickFlag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val playerView = findViewById<PlayerView>(R.id.player_view)
        val btnFullScrin = findViewById<ImageView>(R.id.bt_fullscreen)
        val close_video = findViewById<ImageView>(R.id.close_video)
        val nextVideo = findViewById<ImageView>(R.id.next)
        val prevVideo = findViewById<ImageView>(R.id.prev)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val path = intent.getCharSequenceExtra("path").toString()
        val pathList = intent.getCharSequenceArrayListExtra("path_list")
        val trackSelector = DefaultTrackSelector(this)

        //Toast.makeText(this, "${pathList?.get(0)}", Toast.LENGTH_LONG).show()

        val mediaItems = ArrayList<MediaItem>()
        pathList?.forEach {path ->
            mediaItems.add(MediaItem.fromUri(Uri.parse(path.toString())))
        }




        simpleExoPlayer = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        val mediaItem = MediaItem.fromUri(Uri.parse(path))
        val indexItem = mediaItems.indexOf(mediaItem)




        playerView.player = simpleExoPlayer
        val anim_alpha = AnimationUtils.loadAnimation(this , R.anim.alpha)

        btnFullScrin.setOnClickListener(object: View.OnClickListener{
            override fun onClick(view: View?) {
                if(flag){
                    btnFullScrin.setImageDrawable(resources.getDrawable(R.drawable.ic_fullscreen))
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    view?.startAnimation(anim_alpha)
                    flag = false
                }
                else{
                    btnFullScrin.setImageDrawable(resources.getDrawable(R.drawable.ic_fullscreen_exit))
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    view?.startAnimation(anim_alpha)
                    flag = true
                }
            }
        })
        close_video.setOnClickListener{
            intent = Intent(this, MainActivity::class.java).apply{}
            startActivity(intent)
            val anim_update = AnimationUtils.loadAnimation(this, R.anim.alpha)
            close_video.startAnimation(anim_update)
            finish()
        }

        simpleExoPlayer?.setMediaItems(mediaItems, indexItem, 0)//Загружаю плейлист

        simpleExoPlayer?.prepare()
        simpleExoPlayer?.play()


        nextVideo.setOnClickListener{
            simpleExoPlayer?.next()
        }
        prevVideo.setOnClickListener{
            simpleExoPlayer?.previous()
        }
    }

    override fun onPause(){
        super.onPause()
        simpleExoPlayer?.playWhenReady = false
        simpleExoPlayer?.playbackState
        simpleExoPlayer?.release()// Остановка плеера
    }

    override fun onRestart() {
        super.onRestart()
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.playbackState

    }

    override fun onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
