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

        /// Classic
        binding.btnEasy.setOnClickListener { startGame("EASY", false) }
        binding.btnMedium.setOnClickListener { startGame("MEDIUM", false) }
        binding.btnHard.setOnClickListener { startGame("HARD", false) }

        // Jigsaw
        binding.btnEasyJigsaw.setOnClickListener { startGame("EASY", true) }
        binding.btnMediumJigsaw.setOnClickListener { startGame("MEDIUM", true) }
        binding.btnHardJigsaw.setOnClickListener { startGame("HARD", true) }
    }

    private fun startGame(difficulty: String, isJigsaw: Boolean) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("difficulty", difficulty)
            putExtra("isJigsaw", isJigsaw)
        }
        startActivity(intent)
    }
}