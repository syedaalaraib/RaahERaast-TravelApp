import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Import Glide library
import com.bumptech.glide.load.engine.DiskCacheStrategy // Import Glide DiskCacheStrategy
import com.laraib.smd_project.User_rv
import com.laraib.smd_project.privatemessage_traveller
import com.laraib.smd_project.R

class user_adapter(private val user_list: MutableList<User_rv>, private val mContext: Context) : RecyclerView.Adapter<user_adapter.userViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return userViewHolder(view)
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val mentor = user_list[position]
        holder.bind(mentor)

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, privatemessage_traveller::class.java)
            intent.putExtra("userid", mentor.name) // Assuming mentor has a uid property
            intent.putExtra("currentuserid", mentor.uid)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return user_list.size
    }

    inner class userViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in mentors_item layout
        private val heart: ImageView = itemView.findViewById(R.id.image)
        private val name: TextView = itemView.findViewById(R.id.user_name)
        fun bind(mentor: User_rv) {
            Log.d("User_rv", "Photolink: ${mentor.photolink}")
            if (!mentor.photolink.isNullOrEmpty()) {
                Toast.makeText(itemView.context, mentor.photolink, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(itemView.context, "Photolink is empty or null", Toast.LENGTH_SHORT).show()
            }
            // Load image using Glide
            Glide.with(mContext)
                .load(mentor.photolink) // Load image from mentor's photolink
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image while loading
                .error(R.drawable.ic_launcher_background) // Error image if loading fails
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache image
                .into(heart) // Set loaded image to ImageView
            name.text = mentor.name
        }

    }
}
