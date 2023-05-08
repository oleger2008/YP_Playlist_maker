package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchTextButton: ImageView
    private var queryText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        lateInit()
        setListeners()
    }

    private fun lateInit() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearSearchTextButton = findViewById<ImageView>(R.id.search_clear_button)
    }

    private fun setListeners() {
        clearSearchTextButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
        }
        searchEditText.addTextChangedListener(getSearchTextWatcher())
    }

    private fun getSearchTextWatcher(): TextWatcher {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) {
                clearSearchTextButton.visibility = clearButtonVisibility(sequence)
                queryText = sequence.toString()
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
}