package com.example.jogodaforca

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var wordTextView: TextView
    private lateinit var letterEditText: EditText
    private lateinit var guessButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Referências aos elementos da interface
        wordTextView = findViewById(R.id.wordTextView)
        letterEditText = findViewById(R.id.letterEditText)
        guessButton = findViewById(R.id.guessButton)

        // Exemplo de como você pode definir a palavra oculta
        val hiddenWord = "example" // Palavra secreta a ser adivinhada
        var guessedLetters = mutableListOf<Char>()

        // Atualiza o TextView para exibir as letras adivinhadas corretamente
        fun updateWordDisplay() {
            val displayedWord = StringBuilder()
            for (letter in hiddenWord) {
                if (guessedLetters.contains(letter)) {
                    displayedWord.append(letter)
                } else {
                    displayedWord.append("_")
                }
                displayedWord.append(" ")
            }
            wordTextView.text = displayedWord.toString().trim()
        }

        // Inicializa o display da palavra
        updateWordDisplay()

        // Configuração do botão de adivinhar
        guessButton.setOnClickListener {
            val guessedLetter = letterEditText.text.toString().trim().toLowerCase()

            if (guessedLetter.isNotEmpty() && guessedLetter.length == 1) {
                guessedLetters.add(guessedLetter[0])
                updateWordDisplay() // Atualiza a exibição da palavra com a letra adivinhada

                // Limpa o campo de entrada
                letterEditText.text.clear()
            }
        }
    }
}



