package com.sudokuhomeedition

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sudokuhomeedition.databinding.ActivityGameBinding
import androidx.core.graphics.toColorInt

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var boardView: SudokuBoardView
    private lateinit var sudokuGame: SudokuGame
    private var selectedRow = -1
    private var selectedCol = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        Handler(Looper.getMainLooper()).postDelayed({
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }, 10 * 60 * 1000) // 10 minutes in milliseconds


        val difficulty = intent.getStringExtra("difficulty") ?: "EASY"
        sudokuGame = SudokuGame(
            when (difficulty) {
                "MEDIUM" -> SudokuGame.Difficulty.MEDIUM
                "HARD" -> SudokuGame.Difficulty.HARD
                else -> SudokuGame.Difficulty.EASY
            }
        )

        boardView = findViewById(R.id.sudokuBoard)
        boardView.setGame(sudokuGame)

        boardView.setOnCellTouchListener { row, col ->
            if (sudokuGame.isCellEditable(row, col)) {
                selectedRow = row
                selectedCol = col
                boardView.setSelectedCell(row, col)
                boardView.invalidate()
            }
        }

        binding.btnNew.setOnClickListener {
            finish() // враќа на почетниот екран
        }

        binding.btnExit.setOnClickListener {
            finishAffinity() // целосно затворање на апликацијата
        }

        binding.apply {
            // Number pad actions
            val numberButtons = listOf(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)
            numberButtons.forEachIndexed { index, button ->
                button.setOnClickListener {
                    if (selectedRow in 0..8 && selectedCol in 0..8 && sudokuGame.isCellEditable(selectedRow, selectedCol)) {
                        val number = index + 1
                        sudokuGame.setCell(selectedRow, selectedCol, number)

                        val isValid = sudokuGame.isMoveValid(selectedRow, selectedCol, number)

                        // Сетира боја врз основа на точноста
                        boardView.setCellColor(
                            selectedRow,
                            selectedCol,
                            if (isValid) Color.BLACK else "#e85d04".toColorInt()
                        )

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
                if (selectedRow in 0..8 && selectedCol in 0..8 && sudokuGame.isCellEditable(selectedRow, selectedCol)) {
                    sudokuGame.setCell(selectedRow, selectedCol, 0)

                    boardView.setCellColor(selectedRow, selectedCol, Color.BLACK) // враќа нормална боја
                    boardView.invalidate()
                }
            }

            btnNew.setOnClickListener {
                finish() // враќа на почетен екран
            }

            btnExit.setOnClickListener {
                finishAffinity() // затвора целата апликација
            }
        }

    }

}
