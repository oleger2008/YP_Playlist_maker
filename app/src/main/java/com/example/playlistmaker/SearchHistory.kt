package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val preferences: SharedPreferences) {
    private val gson = Gson()
    var recentTracks: ArrayList<Track> = loadTracks()
        private set

    fun addTrack(track: Track) {
        recentTracks.removeIf {
            it.trackId == track.trackId
        }
        if (recentTracks.size == MAX_SIZE) {
            recentTracks.removeLast()
        }
        recentTracks.add(0, track)
        saveTracks()
    }

    fun clear() {
        recentTracks.clear()
        saveTracks()
    }

    private fun saveTracks() {
        val json = gson.toJson(recentTracks)
        preferences.edit()
            .putString(SEARCH_HISTORY, json)
            .apply()
    }

    private fun loadTracks(): ArrayList<Track> {
        val json = preferences.getString(SEARCH_HISTORY, null)
        return if (json == null) {
            arrayListOf()
        } else {
            val type = object : TypeToken<ArrayList<Track>>(){}.type
            gson.fromJson(json, type)
        }
    }

    companion object {
        private const val MAX_SIZE = 10
        private const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }
}