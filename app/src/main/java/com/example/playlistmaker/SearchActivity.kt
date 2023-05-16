package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi = retrofit.create(ITunesApi::class.java)
    private var searchText: String = ""
    private val tracks: ArrayList<Track> = arrayListOf()

    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var returnButton: ImageButton
    private lateinit var trackList: RecyclerView


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
        trackList.adapter = TrackListAdapter(tracks)
    }

    private fun setListeners() {
        clearButton.setOnClickListener {
            queryInput.setText("")
            hideKeyboard()
        }
        returnButton.setOnClickListener {
            finish()
        }
        queryInput.addTextChangedListener(getSearchTextWatcher())
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //TODO SEND request
                true
            }
            false
        }
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

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val BASE_URL = "https://itunes.apple.com"
    }
}