package com.example.rsesapp

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ActivitySaveVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_video)
        val rcView = findViewById<RecyclerView>(R.id.rcView)
        rcView.layoutManager = LinearLayoutManager(this)

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener{
            val anim_update = AnimationUtils.loadAnimation(this, R.anim.alpha)
            back.startAnimation(anim_update)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }


        val arrDisplay = ArrayList<File>()// Список который будем передавать в адаптер
        val path_storage = getExternalFilesDir(null).toString()//Путь к основной папке
        val dir = File(path_storage, "VIDEO_DATA_RSES")//Путь к папке с видео

        dir.walk().forEach { file ->
            if(file.isFile) arrDisplay.add(file)
            //Получаем список всех файлов в папке с видео
        }

        val adapter = AdapterSaveVideo(arrDisplay, this, this)
        rcView.adapter = adapter
        val swap_halper = Swap(adapter)
        swap_halper.attachToRecyclerView(rcView)
    }
    fun Swap(adapter: AdapterSaveVideo): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition //Берем позицию из отображаемых элементов
                viewHolder.setIsRecyclable(true)
                val item = adapter.data_list[position]
                adapter.remuve_view(position)

                val conf = AlertDialog.Builder(adapter.adapter_context)// Диалоговое окно
                conf.setCancelable(false)
                conf.setTitle("Подтверждение")
                conf.setMessage("Вы действительно хотите удалить видео с телефона?")

                conf.setPositiveButton("Да"){dialog, width ->
                    adapter.remove(item)
                }
                conf.setNegativeButton("Нет"){dialog, width ->
                    adapter.set_view(position, item)
                }
                conf.show()
            }
        })
    }

    override fun onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}



