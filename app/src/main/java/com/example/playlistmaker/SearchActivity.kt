package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var returnButton: ImageButton
    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        lateInit()
        setListeners()
    }

    private fun lateInit() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.search_clear_button)
        returnButton = findViewById(R.id.search_return_button)
    }

    private fun setListeners() {
        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
        }
        returnButton.setOnClickListener {
            finish()
        }
        searchEditText.addTextChangedListener(getSearchTextWatcher())
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
        keyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        searchEditText.clearFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, getString(R.string.search))
        searchEditText.setText(searchText)
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}