package com.example.rsesapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AdapterImage(var data_list:ArrayList<ContentVideo>, var clickListener:OnItemLongClickListener, context: Context, activity:MainActivity) : RecyclerView.Adapter<AdapterImage.MyViewHolder>(){
    val adapter_context = context
    val adapter_activity = activity

    val USER_KEY = "Image"

    val storage_reference = FirebaseStorage.getInstance().getReference("VideoDB")

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val text_name = view.findViewById<TextView>(R.id.text_name)
        val text_year = view.findViewById<TextView>(R.id.year)
        val botton_download = view.findViewById<ImageButton>(R.id.video_download)
        val viewR = view

        fun bind(item:ContentVideo, clickListener:OnItemLongClickListener)
        {
            Glide.with(adapter_context).load(item.preview_uri).into(imageView)
            text_name.text = item.name
            text_year.text = item.year
            botton_download.setVisibility(View.GONE)


            itemView.setOnClickListener{
                val path_list = ArrayList<String>()
                data_list.forEach { item ->
                    path_list.add(item.video_uri)
                }
                Toast.makeText(adapter_context, "Просмотор фото", Toast.LENGTH_SHORT).show()
                val intent = Intent(adapter_context, ImageActivity::class.java).apply {
                    putExtra("path_list", path_list)
                    putExtra("path", item.video_uri)}
                adapter_context.startActivity(intent)
                adapter_activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

            val animate = AnimationUtils.loadAnimation(adapter_context, R.anim.alpha)
            viewR.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN ->
                            viewR.startAnimation(animate)
                    }

                    return v?.onTouchEvent(event) ?: true
                }
            })

            viewR.setOnLongClickListener(){
                clickListener.itemLongClick(item, adapterPosition, viewR)
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_video_view, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item  = data_list.get(position)
        holder.bind(item, clickListener)

    }

    override fun getItemCount(): Int {
        return data_list.size
    }

    fun remuve_view(pos: Int){
        /*Удаление визуального отображения*/
        data_list.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(0, data_list.size)
    }

    fun remove(item:ContentVideo){
        val data_base: DatabaseReference = FirebaseDatabase.getInstance().getReference(USER_KEY)
        data_base.child(item.id).removeValue()//Удаляю запись в БД

        val image_reference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.video_uri)
        image_reference.delete()//Удаляю файл их хранилища
    }

    fun refract(item:ContentVideo, new_item:ContentVideo){
        val data_base: DatabaseReference = FirebaseDatabase.getInstance().getReference(USER_KEY)
        data_base.child(item.id).setValue(new_item)
    }

    fun set_view(pos:Int, item:ContentVideo){
        /*Возвращение визуального отображения*/
        data_list.add(pos, item)
        notifyItemInserted(pos)
    }

    interface OnItemLongClickListener{
        fun itemLongClick(item:ContentVideo, position:Int, view: View){}
    }
}