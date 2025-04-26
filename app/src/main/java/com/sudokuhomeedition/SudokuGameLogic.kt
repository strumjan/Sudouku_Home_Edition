package com.sudokuhomeedition

interface SudokuGameLogic {
    fun getCell(row: Int, col: Int): Int
    fun setCell(row: Int, col: Int, value: Int)
    fun isCellEditable(row: Int, col: Int): Boolean
    fun isCellCorrect(row: Int, col: Int): Boolean
    fun isSolved(): Boolean
    fun getRegionId(row: Int, col: Int): Int
}
