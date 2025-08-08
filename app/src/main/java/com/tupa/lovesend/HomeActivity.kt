package com.tupa.lovesend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class HomeActivity: AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val mensagem = findViewById<EditText>(R.id.editTextTextMensagem)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)

        btnSettings.setOnClickListener{
            val intent = Intent(this,configuracaoActivity::class.java)
            startActivity(intent)
        }



    }
}
