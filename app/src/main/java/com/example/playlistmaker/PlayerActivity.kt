package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var returnButton: ImageButton
    private lateinit var albumCoverView: ImageView
    private lateinit var playerTimerView: TextView
    private lateinit var trackNameView: TextView
    private lateinit var artistNameView: TextView
    private lateinit var durationView: TextView
    private lateinit var albumNameView: TextView
    private lateinit var releaseView: TextView
    private lateinit var genreView: TextView
    private lateinit var countryView: TextView
    private lateinit var albumNameGroup: Group

    private lateinit var currentTrack: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setListeners()
        currentTrack = loadTrack()
        fillTrackInfo(currentTrack)
    }

    override fun onResume() {
        super.onResume()
        fillTrackInfo(currentTrack)
    }

    private fun initViews() {
        returnButton = findViewById(R.id.player_return_button)
        albumCoverView = findViewById(R.id.player_album_cover)
        playerTimerView = findViewById(R.id.player_track_timer)
        trackNameView = findViewById(R.id.player_track_name)
        artistNameView = findViewById(R.id.player_artist_name)
        durationView = findViewById(R.id.player_track_duration_value)
        albumNameView = findViewById(R.id.player_track_album_value)
        releaseView = findViewById(R.id.player_track_release_value)
        genreView = findViewById(R.id.player_track_genre_value)
        countryView = findViewById(R.id.player_track_country_value)
        albumNameGroup = findViewById(R.id.player_album_name_group)
    }

    private fun loadTrack(): Track {
        return intent.getParcelableExtra(PLAYER_TRACK_DATA)!!
    }

    private fun setListeners() {
        returnButton.setOnClickListener {
            finish()
        }
    }

    private fun getCoverArtwork(artworkUrl100: String) =
        artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    private fun setAlbumCover(track: Track) {
        val radius = resources.getDimensionPixelSize(R.dimen.player_track_album_cover_corner_radius)
        Glide.with(this)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.album_default_cover)
            .transform(RoundedCorners(radius))
            .into(albumCoverView)
    }

    private fun fillTrackInfo (track: Track){
        setAlbumCover(track)
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        durationView.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis.toInt())
        playerTimerView.text = durationView.text //todo temporary solution
        albumNameView.text = track.collectionName
        albumNameGroup.visibility = if (track.collectionName.isEmpty()) View.GONE else View.VISIBLE
        releaseView.text = track.releaseDate.substring(0, 4)
        genreView.text = track.primaryGenreName
        countryView.text = track.country
    }
}