package com.example.rsesapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class AddActivity : AppCompatActivity() {
    val USER_KEY = "Video"
    val data_base: DatabaseReference = FirebaseDatabase.getInstance().getReference(USER_KEY)
    var storage_reference_video: StorageReference? = null
    var storage_reference_preview: StorageReference? = null

    var video_view: VideoView? = null
    var uploadUriVideo: Uri? = null
    var uploadUriPreview: Uri? = null
    var video_uri:Uri? = null
    var image: Bitmap? = null
    var select_image:Long? = null

    var radio_group_year: RadioGroup? = null
    var radio_group_type: RadioGroup? = null
    var edit_name: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        Toast.makeText(this, "Добавить видео", Toast.LENGTH_SHORT).show()

        val button_pick_video = findViewById<Button>(R.id.pick_video)
        val button_save = findViewById<Button>(R.id.add)
        val button_back = findViewById<ImageButton>(R.id.back)
        radio_group_year = findViewById(R.id.radioGroupYear)
        radio_group_type = findViewById(R.id.radioGroupType)
        edit_name = findViewById(R.id.name_form)
        video_view = findViewById(R.id.tvVideo)
        storage_reference_video = FirebaseStorage.getInstance().getReference("VideoDB")
        storage_reference_preview = FirebaseStorage.getInstance().getReference("PreviewDB")

        button_back.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            val anim_update = AnimationUtils.loadAnimation(this, R.anim.alpha)
            button_back.startAnimation(anim_update)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        button_save.setOnClickListener{

            if(edit_name?.editText?.text?.length!! > 0 && video_uri != null)
            {
                video_view?.pause()
                val mRetriever = MediaMetadataRetriever()
                mRetriever.setDataSource(this@AddActivity, video_uri)

                select_image = video_view?.currentPosition?.toLong()!! * 1000
                image = mRetriever.getFrameAtTime(select_image!!, MediaMetadataRetriever.OPTION_CLOSEST)
                upLoadVideo()
                upLoadPreview()
                intent = Intent(this, MainActivity::class.java).apply{}
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            else
            {
                Toast.makeText(this, "Придумайте название и выберите видео", Toast.LENGTH_SHORT).show()
                val anim = AnimationUtils.loadAnimation(this, R.anim.alpha)
                edit_name?.startAnimation(anim)
            }

        }


        button_pick_video.setOnClickListener{
            pick_image()
        }
    }

    companion object {
        //image pick code
        private val VIDEO_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    fun pick_image(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted
                pickImageFromGallery();
            }
        } else {
            //system OS is < Marshmallow
            pickImageFromGallery();
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent, VIDEO_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        video_view = findViewById<VideoView>(R.id.tvVideo)
        if (resultCode == Activity.RESULT_OK && requestCode == VIDEO_PICK_CODE) {
            video_uri = data?.data


            //Toast.makeText(this, "$select_image", Toast.LENGTH_SHORT).show()

            val mediaControler = MediaController(this)
            video_view?.setMediaController(mediaControler)
            video_view?.setVideoURI(video_uri)
            video_view?.start()

        }
    }

    fun save_user(){
        val id = data_base.push().key.toString()
        val name = edit_name?.editText?.text.toString()
        val year = GetYearType().getYear(radio_group_year!!)
        val type = GetYearType().getType(radio_group_type!!)

        val user = ContentVideo(id, name, year, type, uploadUriVideo.toString(), uploadUriPreview.toString())
        if(!name.isEmpty()) data_base.child(id).setValue(user)
        else
        {
            Toast.makeText(this, "Заполните поле", Toast.LENGTH_SHORT).show()
            edit_name?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
        }

    }

    fun upLoadVideo()
    {
        val mRef: StorageReference = storage_reference_video!!.child(System.currentTimeMillis().toString()+"_"+edit_name?.editText?.text.toString().replace(" ", "_"))
        val uploadTask = mRef.putFile(video_uri!!)

        val task: Task<Uri> = uploadTask.continueWithTask(object :
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                return mRef.downloadUrl
            }
        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(p0: Task<Uri>)
            {
                uploadUriVideo = p0.result
                save_user()
            }
        })
    }

    fun upLoadPreview()
    {
        val mRef: StorageReference = storage_reference_preview!!.child(System.currentTimeMillis().toString()+"_"+edit_name?.editText?.text.toString().replace(" ", "_")+"_prev")
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val uploadTask:UploadTask = mRef.putBytes(baos.toByteArray())

        val task: Task<Uri> = uploadTask.continueWithTask(object :
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                return mRef.downloadUrl
            }
        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(p0: Task<Uri>)
            {
                uploadUriPreview = p0.result
            }
        })

    }
    override fun onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}