package com.example.timecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timecalculator.model.Duration
import com.example.timecalculator.model.Todo
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
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Result",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = accumulator.toString(),
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Current: $current",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Input: $input",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        NumberPad(
            onDigit = { input += it }
        )

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

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { digit ->
                    Button(
                        onClick = { onDigit(digit) },
                        modifier = Modifier
                            .size(80.dp)
                    ) {
                        Text(
                            text = digit,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnitButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(72.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge
        )
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
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("Calculator") }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when (currentScreen) {
                "Calculator" -> CalculatorScreen()  // ton code existant
                "Todos" -> TodoScreen()             // la nouvelle page
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
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
    var todos by remember { mutableStateOf(listOf<Todo>()) }
    var description by remember { mutableStateOf("") }

    // Coroutine pour mettre à jour le chrono toutes les secondes
    LaunchedEffect(todos) {
        while (true) {
            delay(1000)
            todos = todos.toList() // Force la recomposition pour les timers
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Champ pour ajouter une nouvelle Todo
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Nouvelle Todo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            if (description.isNotBlank()) {
                todos = todos + Todo(description)
                description = ""
            }
        }) {
            Text("Ajouter Todo")
        }

        Spacer(Modifier.height(16.dp))

        // Liste des Todos
        todos.forEachIndexed { index, todo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Texte avec ✔ si terminé et durée formatée
                Text(
                    text = if (todo.finished)
                        "✔ ${todo.description} (${formatDuration(todo.durationMillis())})"
                    else
                        "${todo.description} (${formatDuration(System.currentTimeMillis() - todo.startTime)})"
                )

                Row {
                    // Bouton Finish (uniquement si la tâche n'est pas terminée)
                    if (!todo.finished) {
                        Button(onClick = {
                            todos = todos.toMutableList().also {
                                it[index] = it[index].copy(
                                    finished = true,
                                    endTime = System.currentTimeMillis()
                                )
                            }
                        }) {
                            Text("Finish")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Bouton Delete
                    Button(onClick = {
                        todos = todos.toMutableList().also {
                            it.removeAt(index)
                        }
                    }) {
                        Text("Delete")
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}


