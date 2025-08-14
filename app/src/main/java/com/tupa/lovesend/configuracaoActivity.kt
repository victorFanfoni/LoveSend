package com.tupa.lovesend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class configuracaoActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText
    private lateinit var emailVinculadoTextView: TextView
    private lateinit var salvarButton: Button
    private lateinit var btnVoltar: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracao)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailEditText = findViewById(R.id.editTextTextEmailAddress3)
        senhaEditText = findViewById(R.id.editTextTextPassword) // campo de senha que você deve ter no layout
        emailVinculadoTextView = findViewById(R.id.txtEmailVinculado)
        salvarButton = findViewById(R.id.btnVincular)
        btnVoltar = findViewById(R.id.button2)

        btnVoltar.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid

            // Buscar dados já salvos
            carregarEmail(uid)

            // Ação do botão
            salvarButton.setOnClickListener {
                val novoEmail = emailEditText.text.toString().trim()
                val senha = senhaEditText.text.toString().trim()

                if (novoEmail.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(this, "Digite e-mail e senha", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val credential = EmailAuthProvider.getCredential(novoEmail, senha)

                // Vincular conta no Firebase Authentication
                auth.currentUser?.linkWithCredential(credential)
                    ?.addOnSuccessListener {
                        // Se vinculou no Auth, também salva no Firestore
                        val dados = hashMapOf("email" to novoEmail)

                        db.collection("usuarios").document(uid)
                            .set(dados)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Conta vinculada e salva!", Toast.LENGTH_SHORT).show()
                                emailVinculadoTextView.text = "Email vinculado: $novoEmail"
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao salvar no Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    ?.addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao vincular conta: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun carregarEmail(uid: String) {
        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val emailVinculado = document.getString("email") ?: ""
                    emailEditText.setText(emailVinculado)
                    emailVinculadoTextView.text = "Email vinculado: $emailVinculado"
                } else {
                    emailVinculadoTextView.text = "Nenhum email vinculado ainda."
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
