package com.tupa.lovesend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val password = findViewById<EditText>(R.id.editTextTextPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCadastro = findViewById<Button>(R.id.btnCadastro)

        // Botão de Login
        btnLogin.setOnClickListener {
            val emailTxt = email.text.toString().trim()
            val passTxt = password.text.toString().trim()

            // Validações
            if (emailTxt.isEmpty() || passTxt.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
                Toast.makeText(this, "Digite um email válido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Autenticação no Firebase
            auth.signInWithEmailAndPassword(emailTxt, passTxt)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish() // Fecha tela de login
                    } else {
                        val errorMsg = when (task.exception?.message) {
                            "The password is invalid or the user does not have a password." -> "Senha incorreta."
                            "There is no user record corresponding to this identifier. The user may have been deleted." -> "Usuário não encontrado."
                            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Erro de conexão com a internet."
                            else -> "Erro ao fazer login: ${task.exception?.message}"
                        }
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Botão de Cadastro
        btnCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // Se já estiver logado, vai direto para HomeActivity
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
