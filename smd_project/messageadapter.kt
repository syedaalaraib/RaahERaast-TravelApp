package com.laraib.smd_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.laraib.smd_project.R
import com.laraib.smd_project.ChatRV

class messageadapter(private val mContext: Context, private val chatList: List<ChatRV>) : RecyclerView.Adapter<messageadapter.ChatViewHolder>() {

    private val fuser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(mContext).inflate(
            if (viewType == MSG_TYPE_RIGHT) R.layout.chat_item_right else R.layout.chat_item_left,
            parent, false
        )
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]

        // Check if it's a text message or a voice chat message
        if (chat.message?.isNotEmpty() == true) {
            // Text message
            holder.showMessage.visibility = View.VISIBLE
            holder.voiceMessageButton.visibility = View.GONE
            holder.showMessage.text = chat.message
        }
        else if (chat.image?.isNotEmpty() == true) {
            // Image message
            holder.showMessage.visibility = View.GONE
            holder.voiceMessageButton.visibility = View.VISIBLE
            // Load image using Glide
            Glide.with(mContext).load(chat.image).into(holder.voiceMessageButton)
        }
        else if (chat.voiceNoteUrl?.isNotEmpty() == true) {
            // Voice chat message
            holder.showMessage.visibility = View.GONE
            holder.voiceMessageButton.visibility = View.VISIBLE
            // Add click listener to play voice message
            holder.voiceMessageButton.setOnClickListener {
                // Implement voice message playback logic here
            }
        }
        else {
            // No message
            holder.showMessage.visibility = View.GONE
            holder.voiceMessageButton.visibility = View.GONE
        }

        // Load user image using Glide
//        if (chat.sender == fuser?.uid) {
//            // Load sender's image
//            Glide.with(mContext).load(chat.image).into(holder.profileImage)
//        } else {
//            // Load receiver's image
//            Glide.with(mContext).load(chat.image).into(holder.profileImage)
//        }
//        if (chat.voiceNoteUrl?.isNotEmpty() == true) {
//            // Voice chat message
//
//            holder.showMessage.visibility = View.GONE
//            holder.voiceMessageButton.visibility = View.VISIBLE
//
//            // Add click listener to play voice message
//            holder.voiceMessageButton.setOnClickListener {
//                // Implement voice message playback logic here
//            }
//        }

        // Load user image using Glide
//        if (imageurl == "default") {
//            holder.profileImage.setImageResource(R.drawable.ic_launcher_background)
//        } else {
//            Glide.with(mContext).load(imageurl).into(holder.profileImage)
//        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showMessage: TextView = itemView.findViewById(R.id.showmessage)
        val profileImage: ImageView = itemView.findViewById(R.id.profileimage)
        val voiceMessageButton: ImageView = itemView.findViewById(R.id.voice_message_button)
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].sender == fuser?.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}