package com.tupa.lovesend

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        // Inicializa o Firebase Auth
        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val password = findViewById<EditText>(R.id.editTextTextPassword2)
        val confPassword = findViewById<EditText>(R.id.editTextTextPassword3)
        val btnCadastro = findViewById<Button>(R.id.btnCadastrar)

        btnCadastro.setOnClickListener {
            val emailTxt = email.text.toString().trim()
            val passTxt = password.text.toString().trim()
            val confPassTxt = confPassword.text.toString().trim()

            // Validações
            if (emailTxt.isEmpty() || passTxt.isEmpty() || confPassTxt.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
                Toast.makeText(this, "Digite um email válido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passTxt.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passTxt != confPassTxt) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Criar usuário no Firebase
            auth.createUserWithEmailAndPassword(emailTxt, passTxt)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish() // Fecha a tela e volta
                    } else {
                        val errorMsg = when (task.exception?.message) {
                            "The email address is already in use by another account." -> "Este email já está cadastrado."
                            "The given password is invalid. [ Password should be at least 6 characters ]" -> "Senha muito curta!"
                            else -> "Erro ao cadastrar: ${task.exception?.message}"
                        }
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
