package com.example.rsesapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
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

class AddActivityImage : AppCompatActivity() {
    val USER_KEY = "Image"
    val data_base: DatabaseReference = FirebaseDatabase.getInstance().getReference(USER_KEY)
    var storage_reference_image: StorageReference? = null
    var image_view: ImageView? = null
    var uploadUriImage: Uri? = null
    var image_uri:Uri? = null
    var image: Bitmap? = null
    var radio_group_year: RadioGroup? = null
    var edit_name: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)
        Toast.makeText(this, "Добавить фото", Toast.LENGTH_SHORT).show()

        val button_pick_image = findViewById<Button>(R.id.pick_image)
        val button_save = findViewById<Button>(R.id.add)
        val button_back = findViewById<ImageButton>(R.id.back)
        radio_group_year = findViewById(R.id.radioGroupYear)
        edit_name = findViewById(R.id.name_form)
        image_view = findViewById(R.id.tvImage)
        storage_reference_image = FirebaseStorage.getInstance().getReference("ImageDB")

        button_back.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            button_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        button_save.setOnClickListener{
            if(edit_name?.editText?.text?.length!! > 0 && image_uri != null)
            {
                upLoadImage()
                intent = Intent(this, MainActivity::class.java).apply{}
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            else
            {
                Toast.makeText(this, "Придумайте название и выберите фото", Toast.LENGTH_SHORT).show()
                val anim = AnimationUtils.loadAnimation(this, R.anim.alpha)
                edit_name?.startAnimation(anim)
            }

        }

        button_pick_image.setOnClickListener{
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
                requestPermissions(permissions, AddActivityImage.PERMISSION_CODE);
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
        startActivityForResult(intent, AddActivityImage.VIDEO_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            AddActivityImage.PERMISSION_CODE -> {
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
        image_view = findViewById(R.id.tvImage)
        if (resultCode == Activity.RESULT_OK && requestCode == AddActivityImage.VIDEO_PICK_CODE)
        {
            image_uri = data?.data
            image_view?.setImageURI(image_uri)
        }
    }

    fun save_user(){
        val id = data_base.push().key.toString()
        val name = edit_name?.editText?.text.toString()
        val year = GetYearType().getYear(radio_group_year!!)
        val type = "IMAGE"

        val user = ContentVideo(id, name, year, type, uploadUriImage.toString(), uploadUriImage.toString())
        if(!name.isEmpty()) data_base.child(id).setValue(user)
        else
        {
            Toast.makeText(this, "Заполните поле", Toast.LENGTH_SHORT).show()
            edit_name?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha))
        }

    }

    fun upLoadImage()
    {
        val mRef: StorageReference = storage_reference_image!!.child(System.currentTimeMillis().toString()+"_"+edit_name?.editText?.text.toString().replace(" ", "_"))
        image = image_view?.drawable?.toBitmap()
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG,100, baos)
        val byteArray = baos.toByteArray()
        val uploadTask = mRef.putBytes(byteArray)

        val task: Task<Uri> = uploadTask.continueWithTask(object :
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                return mRef.downloadUrl

            }
        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(p0: Task<Uri>)
            {
                uploadUriImage = p0.result
                save_user()
            }
        })
    }

    override fun onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}