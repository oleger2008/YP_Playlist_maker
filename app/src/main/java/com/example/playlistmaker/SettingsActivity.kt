package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initButtonsCallbacks()
    }

    private fun initButtonsCallbacks() {
        val returnButton = findViewById<ImageButton>(R.id.settings_return_button)
        returnButton.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<LinearLayout>(R.id.share_settings_line)
        shareButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, R.string.share_link)
                startActivity(this)
            }
        }

        val supportButton = findViewById<LinearLayout>(R.id.support_settings_line)
        supportButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_letter_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_letter_text))
                startActivity(this)
            }
        }

        val licenseAgreementButton = findViewById<LinearLayout>(R.id.license_agreement_settings_line)
        licenseAgreementButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.license_agreement_link))
                startActivity(this)
            }
        }
    }
}