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

    private var game: SudokuGameLogic? = null
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


    fun setGame(game: SudokuGameLogic) {
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

        cellSize = width / 9f
        drawCellGridLines(canvas)
        drawRegions(canvas)
        drawJigsawBorders(canvas)
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

    private fun drawCellGridLines(canvas: Canvas) {
        for (i in 0..9) {
            // Хоризонтални линии
            canvas.drawLine(
                0f, i * cellSize,
                width.toFloat(), i * cellSize,
                linePaint
            )
            // Вертикални линии
            canvas.drawLine(
                i * cellSize, 0f,
                i * cellSize, height.toFloat(),
                linePaint
            )
        }
    }

    private fun getRegionCells(regionId: Int): List<Pair<Int, Int>> {
        val cells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                if (game?.getRegionId(i, j) == regionId) {
                    cells.add(i to j)
                }
            }
        }
        return cells
    }

    private fun drawJigsawBorders(canvas: Canvas) {
        val size = width / 9f
        val visited = mutableSetOf<Pair<Int, Int>>()

        for (regionId in 0..8) {
            val cells = getRegionCells(regionId)
            for ((row, col) in cells) {
                // Секое поле го гледаме дали е на работ
                if (col == 0 || game?.getRegionId(row, col - 1) != regionId) {
                    canvas.drawLine(col * size, row * size, col * size, (row + 1) * size, boldLinePaint)
                }
                if (row == 0 || game?.getRegionId(row - 1, col) != regionId) {
                    canvas.drawLine(col * size, row * size, (col + 1) * size, row * size, boldLinePaint)
                }
                if (col == 8 || game?.getRegionId(row, col + 1) != regionId) {
                    canvas.drawLine((col + 1) * size, row * size, (col + 1) * size, (row + 1) * size, boldLinePaint)
                }
                if (row == 8 || game?.getRegionId(row + 1, col) != regionId) {
                    canvas.drawLine(col * size, (row + 1) * size, (col + 1) * size, (row + 1) * size, boldLinePaint)
                }
            }
        }
    }

    private fun drawRegions(canvas: Canvas) {
        val size = width / 9f

        // Дефинирање уникатни бои за секој регион
        val regionColors = arrayOf(
            "#44FFCDD2".toColorInt(), // Пастелно розева
            "#44BBDEFB".toColorInt(), // Пастелно светло сина
            "#44C8E6C9".toColorInt(), // Пастелно зелена
            "#44FFF9C4".toColorInt(), // Пастелно жолта
            "#44B2EBF2".toColorInt(), // Пастелно тиркизна
            "#44E1BEE7".toColorInt(), // Пастелно виолетова
            "#44D7CCC8".toColorInt(), // Пастелно сива
            "#44F0F4C3".toColorInt(), // Пастелно светло зелена
            "#44FFECB3".toColorInt()  // Пастелно крем
        )


        // Обоени региони
        for (regionId in 0..8) { // Итерираме преку секој регион
            val paint = Paint()
            paint.color = regionColors[regionId]

            for (row in 0 until 9) {
                for (col in 0 until 9) {
                    if (game?.getRegionId(row, col) == regionId) {
                        // Боење на сите ќелии што припаѓаат на истиот регион
                        canvas.drawRect(
                            col * size, row * size, (col + 1) * size, (row + 1) * size, paint
                        )
                    }
                }
            }
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
