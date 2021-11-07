package com.example.rsesapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    var firebaseAuth: FirebaseAuth? = null
    var storage_reference_image: StorageReference? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth?.currentUser?.email
        val navView: BottomNavigationView = findViewById(R.id.nav_view)


        val navController = findNavController(R.id.nav_host_fragment)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val profile = findViewById<CircleImageView>(R.id.profile)
        storage_reference_image = FirebaseStorage.getInstance().getReference("ProfileImageDB")
        storage_reference_image?.child("image_profile_" + user)?.downloadUrl?.addOnCompleteListener {
            Glide.with(this).load(it.result).into(profile)
        }

        profile.setOnClickListener{
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
            val options = ActivityOptions.makeSceneTransitionAnimation(this, profile, "profile")
            startActivity(Intent(this, ProfileActivity::class.java), options.toBundle())
        }


        val add_button = findViewById<ImageButton>(R.id.add)
        add_button.setOnClickListener{
            val currentFragment = navController.currentDestination?.label
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
            if(currentFragment == "Фото")
            {
                startActivity(Intent(this, AddActivityImage::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            else
            {
                startActivity(Intent(this, AddActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

        }

        val save_videos_button = findViewById<ImageButton>(R.id.saves_video)
        save_videos_button.setOnClickListener{
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
            startActivity(Intent(this, ActivitySaveVideo::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        
        val info_botton = findViewById<ImageButton>(R.id.info)
        info_botton.setOnClickListener{
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
            startActivity(Intent(this, InfoActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        navView.setupWithNavController(navController) }

    override fun onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}