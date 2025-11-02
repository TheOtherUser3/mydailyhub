package com.example.mydailyhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mydailyhub.ui.theme.MydailyhubTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween


// ------------------ ROUTES ------------------
sealed class Route(val base: String, val withArg: String) {
    object Notes : Route("notes", "notes/{anim}")
    object Tasks : Route("tasks", "tasks/{anim}")
    object Calendar : Route("calendar", "calendar/{anim}")
    companion object {
        const val ARG_ANIM = "anim"
    }
}

// ------------------ DATA ------------------
data class TaskItem(val id: Int, val title: String, val done: Boolean)

// ------------------ VIEWMODELS ------------------
class NotesViewModel : ViewModel() {
    private var nextId = 1
    private val _notes = mutableStateListOf<Pair<Int, String>>()
    val notes: List<Pair<Int, String>> get() = _notes
    fun addNote(text: String) {
        if (text.isNotBlank()) _notes.add(0, nextId++ to text)
    }
}

class TasksViewModel : ViewModel() {
    private var nextId = 1
    private val _tasks = mutableStateListOf<TaskItem>()
    val tasks: List<TaskItem> get() = _tasks
    fun addTask(title: String) {
        if (title.isNotBlank()) _tasks.add(0, TaskItem(nextId++, title, false))
    }
    fun toggle(id: Int) {
        val idx = _tasks.indexOfFirst { it.id == id }
        if (idx != -1) {
            val t = _tasks[idx]
            _tasks[idx] = t.copy(done = !t.done)
        }
    }
}

// ------------------ MAIN ACTIVITY ------------------
class MainActivity : ComponentActivity() {
    private val notesVM: NotesViewModel by viewModels()
    private val tasksVM: TasksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MydailyhubTheme {
                MyDailyHubApp(notesVM, tasksVM)
            }
        }
    }
}

// ------------------ APP ROOT ------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDailyHubApp(notesVM: NotesViewModel, tasksVM: TasksViewModel) {
    val navController = rememberNavController()
    val backstack by navController.currentBackStackEntryAsState()
    val currentDest: NavDestination? = backstack?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentDest?.route?.startsWith(Route.Notes.base) == true,
                    onClick = {
                        navController.navigate("${Route.Notes.base}/fade") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Notes") },
                    label = { Text("Notes") }
                )
                NavigationBarItem(
                    selected = currentDest?.route?.startsWith(Route.Tasks.base) == true,
                    onClick = {
                        navController.navigate("${Route.Tasks.base}/fade") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = currentDest?.route?.startsWith(Route.Calendar.base) == true,
                    onClick = {
                        navController.navigate("${Route.Calendar.base}/fade") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Event, contentDescription = "Calendar") },
                    label = { Text("Calendar") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "${Route.Notes.base}/fade",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Route.Notes.withArg,
                arguments = listOf(navArgument(Route.ARG_ANIM) { type = NavType.StringType })
            ) { entry ->
                val anim = entry.arguments?.getString(Route.ARG_ANIM).orEmpty()
                ScreenScaffold(title = "Notes") {
                    AnimatedVisibility(
                        visible = true,
                        enter = if (anim == "fade") fadeIn(tween(400)) else fadeIn(),
                        exit = fadeOut(tween(400))
                    ) {
                        NotesScreen(notesVM)
                    }
                }
            }

            composable(
                route = Route.Tasks.withArg,
                arguments = listOf(navArgument(Route.ARG_ANIM) { type = NavType.StringType })
            ) { entry ->
                val anim = entry.arguments?.getString(Route.ARG_ANIM).orEmpty()
                ScreenScaffold(title = "Tasks") {
                    AnimatedVisibility(
                        visible = true,
                        enter = if (anim == "fade") fadeIn(tween(400)) else fadeIn(),
                        exit = fadeOut(tween(400))
                    ) {
                        TasksScreen(tasksVM)
                    }
                }
            }

            composable(
                route = Route.Calendar.withArg,
                arguments = listOf(navArgument(Route.ARG_ANIM) { type = NavType.StringType })
            ) { entry ->
                val anim = entry.arguments?.getString(Route.ARG_ANIM).orEmpty()
                ScreenScaffold(title = "Calendar") {
                    AnimatedVisibility(
                        visible = true,
                        enter = if (anim == "fade") fadeIn(tween(400)) else fadeIn(),
                        exit = fadeOut(tween(400))
                    ) {
                        CalendarScreen()
                    }
                }
            }
        }
    }
}

// ------------------ COMMON SCREEN CHROME ------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(title: String, content: @Composable () -> Unit) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(title) }) }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

// ------------------ NOTES ------------------
@Composable
fun NotesScreen(vm: NotesViewModel) {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("New note") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { vm.addNote(text).also { text = "" } },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Add") }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.notes) { (id, note) ->
                Card(colors = CardDefaults.cardColors()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Note #$id", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(note)
                    }
                }
            }
        }
    }
}

// ------------------ TASKS ------------------
@Composable
fun TasksScreen(vm: TasksViewModel) {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("New task") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { vm.addTask(text).also { text = "" } },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Add") }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.tasks) { task ->
                TaskRow(task = task, onToggle = { vm.toggle(task.id) })
            }
        }
    }
}

@Composable
fun TaskRow(task: TaskItem, onToggle: () -> Unit) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = if (task.done) FontWeight.Normal else FontWeight.Medium
                )
                Text(if (task.done) "Done" else "Pending", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onToggle) {
                Text(if (task.done) "Uncheck" else "Check")
            }
        }
    }
}

// ------------------ CALENDAR (static placeholder) ------------------
@Composable
fun CalendarScreen() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calendar", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("Static placeholder (demo)", style = MaterialTheme.typography.bodyMedium)
    }
}
