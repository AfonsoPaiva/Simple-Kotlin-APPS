package com.example.jogodaforca

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var roomIdEditText: EditText
    private lateinit var joinRoomButton: Button
    private lateinit var playerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)

        playerName = intent.getStringExtra("playerName") ?: "Player"

        roomIdEditText = findViewById(R.id.roomIdEditText)
        joinRoomButton = findViewById(R.id.joinRoomButton)

        joinRoomButton.setOnClickListener {
            val roomId = roomIdEditText.text.toString()
            if (roomId.isNotEmpty()) {
                val database = FirebaseDatabase.getInstance().reference
                val roomRef = database.child("rooms").child(roomId)

                roomRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val room = snapshot.getValue(Room::class.java)
                        room?.let {
                            if (room.currentPlayers < room.maxPlayers) {
                                val updatedPlayers = room.players.toMutableList()
                                updatedPlayers.add(playerName)
                                roomRef.child("players").setValue(updatedPlayers)
                                roomRef.child("currentPlayers").setValue(room.currentPlayers + 1)

                                val intent = Intent(this, WaitRoomActivity::class.java)
                                intent.putExtra("roomId", roomId)
                                startActivity(intent)
                            } else {
                                roomIdEditText.error = "Sala cheia!"
                            }
                        }
                    } else {
                        roomIdEditText.error = "Sala não encontrada!"
                    }
                }
            } else {
                roomIdEditText.error = "O ID da sala não pode estar vazio!"
            }
        }
    }
}