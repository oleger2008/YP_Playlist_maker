package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackImage = itemView.findViewById<ImageView>(R.id.track_image)
    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val trackAuthor = itemView.findViewById<TextView>(R.id.track_author)
    private val trackDuration = itemView.findViewById<TextView>(R.id.track_duration)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_default_image)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(trackImage)
        trackName.text = model.trackName
        trackAuthor.text = model.artistName
        trackDuration.text = model.trackTime
    }
}
