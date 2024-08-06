package com.laraib.smd_project


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.laraib.smd_project.Item_RV
import com.laraib.smd_project.R // Assuming you have an R class in your package

class guide_adapter(private val mentors_list: MutableList<Item_RV>, private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<guide_adapter.mentorsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mentorsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.topmentor_layout, parent, false)
        return mentorsViewHolder(view)
    }

    override fun onBindViewHolder(holder: mentorsViewHolder, position: Int) {
        val mentor = mentors_list[position]
        holder.bind(mentor)
    }

    override fun getItemCount(): Int {
        return mentors_list.size
    }

    inner class mentorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in mentors_item layout
        private val image1: ImageView = itemView.findViewById(R.id.image)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val description: TextView = itemView.findViewById(R.id.description)

        fun bind(mentor: Item_RV) {
            // Set values to views
            Glide.with(itemView)
                .load(mentor.image1) // Load image using Glide (assuming you have Glide dependency)
                .into(image1)
            name.text = mentor.name
            description.text = mentor.place

            // Set click listener for the item view
            itemView.setOnClickListener {
                onItemClick(mentor.uid ?: "")
            }
        }

    }
}
