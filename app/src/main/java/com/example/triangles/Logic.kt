package com.example.triangles

import android.util.Log

class Logic {
    private var day: Int? = null
    private var month: Int? = null
    private var year: Int? = null

    private val triangles = arrayOf(
        { getDay() }, { getMonth() }, { getYear() }, { getFourthUpper() }, { getFifthUpper() }
    )

    fun setDate(day: Int, month: Int, year: Int) {
        this.day = day
        this.month = month
        this.year = year
    }

    fun getUpperCorner(index: Int): Int {
        return triangles[index]()
    }

    fun getLowerCorner(indexOne: Int, indexTwo: Int): Int {
        Log.d("CORNERS", "$indexOne and $indexTwo : ${cutIndex(indexOne)} and ${cutIndex(indexTwo)}")
        return firstRule(triangles[cutIndex(indexOne)]() + triangles[cutIndex(indexTwo)]())
    }

    fun getInnerLeftUpperCorner(triangleIndex: Int): Int {
        return firstRule(triangles[triangleIndex]() + getLowerCorner(triangleIndex, triangleIndex - 1))
    }

    fun getInnerRightUpperCorner(triangleIndex: Int): Int {
        return firstRule(triangles[triangleIndex]() + getLowerCorner(triangleIndex, triangleIndex + 1))
    }

    fun getInnerLowerCorner(triangleIndex: Int): Int {
        return firstRule(getLowerCorner(triangleIndex, triangleIndex + 1) + getLowerCorner(triangleIndex, triangleIndex - 1))
    }

    fun missionCalculate(): String {
        val two = firstRule(getUpperCorner(3) + getUpperCorner(4))
        val three = firstRule(getLowerCorner(0, 4))
        val four = firstRule(two + three)
        return "$two; " +
                "$three; " +
                "$four"
    }

    private fun getDay(): Int {
        return firstRule(day ?: 0)
    }

    private fun getMonth(): Int {
        return month ?: 0
    }

    private fun getYear(): Int {
        return addingDigits(year ?: 0)
    }

    private fun getFourthUpper(): Int {
        return firstRule(getDay() + getMonth() + getYear())
    }

    private fun getFifthUpper(): Int {
        return firstRule(getDay() + getMonth() + getYear() + getFourthUpper())
    }

    private fun firstRule(number: Int): Int {
        if (number > 22) {
            return addingDigits(number)
        }
        return number
    }

    private fun addingDigits(number: Int): Int {
        var change = number
        var digit = change % 10
        var sum = 0
        while (change > 0) {
            sum += digit

            change /= 10
            digit = change %10
        }
        return sum
    }

    private fun cutIndex(index: Int): Int {
        val x = if (index >= 0) index % 5
        else 5 + index % 5
        return x
    }
}