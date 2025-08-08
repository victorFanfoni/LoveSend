package com.tupa.lovesend

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity: AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val email = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val password = findViewById<EditText>(R.id.editTextTextPassword2)
        val confpassword = findViewById<EditText>(R.id.editTextTextPassword3)
        val btnCadastro = findViewById<Button>(R.id.btnCadastrar)

        btnCadastro.setOnClickListener {

        }

    }
}
