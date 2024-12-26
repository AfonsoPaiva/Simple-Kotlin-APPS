package com.example.jogodaforca

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.jogodaforca.GameActivity
import com.example.jogodaforca.R

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var roomIdEditText: EditText
    private lateinit var joinRoomButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)

        // Referência ao EditText para o ID da sala e ao botão
        roomIdEditText = findViewById(R.id.roomIdEditText)
        joinRoomButton = findViewById(R.id.joinRoomButton)

        // Ação do botão para entrar na sala
        joinRoomButton.setOnClickListener {
            val roomId = roomIdEditText.text.toString()

            if (roomId.isNotEmpty()) {
                // Lógica para tentar entrar na sala com o roomId
                // Exemplo de navegação para a próxima tela (onde o jogo acontece)
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            } else {
                // Mostrar uma mensagem de erro caso o ID da sala esteja vazio
                roomIdEditText.error = "O ID da sala não pode estar vazio!"
            }
        }
    }
}
