package com.example.timecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timecalculator.model.Duration
import com.example.timecalculator.model.Timed

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

/* ===================== NAVIGATION ===================== */

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("Calculator") }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {
            when (currentScreen) {
                "Calculator" -> CalculatorScreen()
                "Todos" -> TodoScreen()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { currentScreen = "Calculator" }) {
                Text("Calculatrice")
            }
            Button(onClick = { currentScreen = "Todos" }) {
                Text("Todos")
            }
        }
    }
}

/* ===================== CALCULATRICE ===================== */

@Composable
fun CalculatorScreen() {

    var input by remember { mutableStateOf("") }
    var current by remember { mutableStateOf(Duration.ZERO) }
    var accumulator by remember { mutableStateOf(Duration.ZERO) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Result", style = MaterialTheme.typography.labelLarge)
                Text(accumulator.toString(), style = MaterialTheme.typography.headlineLarge)

                Spacer(Modifier.height(8.dp))
                Text("Current: $current")
                Text("Input: $input")
            }
        }

        NumberPad { input += it }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            UnitButton("d") {
                input.toIntOrNull()?.let {
                    current += Duration(it, 0, 0)
                    input = ""
                }
            }
            UnitButton("h") {
                input.toIntOrNull()?.let {
                    current += Duration(0, it, 0)
                    input = ""
                }
            }
            UnitButton("m") {
                input.toIntOrNull()?.let {
                    current += Duration(0, 0, it)
                    input = ""
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton("+") {
                accumulator += current
                current = Duration.ZERO
            }
            ActionButton("=") {
                accumulator += current
                current = Duration.ZERO
            }
            ActionButton("Clear") {
                input = ""
                current = Duration.ZERO
                accumulator = Duration.ZERO
            }
        }
    }
}

/* ===================== TODO SCREEN ===================== */

@Composable
fun TodoScreen() {

    var todos by remember { mutableStateOf(listOf<Timed<String>>()) }
    var description by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    var result by remember { mutableStateOf<Timed<List<String>>?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            if (description.isNotBlank()) {
                todos = todos + Timed(Duration.ZERO, description)
                description = ""
            }
        }) {
            Text("Ajouter Todo (durée = 0)")
        }

        Spacer(Modifier.height(16.dp))

        todos.forEachIndexed { index, todo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${todo.value} — ${todo.duration}")

                Checkbox(
                    checked = selected.contains(index),
                    onCheckedChange = {
                        selected = if (it) selected + index else selected - index
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            val chosen = selected.map { todos[it] }
            result = Timed.combine(chosen)
        }) {
            Text("Finish sélection")
        }

        Spacer(Modifier.height(16.dp))

        result?.let {
            Text("⏱ Temps total : ${it.duration}")
            Text("📋 Tâches : ${it.value.joinToString()}")
        }
    }
}

/* ===================== UI HELPERS ===================== */

@Composable
fun NumberPad(onDigit: (String) -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("0")
    )

    Column {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach {
                    Button(
                        onClick = { onDigit(it) },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text(it)
                    }
                }
            }
        }
    }
}

@Composable
fun UnitButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.size(72.dp)) {
        Text(label)
    }
}

@Composable
fun ActionButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.width(100.dp)) {
        Text(label)
    }
}
