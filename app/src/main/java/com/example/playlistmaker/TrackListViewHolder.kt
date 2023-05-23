package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackListViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(
        R.layout.track_item, parent, false)) {
    private val trackImage = itemView.findViewById<ImageView>(R.id.track_image)
    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val trackAuthor = itemView.findViewById<TextView>(R.id.track_author)
    private val trackDuration = itemView.findViewById<TextView>(R.id.track_duration)

    fun bind(model: Track) {
        val radius = trackImage.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_default_image)
            .centerCrop()
            .transform(RoundedCorners(radius))
            .into(trackImage)
        trackName.text = model.trackName
        trackAuthor.text = model.artistName
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(model.trackTimeMillis.toInt())
    }
}
