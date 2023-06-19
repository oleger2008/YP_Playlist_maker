package com.example.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackListAdapter(
    private val trackList: ArrayList<Track>,
    private val onTrackClickListener: ((Track) -> Unit)
) : RecyclerView.Adapter<TrackListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder =
        TrackListViewHolder(parent)

    override fun getItemCount(): Int = trackList.size

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClickListener.invoke(track)
        }
    }
}
