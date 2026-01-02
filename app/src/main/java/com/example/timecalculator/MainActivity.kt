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
import kotlinx.coroutines.delay

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
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { currentScreen = "Calculator" }) { Text("Calculatrice") }
            Button(onClick = { currentScreen = "Todos" }) { Text("Todos") }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var input by remember { mutableStateOf("") }
    var current by remember { mutableStateOf(Duration.ZERO) }
    var accumulator by remember { mutableStateOf(Duration.ZERO) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Result", style = MaterialTheme.typography.labelLarge)
                Text(accumulator.toString(), style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(8.dp))
                Text("Current: $current", style = MaterialTheme.typography.bodyMedium)
                Text("Input: $input", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(16.dp))

        NumberPad { input += it }

        Spacer(Modifier.height(12.dp))

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

        Spacer(Modifier.height(12.dp))

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

@Composable
fun NumberPad(onDigit: (String) -> Unit) {
    val rows = listOf(
        listOf("1","2","3"),
        listOf("4","5","6"),
        listOf("7","8","9"),
        listOf("0")
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { digit ->
                    Button(onClick = { onDigit(digit) }, modifier = Modifier.size(80.dp)) {
                        Text(digit, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun UnitButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.size(72.dp)) {
        Text(label, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun ActionButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(56.dp)
            .width(100.dp)
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val days = totalSeconds / (24 * 3600)
    val hours = (totalSeconds % (24 * 3600)) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return buildString {
        if (days > 0) append("${days}d ")
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        append("${seconds}s")
    }
}

@Composable
fun TodoScreen() {
    var todos by remember { mutableStateOf(listOf<Timed<String>>()) }
    var startTimes by remember { mutableStateOf(mutableMapOf<String, Long>()) }
    var finishedFlags by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Nouvelle Todo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            if (description.isNotBlank()) {
                startTimes[description] = System.currentTimeMillis()
                todos = todos + Timed(Duration.ZERO, description)
                finishedFlags[description] = false
                description = ""
            }
        }) { Text("Ajouter Todo") }

        Spacer(Modifier.height(16.dp))

        todos.forEachIndexed { index, todo ->
            val finished = finishedFlags[todo.value] ?: false

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Texte rayé si terminé, sinon normal
                Text(
                    text = if (finished) {
                        val duration = todo.duration
                        "✔ ${todo.value} (${duration})"
                    } else todo.value,
                    style = if (finished)
                        MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    else MaterialTheme.typography.bodyLarge
                )

                Row {
                    // Bouton Finish uniquement si pas encore terminé
                    if (!finished) {
                        Button(onClick = {
                            val endDuration = Duration(
                                0, 0,
                                ((System.currentTimeMillis() - startTimes[todo.value]!!) / 60000).toInt()
                            )
                            todos = todos.toMutableList().also { it[index] = Timed(endDuration, todo.value) }
                            finishedFlags[todo.value] = true
                        }) { Text("Finish") }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Button(onClick = {
                        todos = todos.toMutableList().also { it.removeAt(index) }
                        startTimes.remove(todo.value)
                        finishedFlags.remove(todo.value)
                    }) { Text("Delete") }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

