package com.sudokuhomeedition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt

class SudokuBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var game: SudokuGame? = null
    private var cellSize = 0f
    private var onCellTouchListener: ((Int, Int) -> Unit)? = null
    private var selectedRow = -1
    private var selectedCol = -1
    private val cellColors = Array(9) { Array(9) { Color.BLACK } }


    private val linePaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 2f
    }

    private val boldLinePaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 4f
    }

    private val textPaint = Paint().apply {
        color = "#0026FF".toColorInt()
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private val fixedPaint = Paint().apply {
        color = "#555555".toColorInt()
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private val errorPaint = Paint().apply {
        style = Paint.Style.FILL
        color = "#e85d04".toColorInt()
        textAlign = Paint.Align.CENTER
        textSize = 40f
        isFakeBoldText = true
    }


    fun setGame(game: SudokuGame) {
        this.game = game
        invalidate()
    }

    fun setOnCellTouchListener(listener: (Int, Int) -> Unit) {
        this.onCellTouchListener = listener
    }

    fun setSelectedCell(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
    }

    fun setCellColor(row: Int, col: Int, color: Int) {
        if (row in 0..8 && col in 0..8) {
            cellColors[row][col] = color
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec).coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        cellSize = size / 9f
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw cells
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                val value = game?.getCell(i, j) ?: 0
                val x = j * cellSize + cellSize / 2
                val y = i * cellSize + cellSize / 1.5f

                if (value != 0) {
                    val paint = when {
                        game?.isCellEditable(i, j) == false -> fixedPaint
                        game?.isCellCorrect(i, j) == false -> errorPaint
                        else -> textPaint
                    }
                    canvas.drawText(value.toString(), x, y, paint)
                }
            }
        }


        // Draw grid lines
        for (i in 0..9) {
            val paint = if (i % 3 == 0) boldLinePaint else linePaint
            canvas.drawLine(0f, i * cellSize, width.toFloat(), i * cellSize, paint)
            canvas.drawLine(i * cellSize, 0f, i * cellSize, height.toFloat(), paint)
        }

        // Во onDraw - затемни селектирана ќелија
        if (selectedRow in 0..8 && selectedCol in 0..8) {
            val highlightPaint = Paint().apply {
                color = "#44CCCCCC".toColorInt() // полупроѕирна сива
                style = Paint.Style.FILL
            }
            canvas.drawRect(
                selectedCol * cellSize,
                selectedRow * cellSize,
                (selectedCol + 1) * cellSize,
                (selectedRow + 1) * cellSize,
                highlightPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val row = (event.y / cellSize).toInt()
            val col = (event.x / cellSize).toInt()
            if (row in 0..8 && col in 0..8) {
                onCellTouchListener?.invoke(row, col)
            }
            return true
        }
        return false
    }
}
