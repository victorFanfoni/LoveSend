package com.tupa.lovesend

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val mensagemEditText = findViewById<EditText>(R.id.editTextTextMensagem)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val btnSair = findViewById<Button>(R.id.btnSair)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Escuta mensagens recebidas em tempo real
        escutarMensagens()

        btnSettings.setOnClickListener {
            startActivity(Intent(this, configuracaoActivity::class.java))
        }

        btnSair.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnEnviar.setOnClickListener {
            val mensagem = mensagemEditText.text.toString().trim()
            if (mensagem.isEmpty()) {
                Toast.makeText(this, "Digite uma mensagem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                val uid = user.uid

                // Busca o email do parceiro salvo no Firestore
                db.collection("usuarios").document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val parceiroEmail = doc.getString("email")
                        if (parceiroEmail.isNullOrEmpty()) {
                            Toast.makeText(this, "Nenhum parceiro vinculado!", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        // Procura o UID do parceiro usando o meuEmail salvo no doc dele
                        db.collection("usuarios")
                            .whereEqualTo("meuEmail", parceiroEmail)
                            .get()
                            .addOnSuccessListener { query ->
                                if (!query.isEmpty) {
                                    val parceiroUid = query.documents[0].id

                                    // Salva a mensagem no documento do parceiro
                                    db.collection("usuarios").document(parceiroUid)
                                        .update("mensagem", mensagem)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Mensagem enviada!", Toast.LENGTH_SHORT).show()
                                            mensagemEditText.setText("")
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(this, "Parceiro não encontrado!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
            } else {
                Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun escutarMensagens() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("usuarios").document(user.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

                    val novaMensagem = snapshot.getString("mensagem") ?: return@addSnapshotListener
                    if (novaMensagem.isNotEmpty()) {
                        exibirNotificacao(novaMensagem)
                    }
                }
        }
    }

    private fun exibirNotificacao(mensagem: String) {
        val channelId = "mensagem_casal"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mensagens",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Mensagem do seu parceiro(a)")
            .setContentText(mensagem)
            .setSmallIcon(R.drawable.ic_message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(1, notification)
    }
}
