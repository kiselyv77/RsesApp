package com.example.rsesapp

import android.app.AlertDialog
import android.app.Dialog
import android.view.Window
import android.widget.Button
import android.widget.RadioGroup
import android.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import kotlin.collections.ArrayList

class HalperImage {

    fun SwapImage(adapter: AdapterImage): ItemTouchHelper {
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
                conf.setMessage("Вы действительно хотите удалить фото?")

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
    fun refactor_image(item:ContentVideo, adapter:AdapterImage){
        val dialog = Dialog(adapter.adapter_context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.rename_images)
        val name_field = dialog.findViewById<TextInputLayout>(R.id.name_form)
        val radio_group_year = dialog.findViewById<RadioGroup>(R.id.radioGroupYear)
        val yes = dialog.findViewById<Button>(R.id.yes)
        val no = dialog.findViewById<Button>(R.id.not)
        name_field.editText?.setText(item.name)

        /*Проверяю какой год был до изменения*/
        var yearCheck:Int? = null
        if("2016" == item.year) yearCheck = R.id.year2016
        else if("2017" == item.year) yearCheck = R.id.year2017
        else if("2018" == item.year) yearCheck = R.id.year2018
        else if("2019" == item.year) yearCheck = R.id.year2019
        else if("2020" == item.year) yearCheck = R.id.year2020
        else if("2021" == item.year) yearCheck = R.id.year2021
        else yearCheck = R.id.year2021
        radio_group_year.check(yearCheck)



        yes.setOnClickListener{
            val name = name_field.editText?.text.toString()
            val year = GetYearType().getYear(radio_group_year)
            val type = "IMAGE"
            val new_item = ContentVideo(item.id, name, year, type, item.video_uri, item.preview_uri)
            adapter.refract(item, new_item)
            dialog.dismiss()
        }

        no.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    fun initSearchViewImage(searchView: SearchView, rcView: RecyclerView, list_data:ArrayList<ContentVideo>, list_data_display:ArrayList<ContentVideo>)
            /*Функция инициализации поиска*/
    {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean { // Срабатывает ппри изменении текста в поле поиска

                if(newText!!.isNotEmpty()){
                    /*Если поле поиска не пустое заполняем arrDisplay
                    совпадениями из изначального списка*/
                    val searchText = newText.toLowerCase(Locale.getDefault())
                    list_data_display.clear()
                    list_data.forEach {item ->
                        if(item.name.toLowerCase(Locale.getDefault()).contains(searchText)){
                            list_data_display.add(item)
                        }
                    }
                    rcView.adapter!!.notifyDataSetChanged()
                }
                else{
                    /*Если пустое заполняем его всеми элементами изначального списка*/
                    list_data_display.clear()
                    list_data_display.addAll(list_data)
                    rcView.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
    }
}