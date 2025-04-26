package com.sudokuhomeedition

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sudokuhomeedition.databinding.ActivityGameBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var boardView: SudokuBoardView
    private lateinit var sudokuGame: SudokuGameLogic
    private var selectedRow = -1
    private var selectedCol = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        boardView = findViewById(R.id.sudokuBoard)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        Handler(Looper.getMainLooper()).postDelayed({
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }, 30 * 60 * 1000) // 30 minutes in milliseconds

        val difficultyStr = intent.getStringExtra("difficulty") ?: "EASY"
        val isJigsaw = intent.getBooleanExtra("isJigsaw", false)

        val difficulty = when (difficultyStr.uppercase()) {
            "MEDIUM" -> SudokuGame.Difficulty.MEDIUM
            "HARD" -> SudokuGame.Difficulty.HARD
            else -> SudokuGame.Difficulty.EASY
        }

        lifecycleScope.launch {
            if (isJigsaw) {
                binding.progressBar.visibility = View.VISIBLE

                val game = withContext(Dispatchers.Default) {
                    JigsawSudokuGame(difficulty)
                }

                sudokuGame = game
                boardView = findViewById(R.id.sudokuBoard)
                boardView.setGame(sudokuGame)

                binding.progressBar.visibility = View.GONE
            } else {
                sudokuGame = SudokuGame(difficulty)
                boardView = findViewById(R.id.sudokuBoard)
                boardView.setGame(sudokuGame)
            }
        }


        boardView.setOnCellTouchListener { row, col ->
            if (sudokuGame.isCellEditable(row, col)) {
                selectedRow = row
                selectedCol = col
                boardView.setSelectedCell(row, col)
                boardView.invalidate()
            }
        }

        binding.apply {
            val buttons = listOf(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)
            buttons.forEachIndexed { index, button ->
                button.setOnClickListener {
                    if (selectedRow in 0..8 && selectedCol in 0..8) {
                        sudokuGame.setCell(selectedRow, selectedCol, index + 1)
                        boardView.setSelectedCell(selectedRow, selectedCol)
                        boardView.invalidate()
                        if (sudokuGame.isSolved()) {
                            val inflater = layoutInflater
                            val layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.toast_layout_root))
                            val toast = Toast(applicationContext)
                            toast.duration = Toast.LENGTH_LONG
                            toast.view = layout
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                    }
                }
            }

            btnErase.setOnClickListener {
                if (selectedRow in 0..8 && selectedCol in 0..8) {
                    sudokuGame.setCell(selectedRow, selectedCol, 0)
                    boardView.invalidate()
                }
            }

            btnNew.setOnClickListener {
                finish()
            }

            btnExit.setOnClickListener {
                finishAffinity()
            }
        }
    }
}
