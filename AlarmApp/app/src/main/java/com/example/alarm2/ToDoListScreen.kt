package com.example.alarm2

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.unit.sp

@Composable
fun ToDoListScreen() {
    // State for tasks and dialogs
    val tasks = remember { mutableStateOf(initialTasks) }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var newTaskName by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }
    var longPressedTask by remember { mutableStateOf<Task?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(3.dp)) {
            // Title Box
            TitleBox()

            // Task List
            TaskList(tasks = tasks.value, onLongPress = { task ->
                longPressedTask = task
                expandedMenu = true // Show the options menu
            }, onClick = { task ->
                taskToEdit = task
                newTaskName = task.name
                showDialog = true
            })
        }

        // Floating Action Button for adding new task
        FloatingActionButton(
            onClick = {
                taskToEdit = null // Reset taskToEdit when adding a new task
                newTaskName = "" // Clear the new task name
                showDialog = true // Show the dialog
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Positioned at the bottom-right corner
                .padding(bottom = 65.dp, end = 16.dp), // Floating button padding from the edge
            backgroundColor = Color(0xFF2698CC) // Button color
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Task",
                tint = Color.White // Icon color
            )
        }

        // Show dialog to add/edit task
        if (showDialog) {
            NewTaskDialog(
                taskName = newTaskName,
                onTaskNameChange = { newTaskName = it },
                onDismiss = { showDialog = false },
                onSave = {
                    if (taskToEdit == null) {
                        // Add new task
                        tasks.value = tasks.value + Task(newTaskName, false)
                    } else {
                        // Edit existing task
                        tasks.value = tasks.value.map {
                            if (it == taskToEdit) it.copy(name = newTaskName) else it
                        }
                    }
                    showDialog = false // Close the dialog after saving
                },
                dialogType = if (taskToEdit != null) DialogType.Edit else DialogType.Add // Check if it's edit or add
            )
        }

        // Show delete confirmation dialog
        if (showDeleteDialog && taskToEdit != null) {
            DeleteTaskDialog(
                onDismiss = { showDeleteDialog = false },
                onDelete = {
                    tasks.value = tasks.value.filter { it != taskToEdit }
                    showDeleteDialog = false
                }
            )
        }

        // Show contextual menu on long press (always centered)
        if (expandedMenu) {
            Popup(
                alignment = Alignment.Center // Always centered in the screen
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Color(0xFFBDBDBD),
                            shape = RoundedCornerShape(16.dp)
                        ) // Dark background with rounded corners
                        .padding(8.dp) // Added padding around the items
                        .width(200.dp) // Fixed width for a more defined appearance
                ) {
                    DropdownMenuItem(onClick = {
                        // Show edit dialog
                        expandedMenu = false
                        taskToEdit = longPressedTask
                        newTaskName = longPressedTask?.name ?: ""
                        showDialog = true
                    }) {
                        Text("Szerkesztés", style = TextStyle(fontSize = 20.sp), fontWeight = FontWeight.Normal)
                    }
                    DropdownMenuItem(onClick = {
                        // Show delete confirmation dialog
                        expandedMenu = false
                        taskToEdit = longPressedTask
                        showDeleteDialog = true
                    }) {
                        Text("Törlés",  style = TextStyle(fontSize = 20.sp), fontWeight = FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@Composable
fun TitleBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 5.dp, end = 5.dp, bottom = 3.dp)
            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Reggeli rutin",
            style = TextStyle(fontSize = 50.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

// Task data class to store the name and checked state
data class Task(val name: String, val isChecked: Boolean)

// Initial list of tasks
val initialTasks = listOf(
    Task("Arcmosás", true),
    Task("Fogmosás", false),
    Task("Ágybevetés", false),
    Task("Reggeli", false),
    Task("Kávé", false),
    Task("Öltözés", false),
    Task("Fésülködés", false),
    Task("Bepakolás", false),
    Task("Kulcs", false),
)

// Enum for dialog type (Add or Edit)
enum class DialogType {
    Add, Edit
}

@Composable
fun NewTaskDialog(
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    dialogType: DialogType
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (dialogType == DialogType.Add) "Új feladat hozzáadása" else "Feladat szerkesztése",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(bottom = 16.dp) // Adjusted bottom padding for title
            )
        },
        text = {
            Column {
                TextField(
                    value = taskName,
                    onValueChange = onTaskNameChange,
                    textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium), // Reduced font size
                    modifier = Modifier
                        .padding(top = 16.dp, start = 3.dp, end = 3.dp) // Adjusted top padding for TextField
                        .fillMaxWidth()
                        .background(Color(0xFFF4F4F4), shape = RoundedCornerShape(8.dp))
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2698CC),
                    contentColor = Color.White),
                    modifier = Modifier.padding(end= 3.dp)

            ) {
                Text(if (dialogType == DialogType.Add) "Hozzáadás" else "Mentés")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2698CC),
                    contentColor = Color.White
                )
            ) {
                Text("Mégse")
            }
        }
    )
}

@Composable
fun DeleteTaskDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Feladat törlése")
        },
        text = {
            Text("Biztos törölni szeretnéd a feladatot?")
        },
        confirmButton = {
            Button(
                onClick = onDelete, // Delete the task
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2698CC), // Blue color for delete button
                    contentColor = Color.White // White text color
                )
            ) {
                Text("Törlés")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss, // Close the dialog without deleting
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2698CC), // Blue color for cancel button as well
                    contentColor = Color.White // White text color
                )
            ) {
                Text("Mégse")
            }
        }
    )
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onLongPress: (Task) -> Unit,
    onClick: (Task) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 3.dp, vertical = 3.dp)
            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(16.dp))
            .padding(bottom = 70.dp) // Increased bottom padding to ensure FAB doesn't overlap last item
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tasks) { task ->
                TaskItem(task, onLongPress)
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onLongPress: (Task) -> Unit) {
    // State for each checkbox
    val checkedState = remember { mutableStateOf(task.isChecked) }

    // Row for each item in the list
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp) // Space between items
            .background(Color.White, shape = RoundedCornerShape(16.dp)) // Background with rounded corners
            .padding(16.dp) // Inner padding
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress(task) }, // Trigger menu on long press
                )
            }
    ) {
        // Checkbox
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it }
        )
        Spacer(modifier = Modifier.width(16.dp)) // Space between checkbox and text
        // Task name text
        Text(
            text = task.name,
            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
