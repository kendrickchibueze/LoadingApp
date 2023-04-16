package com.example.loadingapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton


class DetailActivity : AppCompatActivity() {
    private lateinit var statusTextView: TextView
    private lateinit var fileNameTextView: TextView
    //private lateinit var backButton: Button
    private lateinit var backButton: AppCompatButton



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