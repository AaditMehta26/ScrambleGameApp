package com.example.scrambleapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var dictionary: List<String>
    private lateinit var scrambledWord: String
    private lateinit var wordList: List<String>
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scrambledWordTextView: TextView = findViewById(R.id.scrambledWordTextView)
        val userInput: EditText = findViewById(R.id.userInput)
        val checkButton: Button = findViewById(R.id.checkButton)
        val skipButton: Button = findViewById(R.id.skipButton)
        val resultTextView: TextView = findViewById(R.id.resultTextView)
        val scoreTextView: TextView = findViewById(R.id.scoreTextView)

        // Load the dictionary
        dictionary = loadDictionary()

        // Initialize the game
        loadNewScrambledWord(scrambledWordTextView)

        // Check user input
        checkButton.setOnClickListener {
            val userWord = userInput.text.toString().trim()
            if (userWord in wordList && isValidWord(userWord, scrambledWord)) {
                score += userWord.length
                resultTextView.text = "Correct! '$userWord' is a valid word."
                scoreTextView.text = "Score: $score"
                userInput.text.clear()
            } else {
                resultTextView.text = "Incorrect! Try again."
            }
        }

        // Skip to the next word
        skipButton.setOnClickListener {
            loadNewScrambledWord(scrambledWordTextView)
            resultTextView.text = ""
            userInput.text.clear()
        }
    }

    private fun loadDictionary(): List<String> {
        return try {
            val inputStream = assets.open("dictionary.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { it.toList() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun loadNewScrambledWord(scrambledWordTextView: TextView) {
        val randomWord = dictionary.random()
        scrambledWord = randomWord.toCharArray().apply { shuffle() }.concatToString()
        wordList = dictionary.filter { it.all { char -> randomWord.contains(char) } }
        scrambledWordTextView.text = "Scrambled Word: $scrambledWord"
    }

    private fun isValidWord(word: String, scrambled: String): Boolean {
        val scrambledCounts = scrambled.groupingBy { it }.eachCount()
        val wordCounts = word.groupingBy { it }.eachCount()

        // Check if all characters in the word can be found in scrambled with sufficient counts
        return wordCounts.all { (char, count) ->
            scrambledCounts[char] ?: 0 >= count
        }
    }
}
