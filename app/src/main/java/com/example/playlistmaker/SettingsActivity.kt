package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch

class SettingsActivity : AppCompatActivity() {
    private lateinit var returnButton: ImageButton
    private lateinit var themeSwitcher: Switch
    private lateinit var shareButton: LinearLayout
    private lateinit var supportButton: LinearLayout
    private lateinit var licenseAgreementButton: LinearLayout
    private val sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        lateInit()
        initButtonsCallbacks()
    }

    private fun lateInit() {
        returnButton = findViewById(R.id.settings_return_button)
        themeSwitcher = findViewById(R.id.themeSwitcher)
        themeSwitcher.isChecked = sharedPreferences.getBoolean(DARK_THEME, false)
        shareButton = findViewById(R.id.share_settings_line)
        supportButton = findViewById(R.id.support_settings_line)
        licenseAgreementButton = findViewById(R.id.license_agreement_settings_line)
    }

    private fun initButtonsCallbacks() {
        returnButton.setOnClickListener {
            finish()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        shareButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
                type = "text/plain"
                startActivity(Intent.createChooser(this, "Sharing..."))
            }
        }

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

        licenseAgreementButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.license_agreement_link))
                startActivity(this)
            }
        }
    }
}