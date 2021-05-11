package com.example.calculatorver2

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.mariuszgromada.math.mxparser.*
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {
    private lateinit var display:EditText
    private lateinit var numberButtons: Array<Button>
    private lateinit var operatorButtons: Array<Button>
    private var isOperatorClicked: Boolean = false
    private var isPointClicked: Boolean = false
    private var isNumberClicked: Boolean = false
    private var legthNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeComponents()
    }

    private fun initializeComponents() {
        display = findViewById(R.id.editDisplay)
        display.showSoftInputOnFocus = false
        display.movementMethod = ScrollingMovementMethod()
        display.isEnabled = false
        editDisplay.setOnClickListener {
            if(this.getString(R.string.display) == editDisplay.text.toString())
            {
                display.setText("")
            }

        }
        numberButtons = arrayOf(button1,button2,button3,button4,button5,button6,button7,button8,button9)
        operatorButtons = arrayOf(buttonAdd,buttonSubtract,buttonMultiply,buttonDivide,buttonExponent)
        for(i in numberButtons) { i.setOnClickListener { numberButtonClicked(i) } }
        for(i in operatorButtons) { i.setOnClickListener { operatorButtonClicked(i) } }
        buttonPoint.setOnClickListener{ buttonPointClick() }
        button0.setOnClickListener{ button0Click() }
        buttonEquals.setOnClickListener{ buttonEqualClick() }
        buttonClear.setOnClickListener{ buttonClearClick()}
        buttonDelete.setOnClickListener{ buttonDeleteClick()}
    }

    private fun buttonDeleteClick() {
        val cursorPos: Int = display.selectionStart
        val textLen: Int = display.text.length
        if(cursorPos != 0 && textLen != 0)
        {
            if(textLen < 2 && cursorPos < 2)
            {
                editDisplay.setText(getString(R.string.display))
                editDisplay.setSelection(editDisplay.selectionStart + 1)
                legthNumber = 0
                isOperatorClicked= false
                isPointClicked = false
                isNumberClicked= false
            }
            else
            {
                val oldStr: String = editDisplay.text.toString()
                val lastStr: String = oldStr.substring(cursorPos - 1)
                val beforeStr: String = oldStr.substring(cursorPos - 2)
                var selection: SpannableStringBuilder  = display.text as SpannableStringBuilder
                selection.replace(cursorPos - 1, cursorPos, "")
                display.setText(selection)
                display.setSelection(cursorPos - 1)
                val pattern: Pattern = Pattern.compile("-?\\d+(\\.\\d+)?")
                val matcher: Matcher = pattern.matcher(beforeStr)
                if(lastStr == ".")
                {
                    isPointClicked = false
                }
                else if(!matcher.matches())
                {
                    isOperatorClicked = false
                }
            }

        }
    }

    private fun buttonClearClick() {
        editDisplay.setText(getString(R.string.display))
        val cursorPos: Int = editDisplay.selectionStart + 1
        editDisplay.setSelection(cursorPos)
        legthNumber = 0
        isOperatorClicked= false
        isPointClicked = false
    }

    private fun button0Click() {
        if(getString(R.string.display) == editDisplay.text.toString())
        {
            updateText(button0.text.toString())
        }
        else{
            val oldStr: String = editDisplay.text.toString()
            val cursorPos: Int = editDisplay.selectionStart
            val s: String = oldStr.substring(cursorPos - 1)
            if(s == "0" && !isNumberClicked) { }
            else
            {
                updateText(button0.text.toString())
            }
        }
        isOperatorClicked = false
    }

    private fun buttonPointClick() {
        if(getString(R.string.display) == editDisplay.text.toString() || editDisplay.text.toString() == ""){}
        // Khi mà trước đó là số
        if(!isPointClicked)
        {
            val oldStr: String = editDisplay.text.toString()
            var cursorPos: Int = display.selectionStart

            if(cursorPos > 2 ) {
                val beforeStr: String = oldStr.substring(cursorPos - 1)
                val pattern: Pattern = Pattern.compile("-?\\d+(\\.\\d+)?")
                val matcher: Matcher = pattern.matcher(beforeStr)
                if (!matcher.matches()) {
                    updateText(button0.text.toString())
                }
            }
            updateText(buttonPoint.text.toString())
            isPointClicked = true
            isNumberClicked = true
        }
    }

    private fun numberButtonClicked(btn:Button) {
        if(legthNumber < 15)
        {
            if(getString(R.string.display) == editDisplay.text.toString() || editDisplay.text.toString() == "")
            {
                updateText(btn.text.toString())
                legthNumber++
            }
            else
            {
                val oldStr: String = editDisplay.text.toString()
                val cursorPos: Int = editDisplay.selectionStart
                val s: String = oldStr.substring(cursorPos - 1)
                if(s == "0" && !isNumberClicked) {
                    changeText(btn.text.toString())
                }
                else
                {
                    updateText(btn.text.toString())
                    legthNumber++
                }
            }
            isOperatorClicked = false
            isNumberClicked = true
        }
    }
    private fun operatorButtonClicked(btn: Button) {
        if(!(getString(R.string.display) != editDisplay.text.toString() && editDisplay.text.toString() != "")){}
        // Khi mà trước đó là số
        if(!isOperatorClicked)
        {
            updateText(btn.text.toString())
        }
        // Khi mà trước đó là 1 phép tính
        else
        {
            val oldStr: String = editDisplay.text.toString()
            val cursorPos: Int = editDisplay.selectionStart - 1
            val str: String = oldStr.substring(0,cursorPos)
            editDisplay.setText(String.format("%s%s",str,btn.text.toString()))
            editDisplay.setSelection(cursorPos + 1)
        }
        isPointClicked = false
        isOperatorClicked = true
        isNumberClicked = false
        legthNumber = 0
    }

    private fun buttonEqualClick() {
        var userExp: String = display.text.toString()
        userExp = userExp.replace("÷", "/")
        userExp = userExp.replace("x", "*")
        var exp: Expression = Expression (userExp)
        var result: String = (exp.calculate()).toString()
        display.setText(result)
        display.setSelection(result.length)
    }
    private fun changeText(str: String)
    {
        val oldStr: String = editDisplay.text.toString()
        val cursorPos: Int = editDisplay.selectionStart
        val leftStr: String = oldStr.substring(0,cursorPos - 1)
        editDisplay.setText(String.format("%s%s",leftStr,str))
        editDisplay.setSelection(cursorPos)
    }
    private fun updateText(str: String)
    {
        val oldStr: String = editDisplay.text.toString()
        val cursorPos: Int = editDisplay.selectionStart
        val leftStr: String = oldStr.substring(0,cursorPos)
        val rightStr: String = oldStr.substring(cursorPos)
        if(getString(R.string.display) == editDisplay.text.toString() )
        {
            editDisplay.setText(str)
        }
        else
        {
            editDisplay.setText(String.format("%s%s%s",leftStr,str,rightStr))
        }
        editDisplay.setSelection(cursorPos + 1)
    }

}
