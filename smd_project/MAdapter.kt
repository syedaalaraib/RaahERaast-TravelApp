package com.laraib.smd_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MentorAdapter(private val mentorList: List<Mentor>, private val context: Context) :
    RecyclerView.Adapter<MentorAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mentor = mentorList[position]
        holder.name.text = mentor.name
        holder.location.text = mentor.location
        // Load the image from URL using Glide
        Glide.with(context)
            .load(mentor.imageURL)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return mentorList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nametext)
        val location: TextView = itemView.findViewById(R.id.locationtext)
        val image: ImageView = itemView.findViewById(R.id.placeImage)
    }
}


data class Mentor(
    val name: String,
    val location: String,
    val imageURL: String// Resource ID for the image
)
