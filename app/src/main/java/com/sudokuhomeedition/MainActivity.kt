package com.sudokuhomeedition

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudokuhomeedition.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEasy.setOnClickListener {
            startGame("EASY")
        }
        binding.btnMedium.setOnClickListener {
            startGame("MEDIUM")
        }
        binding.btnHard.setOnClickListener {
            startGame("HARD")
        }
    }

    private fun startGame(difficulty: String) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("difficulty", difficulty)
        startActivity(intent)
    }
}