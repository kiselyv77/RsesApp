package com.example.rsesapp

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


class ProfileActivity : AppCompatActivity() {
    var firebaseAuth: FirebaseAuth? = null
    var image_profile:CircleImageView? =  null
    var storage_reference_image: StorageReference? = null
    var user:String? = null
    var uploadUriImage:Uri? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        image_profile = findViewById(R.id.profile_image)

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener{
            back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
            val options = ActivityOptions.makeSceneTransitionAnimation(this, image_profile, "profile")
            startActivity(Intent(this, MainActivity::class.java), options.toBundle())
            finish()
        }

        val exit_account = findViewById<Button>(R.id.exit_account)
        exit_account.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val logo = findViewById<ImageView>(R.id.logo)
        logo.setOnClickListener{
            Toast.makeText(this, "Е бои", Toast.LENGTH_SHORT).show()
            logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
        }


        firebaseAuth = FirebaseAuth.getInstance()
        val text_user = findViewById<TextView>(R.id.user)
        user = firebaseAuth?.currentUser?.email
        text_user.text = user.toString()
        storage_reference_image = FirebaseStorage.getInstance().getReference("ProfileImageDB")
        storage_reference_image?.child("image_profile_"+user)?.downloadUrl?.addOnCompleteListener {
            Glide.with(this).load(it.result).into(image_profile!!)
        }

        image_profile?.setOnClickListener{
            image_profile?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
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
                requestPermissions(permissions, ProfileActivity.PERMISSION_CODE);
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
        intent.type = "image/*"
        startActivityForResult(intent, ProfileActivity.VIDEO_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            ProfileActivity.PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ProfileActivity.VIDEO_PICK_CODE)
        {
            val image_uri = data?.data
            image_profile?.setImageURI(image_uri)
            upLoadImage()
        }
    }

    fun upLoadImage()
    {
        val mRef: StorageReference = storage_reference_image!!.child("image_profile_" + user)
        val image = image_profile?.drawable?.toBitmap()
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        val uploadTask = mRef.putBytes(byteArray)

        val task: Task<Uri> = uploadTask.continueWithTask(object :
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                return mRef.downloadUrl

            }
        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(p0: Task<Uri>) {
                Toast.makeText(this@ProfileActivity, "Фото изменено", Toast.LENGTH_SHORT).show()
                uploadUriImage = p0.result
            }
        })
    }
}