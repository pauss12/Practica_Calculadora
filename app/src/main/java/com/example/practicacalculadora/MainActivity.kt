package com.example.practicacalculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import com.example.practicacalculadora.databinding.ActivityMainBinding
import java.lang.StringBuilder
import java.util.Stack
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private var historial: StringBuilder = StringBuilder()
    private var buffer: StringBuilder = StringBuilder()
    private lateinit var operacion: String
    private var numero: Double = 0.0
    private var lastOperation: String? = null

    private var result: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {

            historial = StringBuilder(savedInstanceState.getString("historial", ""))

            buffer = StringBuilder(savedInstanceState.getString("buffer", ""))

            result = savedInstanceState.getDouble("result", 0.0)  // Recupera result

            println(result)

            // Aplica la condición para mostrar el resultado
            if (result.isWholeNumber()) {
                binding.textoValor.text = result.toInt().toString()
            } else {
                binding.textoValor.text = result.toString()
            }

        }

        binding.textoValorGuardado.text = historial
        //binding.textoValor.text = buffer

        val buttons = listOf(
            binding.botonAC, binding.botonCero, binding.botonUno, binding.botonDos, binding.botonTres,
            binding.botonCuatro, binding.botonCinco, binding.botonSeis, binding.botonSiete,
            binding.botonOcho, binding.botonNueve, binding.botonSuma, binding.botonResta,
            binding.botonMultiplicacion, binding.botonDivision, binding.botonPunto, binding.botonIgual,
            binding.botonParentesisAbiertos, binding.botonParentesisCierre
        )

        for (button in buttons) {
            button?.setOnClickListener(this)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)

        outState.putString("historial", historial.toString())

        if (result == 0.0)
        {
            outState.putString("buffer", buffer.toString())
        }
        else
        {

            outState.putString("buffer",result.toString())
        }

        outState.putDouble("result", result)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.botonAC -> {

                numero = 0.0

                operacion = ""
                binding.textoValor.text = ""
                result = 0.0
                binding.textoValorGuardado.text = ""
                historial.clear()
                buffer.clear()
                lastOperation = null

            }

            //BOTON COMA -----------------------------
            R.id.botonPunto -> {
                operacion = "."
                historial.append(".")
                buffer.append(".")

            }

            //BOTON SUMA ---------------------------
            R.id.botonSuma -> {

                historial.append(" + ")
                operacion = "+"
                buffer.clear()
                binding.textoValorGuardado.text = historial
                numero = 0.0
            }

            //BOTON PARA PONER  PARENTESIS -----------------------------
            R.id.botonParentesisCierre -> {
                historial.append(")")
                binding.textoValorGuardado.text = historial

            }

            R.id.botonParentesisAbiertos -> {
                historial.append("(")
                binding.textoValorGuardado.text = historial
            }


            //BOTON RESTA ---------------------------
            R.id.botonResta -> {

                operacion = "-"
                historial.append(" - ")
                buffer.clear()
                binding.textoValorGuardado.text = historial
                numero = 0.0
            }

            //BOTON MULTIPLICACION -----------------
            R.id.botonMultiplicacion -> {

                operacion = "*"
                historial.append(" * ")
                buffer.clear()
                binding.textoValorGuardado.text = historial
                numero = 0.0
            }

            //BOTON  DIVISION-----------------
            R.id.botonDivision -> {

                operacion = "/"
                historial.append(" / ")
                buffer.clear()
                binding.textoValorGuardado.text = historial
                numero = 0.0
            }

            R.id.botonIgual -> {

                if (historial.isNotEmpty()) {

                    result = calculate(historial)

                    if (result.isWholeNumber()) {
                        binding.textoValor.text = result.toInt().toString()
                    } else {
                        binding.textoValor.text = result.toString()
                    }

                }

            }

            else -> {

                val numberString = (v as Button).text.toString()

                historial.append(numberString)
                buffer.append(numberString)

                binding.textoValor.text = buffer.toString()

                binding.textoValorGuardado.text = historial


            }
        }
    }

    private fun Double.isWholeNumber(): Boolean {
        return this % 1.0 == 0.0 // Check if remainder after division by 1 is 0
    }


    fun calculate(expression: StringBuilder): Double {

        val operators: Stack<Char> = Stack<Char>()
        val results: Stack<Double> = Stack<Double>()

        val tokens = expression.split(" ") // Divide en tokens separados por espacios

        for (token in tokens) {
            when (token) {
                // Números
                in "0".."9" -> results.add(token.toDouble())

                // Operadores
                "+", "-", "*", "/" -> {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token[0])) {
                        val operand2 = results.pop()
                        val operand1 = results.pop()
                        val operator = operators.pop()
                        results.add(performOperation(operator, operand1, operand2))
                    }
                    operators.push(token[0])
                }

                else -> {

                    Log.e("Error", "Token no válido: $token")
                }
            }
        }

        while (!operators.isEmpty()) {
            val operand2 = results.pop()
            val operand1 = results.pop()
            val operator = operators.pop()
            results.add(performOperation(operator, operand1, operand2))
        }

        return results.lastOrNull() ?: Double.NaN
    }

    fun precedence(operator: Char): Int {
        return when (operator) {
            '+', '-' -> 1
            '*', '/' -> 2
            else -> -1
        }
    }

    fun performOperation(operator: Char, operand1: Double, operand2: Double): Double {
        return when (operator) {
            '+' -> operand1 + operand2
            '-' -> operand1 - operand2
            '*' -> operand1 * operand2
            '/' -> {
                if (operand2 == 0.0) {

                    Log.e("Error", "División por cero!")
                    Double.NaN
                } else {
                    operand1 / operand2
                }
            }
            else -> Double.NaN
        }
    }



}
