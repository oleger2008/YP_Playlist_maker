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
import androidx.core.widget.NestedScrollView
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
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryAdapter: TrackListAdapter
    private val tracks: ArrayList<Track> = arrayListOf()
    private val trackListAdapter = TrackListAdapter(tracks) {
        searchHistory.addTrack(it)
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private lateinit var queryInputEditText: EditText
    private lateinit var clearQueryButton: ImageView
    private lateinit var returnButton: ImageButton
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var errorMessageTextView: TextView
    private lateinit var errorImageView: ImageView
    private lateinit var refreshButton: Button
    private lateinit var searchHistoryListRecyclerView: RecyclerView
    private lateinit var historyNestedScrollView: NestedScrollView
    private lateinit var clearHistoryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setListeners()
    }

    private fun initViews() {
        queryInputEditText = findViewById(R.id.search_edit_text)
        clearQueryButton = findViewById(R.id.search_clear_button)
        returnButton = findViewById(R.id.search_return_button)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        refreshButton = findViewById(R.id.search_refresh_button)

        trackListRecyclerView = findViewById(R.id.track_list)
        trackListRecyclerView.adapter = trackListAdapter

        errorMessageTextView = findViewById(R.id.search_error_message)
        errorImageView = findViewById(R.id.search_error_image)

        searchHistoryListRecyclerView = findViewById(R.id.history_list)
        searchHistory = SearchHistory(getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE))
        searchHistoryAdapter = TrackListAdapter(searchHistory.recentTracks) {
            searchHistory.addTrack(it)
            searchHistoryAdapter.notifyDataSetChanged()
        }
        searchHistoryListRecyclerView.adapter = searchHistoryAdapter
        historyNestedScrollView = findViewById(R.id.search_history)
    }

    private fun setListeners() {
        clearQueryButton.setOnClickListener {
            queryInputEditText.setText("")
            hideKeyboard()
            clearTrackList()
            hideInfoMessage()
            showSearchHistory()
        }
        returnButton.setOnClickListener {
            finish()
        }
        queryInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearTrackList()
                hideInfoMessage()
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }
        queryInputEditText.addTextChangedListener(getSearchTextWatcher())
        queryInputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack(queryInputEditText.text.toString())
                queryInputEditText.clearFocus()
            }
            false
        }
        refreshButton.setOnClickListener {
            searchTrack(queryInputEditText.text.toString())
        }
        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            hideSearchHistory()
        }

        trackListRecyclerView.setOnTouchListener { _, _ ->
            hideKeyboard()
            queryInputEditText.clearFocus()
            false
        }
        searchHistoryListRecyclerView.setOnTouchListener { _, _ ->
            hideKeyboard()
            queryInputEditText.clearFocus()
            false
        }
    }

    private fun showSearchHistory() {
        if (searchHistory.recentTracks.isNotEmpty()) {
            historyNestedScrollView.visibility = View.VISIBLE
        }
    }

    private fun hideSearchHistory() {
        historyNestedScrollView.visibility = View.GONE
    }

    private fun clearTrackList() {
        tracks.clear()
        trackListAdapter.notifyDataSetChanged()
    }

    private fun getSearchTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int,
                                           after: Int) {
                // empty
            }

            override fun onTextChanged(sequence: CharSequence, start: Int, before: Int,
                                       count: Int) {
                clearQueryButton.visibility = getClearButtonVisibility(sequence)
                if (sequence.isNullOrEmpty()) {
                    hideInfoMessage()
                    showSearchHistory()
                }
                searchText = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // empty
            }
        }
    }

    private fun getClearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(queryInputEditText.windowToken, 0)
        queryInputEditText.clearFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, getString(R.string.search))
        queryInputEditText.setText(searchText)
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
        errorMessageTextView.visibility = View.GONE
        errorImageView.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showEmptyInfoMessage() {
        errorMessageTextView.text = getString(R.string.nothing_found)
        errorImageView.setImageResource(R.drawable.nothing_found)

        errorMessageTextView.visibility = View.VISIBLE
        errorImageView.visibility = View.VISIBLE
        refreshButton.visibility = View.GONE
    }

    private fun showErrorInfoMessage() {
        errorMessageTextView.text = getString(R.string.problems_with_internet)
        errorImageView.setImageResource(R.drawable.problem_with_internet)

        errorMessageTextView.visibility = View.VISIBLE
        errorImageView.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val BASE_URL = "https://itunes.apple.com"
    }
}