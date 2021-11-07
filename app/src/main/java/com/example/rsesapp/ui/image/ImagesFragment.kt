package com.example.rsesapp.ui.image

import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsesapp.*
import com.example.rsesapp.R
import com.google.firebase.database.*

class ImagesFragment : Fragment(), AdapterImage.OnItemLongClickListener {

    private lateinit var imagesViewModel: ImagesViewModel
    val USER_KEY = "Image"
    val list_data = ArrayList<ContentVideo>()
    val list_data_display = ArrayList<ContentVideo>()
    var adapterImage: AdapterImage? = null


    val data_base: DatabaseReference = FirebaseDatabase.getInstance().getReference(USER_KEY)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        imagesViewModel = ViewModelProvider(this).get(ImagesViewModel::class.java)

        val activity = activity as MainActivity? //Получаю основное активити
        val searchView = activity?.findViewById<SearchView>(R.id.searh) //Получаю поле поиска из основного активити

        val root = inflater.inflate(R.layout.fragment_images, container, false)




        val rcView: RecyclerView = root.findViewById(R.id.rcView)
        rcView.layoutManager = LinearLayoutManager(requireContext())//Это просто обязательная хуйня
        getDataFromBase()
        adapterImage = AdapterImage(list_data_display, this, requireContext(), activity!!)
        rcView.adapter = adapterImage

        val swap_helper = HalperImage().SwapImage(adapterImage!!)//Свайп вправо
        swap_helper.attachToRecyclerView(rcView)

        if (searchView != null) {
            HalperImage().initSearchViewImage(searchView, rcView, list_data, list_data_display)
        }

        return root
    }

    private fun getDataFromBase()
    {
        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(list_data.size > 0) list_data.clear()
                for(i in snapshot.children){
                    val user = i.getValue(ContentVideo::class.java)// Просто блять запомнить и все нахуй

                    if(user!!.type == "IMAGE")
                    {
                        list_data.add(user)
                    }
                }
                list_data_display.clear()
                list_data_display.addAll(list_data)
                adapterImage?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {}

        }
        data_base.addValueEventListener(valueListener)
    }

    override fun itemLongClick(item: ContentVideo, position: Int, view: View) {
        Toast.makeText(requireContext(), "${item.name}", Toast.LENGTH_SHORT).show()
        HalperImage().refactor_image(item, adapterImage!!)
    }
}