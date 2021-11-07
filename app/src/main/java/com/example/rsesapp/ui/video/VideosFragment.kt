package com.example.rsesapp.ui.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsesapp.*
import com.example.rsesapp.R
import com.google.firebase.database.*

class VideosFragment : Fragment(), AdapterVideo.OnItemLongClickListener {


    private lateinit var videosViewModel: VideosViewModel
    val USER_KEY = "Video"
    val list_data = ArrayList<ContentVideo>()
    val list_data_display = ArrayList<ContentVideo>()
    var adapterVideo: AdapterVideo? = null


    val data_base: DatabaseReference = FirebaseDatabase.getInstance().getReference(USER_KEY)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        videosViewModel = ViewModelProvider(this).get(VideosViewModel::class.java)


        val activity = activity as MainActivity? //Получаю основное активити
        val searchView = activity?.findViewById<SearchView>(R.id.searh) //Получаю поле поиска из основного активити


        val root = inflater.inflate(R.layout.fragment_videos, container, false)

        val rcView: RecyclerView = root.findViewById(R.id.rcView)
        rcView.layoutManager = LinearLayoutManager(requireContext())//Это просто обязательная хуйня
        getDataFromBase()
        adapterVideo = AdapterVideo(list_data_display, this, requireContext(), activity!!)
        rcView.adapter = adapterVideo

        val swap_helper = Halper().Swap(adapterVideo!!)//Свайп вправо
        swap_helper.attachToRecyclerView(rcView)

        if (searchView != null) {
            Halper().initSearchView(searchView, rcView, list_data, list_data_display)
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

                    if(user!!.type == "NORMAL")
                    {
                        list_data.add(user)
                    }
                }
                list_data_display.clear()
                list_data_display.addAll(list_data)
                adapterVideo?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {}

        }
        data_base.addValueEventListener(valueListener)
    }

    override fun itemLongClick(item: ContentVideo, position: Int, view: View) {
        Toast.makeText(requireContext(), "${item.name}", Toast.LENGTH_SHORT).show()
        Halper().refactor(item, adapterVideo!!)
    }
}