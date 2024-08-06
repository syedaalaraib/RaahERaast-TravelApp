package com.laraib.smd_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EventsAdapter(private val context: Context, private val eventsList: List<Event>) :
    RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_events, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.eventName.text = event.name
        holder.location.text = event.location
        holder.date.text = event.date

        // Load image using Glide
        Glide.with(context)
            .load(event.imageURL)
            .placeholder(R.drawable.images_1) // Placeholder image while loading
            .error(R.drawable.image) // Error image if loading fails
            .into(holder.eventImage)
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage: ImageView = itemView.findViewById(R.id.eventimage)
        val eventName: TextView = itemView.findViewById(R.id.eventname)
        val location: TextView = itemView.findViewById(R.id.textViewLocation)
        val date: TextView = itemView.findViewById(R.id.textViewDate)
    }
}
