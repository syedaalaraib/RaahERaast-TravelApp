package com.laraib.smd_project

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Import Glide library

class PlaceAdapter(private val context: Context, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_places, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.location.text = place.location

        // Load image using Glide
        Glide.with(context)
            .load(place.imageURL)
            .placeholder(R.drawable.images_1) // Placeholder image while loading
            .error(R.drawable.image) // Error image if loading fails
            .into(holder.placeImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, placepage::class.java)
            intent.putExtra("placeId", place.id)
            Log.d("PlaceAdapter", "Sending placeId: ${place.id}")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeImage: ImageView = itemView.findViewById(R.id.placeImage)
        val placeName: TextView = itemView.findViewById(R.id.placename)
        val location: TextView = itemView.findViewById(R.id.location)
    }
}
