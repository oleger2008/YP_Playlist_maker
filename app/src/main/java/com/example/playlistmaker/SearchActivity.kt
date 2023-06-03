package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private enum class SearchResponseStatus {
        OK, EMPTY, ERROR
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi = retrofit.create(ITunesApi::class.java)
    private var searchText: String = ""
    private val tracks: ArrayList<Track> = arrayListOf()
    private val trackListAdapter = TrackListAdapter(tracks)

    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var returnButton: ImageButton
    private lateinit var trackList: RecyclerView
    private lateinit var errorMessage: TextView
    private lateinit var errorImage: ImageView
    private lateinit var refreshButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        lateInit()
        setListeners()
    }

    private fun lateInit() {
        queryInput = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.search_clear_button)
        returnButton = findViewById(R.id.search_return_button)
        trackList = findViewById(R.id.track_list)
        trackList.adapter = trackListAdapter
        errorMessage = findViewById(R.id.search_error_message)
        errorImage = findViewById(R.id.search_error_image)
        refreshButton = findViewById(R.id.search_refresh_button)
    }

    private fun setListeners() {
        clearButton.setOnClickListener {
            queryInput.setText("")
            hideKeyboard()
            clearTrackList()
        }
        returnButton.setOnClickListener {
            finish()
        }
        queryInput.addTextChangedListener(getSearchTextWatcher())
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack(queryInput.text.toString())
            }
            false
        }
        refreshButton.setOnClickListener {
            searchTrack(queryInput.text.toString())
        }
    }

    private fun clearTrackList() {
        tracks.clear()
        trackListAdapter.notifyDataSetChanged()
    }

    private fun getSearchTextWatcher(): TextWatcher {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(sequence)
                searchText = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // empty
            }
        }
        return searchTextWatcher
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(queryInput.windowToken, 0)
        queryInput.clearFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, getString(R.string.search))
        queryInput.setText(searchText)
    }

    private fun searchTrack(trackName: String) {
        iTunesApi.search(trackName).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                tracks.clear()
                val isGoodResponse: Boolean = response.code() == 200
                if (isGoodResponse && (response.body()?.results?.isNotEmpty() == true)) {
                    tracks.addAll(response.body()?.results!!)
                }
                trackListAdapter.notifyDataSetChanged()
                handleResponseStatus(getSearchResponseStatus(isGoodResponse))
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                clearTrackList()
                handleResponseStatus(SearchResponseStatus.ERROR)
            }
        })
    }

    private fun getSearchResponseStatus(isGoodResponse: Boolean): SearchResponseStatus {
        return if (isGoodResponse) {
            if (tracks.isEmpty()) SearchResponseStatus.EMPTY
            else SearchResponseStatus.OK
        } else {
            SearchResponseStatus.ERROR
        }
    }

    private fun handleResponseStatus(status: SearchResponseStatus) {
        when (status) {
            SearchResponseStatus.OK -> hideInfoMessage()
            SearchResponseStatus.EMPTY -> showEmptyInfoMessage()
            SearchResponseStatus.ERROR -> showErrorInfoMessage()
        }
    }

    private fun hideInfoMessage() {
        errorMessage.visibility = View.GONE
        errorImage.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showEmptyInfoMessage() {
        errorMessage.text = getString(R.string.nothing_found)
        errorImage.setImageResource(R.drawable.nothing_found)

        errorMessage.visibility = View.VISIBLE
        errorImage.visibility = View.VISIBLE
        refreshButton.visibility = View.GONE
    }

    private fun showErrorInfoMessage() {
        errorMessage.text = getString(R.string.problems_with_internet)
        errorImage.setImageResource(R.drawable.problem_with_internet)

        errorMessage.visibility = View.VISIBLE
        errorImage.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val BASE_URL = "https://itunes.apple.com"
    }
}