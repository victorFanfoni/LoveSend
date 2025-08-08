package com.tupa.lovesend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class configuracaoActivity : AppCompatActivity(){

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracao)

        val email = findViewById<EditText>(R.id.editTextTextEmailAddress3)
        val btn = findViewById<Button>(R.id.button)
        val btnVoltar = findViewById<Button>(R.id.button2)

        btnVoltar.setOnClickListener {
            finish()
        }

    }

}
