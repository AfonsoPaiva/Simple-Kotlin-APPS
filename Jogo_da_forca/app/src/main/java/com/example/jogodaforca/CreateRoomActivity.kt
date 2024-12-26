package com.example.jogodaforca

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class CreateRoomActivity : AppCompatActivity() {

    private lateinit var roomNameEditText: EditText
    private lateinit var maxPlayersEditText: EditText
    private lateinit var createRoomButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        roomNameEditText = findViewById(R.id.roomNameEditText)
        maxPlayersEditText = findViewById(R.id.maxPlayersEditText)
        createRoomButton = findViewById(R.id.createRoomButton)

        createRoomButton.setOnClickListener {
            val roomName = roomNameEditText.text.toString()
            val maxPlayers = maxPlayersEditText.text.toString().toIntOrNull()

            // Verifica se os campos são válidos
            if (roomName.isNotEmpty() && maxPlayers != null && maxPlayers > 1) {
                val database = FirebaseDatabase.getInstance().reference
                val roomRef = database.child("rooms").push()
                val roomId = roomRef.key ?: return@setOnClickListener

                // Criar a sala no Firebase
                val roomData = mapOf(
                    "host" to "HostPlayer", // Você pode substituir por um nome dinâmico do host
                    "max_players" to maxPlayers,
                    "current_players" to 1,
                    "game_started" to false,
                    "players" to mapOf("host_player_id" to "HostPlayer") // A estrutura dos jogadores
                )

                roomRef.setValue(roomData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sala criada com sucesso, agora navega para a tela de espera
                        val intent = Intent(this, WaitRoomActivity::class.java)
                        intent.putExtra("roomId", roomId) // Passando o roomId para a próxima atividade
                        startActivity(intent)
                    } else {
                        // Caso haja algum erro ao salvar a sala
                        Toast.makeText(this, "Erro ao criar a sala. Tente novamente.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Exibe mensagem de erro caso os campos estejam incorretos
                Toast.makeText(this, "Por favor, preencha os campos corretamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
