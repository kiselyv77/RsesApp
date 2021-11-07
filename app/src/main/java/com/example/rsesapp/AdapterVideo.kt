package com.example.rsesapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.*
import java.io.File

class AdapterVideo(var data_list:ArrayList<ContentVideo>, var clickListener:OnItemLongClickListener, context: Context, activity:MainActivity) : RecyclerView.Adapter<AdapterVideo.MyViewHolder>(){
    val adapter_context = context
    val adapter_activity = activity
    val path_storage = adapter_context.getExternalFilesDir(null)
    val storage_directory_video = File(path_storage, "VIDEO_DATA_RSES")
    val storage_directory_prev = File(path_storage, "PREV")



    val USER_KEY = "Video"



    val storage_reference = FirebaseStorage.getInstance().getReference("VideoDB")

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val text_name = view.findViewById<TextView>(R.id.text_name)
        val text_year = view.findViewById<TextView>(R.id.year)
        val botton_download = view.findViewById<ImageButton>(R.id.video_download)
        val progress_bar = view.findViewById<ProgressBar>(R.id.progress_bar)


        var path:String? = null




        val viewR = view
        fun bind(item:ContentVideo, clickListener:OnItemLongClickListener)
        {
            storage_directory_video.mkdirs() // создаю папку во внутреннем хранилище для загрузки туда видео
            storage_directory_prev.mkdir() // создаю папку во внутреннем хранилище для загрузки туда превьюшек
            val reference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.video_uri)
            val referencePrev: StorageReference =FirebaseStorage.getInstance().getReferenceFromUrl(item.preview_uri)

            if(reference.activeDownloadTasks.size != 0){ // если есть активные загрузки отслеживаю их прогресс даже если фрагмент был обновлен
                reference.activeDownloadTasks[0].addOnProgressListener {
                    val progress = it.bytesTransferred.toFloat() / it.totalByteCount.toFloat() * 100 // перевожу прогресс в проценты
                    progress_bar.setProgress(progress.toInt()) // показываю прогресс загрузки
                }.addOnCompleteListener{
                    //botton_download.setImageDrawable(adapter_context.resources.getDrawable(R.drawable.ic_ok)) // изменяю иконку когда загрузка завершена
                    botton_download.setImageDrawable(adapter_context.resources.getDrawable(R.drawable.ic_ok))
                    Toast.makeText(adapter_context, "Видео сохрнанено: ${item.name}", Toast.LENGTH_SHORT).show() // уведомляю кошда загрузка завершена
                    progress_bar.setProgress(0)// сбрасываю прогресс после завершения
                }
            }
            else
            {
                botton_download.setImageDrawable(adapter_context.resources.getDrawable(R.drawable.ic_download))// если нет загрузок то меняю икунку на исходную
            }
            val file_video = File(storage_directory_video, item.id+".mp4") // файл видео во внутреннем хранилище
            val file_prev = File(storage_directory_prev, item.id+".png")  // файл превью во внутреннем хранилище

            botton_download.setOnClickListener{
                botton_download.setOnClickListener {
                    Toast.makeText(adapter_context, "Это видео загружается", Toast.LENGTH_SHORT).show()
                    /*если пользователь нажмет на иконку уже загруженного видео, то уведомить его об этом*/
                }
                botton_download.setImageDrawable(adapter_context.resources.getDrawable(R.drawable.ic_ok))
                path = file_video.toString() // ставлю путь из внутреннего хранилища
                Toast.makeText(adapter_context, "Удаление не доступно когда есть активные загрузки!", Toast.LENGTH_SHORT).show()
                val animate = AnimationUtils.loadAnimation(adapter_context, R.anim.alpha)
                botton_download.startAnimation(animate)
                /*при нажатии на кнопку загрузки то начинается загрузка видео
                * отслеживаю прогрэсс в не обновленном фрагменте*/
                reference.getFile(file_video).addOnProgressListener {
                    val progress = it.bytesTransferred.toFloat() / it.totalByteCount.toFloat() * 100 // перевожу прогресс в проценты
                    progress_bar.setProgress(progress.toInt())  // показываю прогресс загрузки
                }.addOnCompleteListener{
                    notifyDataSetChanged()
                    Toast.makeText(adapter_context, "Видео сохрнанено: ${item.name}", Toast.LENGTH_SHORT).show()
                    botton_download.setOnClickListener {
                        Toast.makeText(adapter_context, "Это видео уже загруженно", Toast.LENGTH_SHORT).show()
                        /*если пользователь нажмет на иконку уже загруженного видео, то уведомить его об этом
                        * для не обновленного еще фрагмента*/
                    }
                    progress_bar.setProgress(0)// сбрасываю прогресс после завершения
                }
                referencePrev.getFile(file_prev) // Скачиваю превью
            }


            if(file_video.exists())// если это видео уже было загружено
            {
                botton_download.setImageDrawable(adapter_context.resources.getDrawable(R.drawable.ic_ok))
                path = file_video.toString() // ставлю путь из внутреннего хранилища
                if(reference.activeDownloadTasks.size != 0)
                {
                    botton_download.setOnClickListener {
                        Toast.makeText(adapter_context, "Это видео загружается", Toast.LENGTH_SHORT).show()
                        /*если пользователь нажмет на иконку уже загруженного видео, то уведомить его об этом*/
                    }
                }
                else
                {
                    botton_download.setOnClickListener {
                        Toast.makeText(adapter_context, "Это видео уже загруженно", Toast.LENGTH_SHORT).show()
                        /*если пользователь нажмет на иконку уже загруженного видео, то уведомить его об этом*/
                    }
                }

            }
            else
            {
                path = item.video_uri // если видео не было загруженно то ставлю путь их хранилища firebase
            }


            Glide.with(adapter_context).load(item.preview_uri).into(imageView)
            text_name.text = item.name
            text_year.text = item.year


            itemView.setOnClickListener{
                val path_list = ArrayList<String>()
                data_list.forEach { item ->
                    val video = File(storage_directory_video, item.id+".mp4")
                    if(video.exists()) path_list.add(video.path)
                    else path_list.add(item.video_uri)
                }
                val intent = Intent(adapter_context, VideoPlayer::class.java).apply {
                    putExtra("path_list", path_list)
                    putExtra("path", path)
                }
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

        val video_reference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.video_uri)
        video_reference.delete()//Удаляю видео их хранилища
        val preview_reference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.preview_uri)
        preview_reference.delete()
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