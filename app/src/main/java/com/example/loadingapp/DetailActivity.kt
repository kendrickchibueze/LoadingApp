package com.example.loadingapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DetailActivity : AppCompatActivity() {
    private lateinit var statusTextView: TextView
    private lateinit var fileNameTextView: TextView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        statusTextView = findViewById(R.id.status)
        fileNameTextView = findViewById(R.id.filename)
        backButton = findViewById(R.id.back_btn)

        intent.getStringExtra("status")?.let {
            statusTextView.text = it
        }

        intent.getStringExtra("file_name")?.let {
            fileNameTextView.text = it
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}