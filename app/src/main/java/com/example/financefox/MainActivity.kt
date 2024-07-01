package com.example.financefox

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import com.firebase.ui.auth.AuthUI


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sign_out : Button = findViewById(R.id.sign_out)
        sign_out.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                }

        }
    }
}