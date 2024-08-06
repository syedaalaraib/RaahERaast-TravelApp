package com.laraib.smd_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewsAdapter(private val reviewsList: List<Review>) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewsList[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return reviewsList.size
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewReviewerName: TextView = itemView.findViewById(R.id.placename)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val textViewReview: TextView = itemView.findViewById(R.id.textViewReview)

        fun bind(review: Review) {
            textViewReviewerName.text = review.placeName
            ratingBar.rating = review.rating
            textViewReview.text = review.reviewText
        }
    }
}
