package com.example.project1cpsc411a

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Todo(var text: String, var isdone: Boolean = false)


private val todoListSaver = listSaver<List<Todo>, Any>(
    save = { list -> list.flatMap { listOf(it.text, it.isdone) } },
    restore = { raw -> raw.chunked(2).map { (t, d) -> Todo(t as String, d as Boolean) } }
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TodoApplication() }
    }
}

@Composable
fun TodoApplication() {
    // ✅ survives rotation now
    var listofTodoItems by rememberSaveable(stateSaver = todoListSaver) {
        mutableStateOf(emptyList())
    }
    var userinput by rememberSaveable { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        Text("TODO List")

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = userinput,
                onValueChange = { userinput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter task") }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (userinput.isNotBlank()) {
                        listofTodoItems = listofTodoItems + Todo(userinput.trim())
                        userinput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.Black
                )
            ) { Text("Add") }
        }

        Spacer(Modifier.height(16.dp))
        Text("Items")
        TaskList(
            list = listofTodoItems.filter { !it.isdone },
            onToggle = { t -> listofTodoItems = listofTodoItems.map { if (it == t) it.copy(isdone = true) else it } },
            onRemove = { t -> listofTodoItems = listofTodoItems - t }
        )
        if (listofTodoItems.none { !it.isdone }) Text("No items yet")

        Spacer(Modifier.height(16.dp))
        Text("Completed Items")
        TaskList(
            list = listofTodoItems.filter { it.isdone },
            onToggle = { t -> listofTodoItems = listofTodoItems.map { if (it == t) it.copy(isdone = false) else it } },
            onRemove = { t -> listofTodoItems = listofTodoItems - t }
        )
    }
}

@Composable
fun TaskList(
    list: List<Todo>,
    onToggle: (Todo) -> Unit,
    onRemove: (Todo) -> Unit
) {
    Column {
        for (t in list) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = t.text,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = t.isdone,
                        onCheckedChange = { onToggle(t) }
                    )
                    IconButton(onClick = { onRemove(t) }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove")
                    }
                }
            }
        }
    }
}
