package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import java.util.Stack

class MainActivity : AppCompatActivity() {
    private var display:TextView? = null
    private var lastNumeric= false
    private var lastDot = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById<TextView>(R.id.display)
    }

    fun onDigit(view:View) {
        display?.append((view as Button).text)
        lastNumeric = true
        lastDot = false
    }
    fun onClear(view:View)
    {
        display?.text=""
    }

    fun onDecimalPoint(view:View)
    {
        if(lastNumeric && !lastDot)
        {
            display?.append(".")
            lastDot= true
            lastNumeric= false
        }
    }


    fun onOperator(view: View)
    {
        var displayValue = display?.text?.toString()

        if(display?.text?.isEmpty() == true)
        {
            if((view as Button).text == "-") {
                display?.append("-")
                return
            }
            else
                return
        }
        display?.append((view as Button).text)
    }


    fun onEqual(view:View)
    {
        var displayValue = display?.text?.toString()

        if(displayValue!!.isEmpty())
            return
        if(displayValue?.last() == '+' || displayValue?.last() == '×' || displayValue?.last() =='-'
            || displayValue?.last() == '/')
            return
        if(isOperatorTogether(displayValue)) {
            display?.text = "error"
            return
        }
        var ans: String? = displayValue?.let { calculate(it) }
        display?.setText(ans)


    }

    fun calculate(expression:String):String
    {
        val stack = Stack<Double>()
        val operators = Stack<Char>()

        fun applyOperator() {
            val operator = operators.pop()
            val b = stack.pop()
            val a = stack.pop()
            when (operator) {
                '+' -> stack.push(a + b)
                '-' -> stack.push(a - b)
                '*' -> stack.push(a * b)
                '/' -> stack.push(a / b)
            }
        }

        val cleanExpression = expression.replace(" ", "")
        var i = 0

        while (i < cleanExpression.length) {
            val c = cleanExpression[i]

            if (c.isDigit() || (c == '-' && (i == 0 || cleanExpression[i - 1] in "+-*/"))) {
                var numBuffer = StringBuilder()
                numBuffer.append(c)
                i++

                while (i < cleanExpression.length && (cleanExpression[i].isDigit() || cleanExpression[i] == '.')) {
                    numBuffer.append(cleanExpression[i])
                    i++
                }

                stack.push(numBuffer.toString().toDouble())
            } else if (c in "+-*/") {
                while (operators.isNotEmpty() && operators.peek() in "*/" && c in "+-") {
                    applyOperator()
                }
                operators.push(c)
                i++
            } else if (c == '(') {
                operators.push(c)
                i++
            } else if (c == ')') {
                while (operators.isNotEmpty() && operators.peek() != '(') {
                    applyOperator()
                }
                if (operators.isEmpty() || operators.peek() != '(') {
                    return "Error: Invalid expression"
                }
                operators.pop()
                i++
            } else {
                return "Error: Invalid character in expression"
            }
        }

        while (operators.isNotEmpty()) {
            if (operators.peek() == '(') {
                return "Error: Invalid expression"
            }
            applyOperator()
        }

        return if (stack.size == 1) {
            stack.pop().toString()
        } else {
            "Error: Invalid expression"
        }
    }

    fun isOperatorTogether(str:String?): Boolean
    {
        str?.let {
            var previous = it[0]
            var s = it.substring(1)
            for (i in s) {
                if(isOperator(previous) && isOperator(i))
                    return true
                previous = i
            }
            return false
        }
        return false
    }

    fun isOperator(chr:Char): Boolean
    {
        return chr=='/' || chr=='+' || chr=='-' || chr=='×'
    }

}