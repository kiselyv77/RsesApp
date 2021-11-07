package com.example.rsesapp

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*


class AdapterPager(private var pathList: ArrayList<String>, context: Context): RecyclerView.Adapter<AdapterPager.Pager2ViewHolder>() {
    val rcontext = context
    inner class Pager2ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.image)


        fun bind(path: String, context: Context)
        {
            Glide.with(context).load(path).into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        val item = pathList.get(position)
        holder.bind(item, rcontext)

    }

    override fun getItemCount(): Int {
        return pathList.size
    }
}