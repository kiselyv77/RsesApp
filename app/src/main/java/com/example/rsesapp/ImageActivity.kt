package com.example.rsesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val path_image = intent.getCharSequenceExtra("path").toString()
        val nameList = intent.getCharSequenceArrayListExtra("path_list")

        val pathList = ArrayList<String>()
        nameList?.forEach {name->
           pathList.add(name.toString())
        }

        val itemStart = nameList?.indexOf(path_image)

        val pager = findViewById<ViewPager2>(R.id.image_pager)
        pager.adapter = AdapterPager(pathList, this)
        pager.setCurrentItem(itemStart!!, false)



        //val image = findViewById<ImageView>(R.id.imageView)

        //Glide.with(this).load(path_image).into(image)
    }
    override fun onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}