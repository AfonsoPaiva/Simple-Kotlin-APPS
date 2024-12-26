package com.example.jogodaforca

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Referência ao botão "Criar Sala"
        val createRoomButton: Button = findViewById(R.id.createRoomButton)

        // Referência ao botão "Entrar na Sala"
        val joinRoomButton: Button = findViewById(R.id.joinRoomButton)

        // Configuração do botão "Criar Sala"
        createRoomButton.setOnClickListener {
            val intent = Intent(this, CreateRoomActivity::class.java)
            startActivity(intent)
        }

        // Configuração do botão "Entrar na Sala"
        joinRoomButton.setOnClickListener {
            val intent = Intent(this, JoinRoomActivity::class.java)
            startActivity(intent)
        }
    }
}
