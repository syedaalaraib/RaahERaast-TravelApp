package com.laraib.smd_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView

class review_adapter(private val reviews: List<reviewclass>) : RecyclerView.Adapter<review_adapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_layout, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userIdTextView: TextView = itemView.findViewById(R.id.name)
        private val reviewTextView: TextView = itemView.findViewById(R.id.review)
        private val image: ImageView = itemView.findViewById(R.id.image)


        fun bind(review: reviewclass) {
            userIdTextView.text = review.name
            reviewTextView.text = review.reviewText
            Glide.with(itemView).load(review.image1).into(image)
        }
    }
}

