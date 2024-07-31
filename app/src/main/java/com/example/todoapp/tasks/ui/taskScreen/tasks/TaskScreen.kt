@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.tasks.ui.taskScreen.tasks

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.tasks.domain.models.Category
import com.example.todoapp.tasks.domain.models.TaskDom
import com.example.todoapp.tasks.ui.BackgroundScreen
import com.example.todoapp.tasks.ui.DateUtilsClass
import com.example.todoapp.tasks.ui.Loading
import com.example.todoapp.tasks.ui.TextFieldComp
import com.example.todoapp.tasks.ui.TonalButton
import com.example.todoapp.tasks.ui.errorFun

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    email: String,
    viewModel: TaskScreenViewModel,
    navHostController: NavHostController
) {

    val state = viewModel.state.collectAsState()
    val context = LocalContext.current

    // State to hold fetched categories
    val fetchedCategories = rememberSaveable { mutableStateOf<List<Category>?>(null) }
    val fetchedTasks = rememberSaveable { mutableStateOf<List<TaskDom>?>(null) }

    // Update fetchedCategories when viewModel state changes
    LaunchedEffect(state.value) {
        fetchedCategories.value = viewModel.categories
        fetchedTasks.value = viewModel.tasks
    }

    // Observe navigation changes
    DisposableEffect(navHostController) {
        val backStackEntry = navHostController.currentBackStackEntry
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Fetch tasks when returning to this screen
                viewModel.getAllTasks()
            }
        }
        backStackEntry?.lifecycle?.addObserver(observer)
        onDispose {
            backStackEntry?.lifecycle?.removeObserver(observer)
        }
    }

    val showToast = viewModel.showToast.collectAsState()

    var show by rememberSaveable {
        mutableStateOf(false)
    }

    var showDate by rememberSaveable {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            content = {
                BackgroundScreen(image = R.drawable.main_bg) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentPadding = it
                    ) {
                        if (fetchedTasks.value != null) {
                            items(fetchedTasks.value!!) { task ->
                                TaskCheckBox(
                                    task = task, onCheckedChange = { taskId, check ->
                                        viewModel.onCheckedChange(taskId = taskId, check)
                                    },
                                    goToDetail = { taskId ->
                                        navHostController.navigate("taskDetailScreen/$taskId")
                                    },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        show = true
                    },
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Icon")
                }
            },

            floatingActionButtonPosition = FabPosition.End
        )

        if (show) {
            DialogTaskAdd(
                viewModel = viewModel,
                closeDate = {
                    showDate = !showDate
                },
                close = {
                    show = false
                }, showDate = showDate,
                categories = fetchedCategories.value
            )
        }

        when (val currentState = state.value) {
            is TaskScreenState.Error -> {
                errorFun(currentState.error, context)
                viewModel.resetState()
            }

            TaskScreenState.Initial -> {}

            TaskScreenState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Loading()
                }

            }

            is TaskScreenState.Success -> {
                if (showToast.value && currentState.message.isNotBlank()) {
                    Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                    viewModel.getAllTasks()
                }
                viewModel.resetState()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DialogTaskAdd(
    viewModel: TaskScreenViewModel,
    close: () -> Unit,
    closeDate: () -> Unit,
    showDate: Boolean,
    categories: List<Category>?
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF494848))
            .padding(bottom = 25.dp)
    ) {
        val (closed, form) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.05f)

        val resetFields by viewModel.resetFields.collectAsState()

        var name by rememberSaveable {
            mutableStateOf("")
        }

        val dateState = rememberDatePickerState()

        var category by rememberSaveable {
            mutableStateOf("")
        }

        var description by rememberSaveable {
            mutableStateOf("")
        }

        var entregables by rememberSaveable {
            mutableStateOf("")
        }

        if (resetFields) {
            name = ""
            category = ""
            description = ""
            entregables = ""
            description = ""
            viewModel.cleanFieldHandled()
        }

        Icon(imageVector = Icons.Filled.Close,
            contentDescription = "Close",
            tint = Color.White,
            modifier = Modifier
                .size(35.dp)
                .constrainAs(closed) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .clickable {
                    close()
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .constrainAs(form) {
                    top.linkTo(guidelineTop)
                },
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            TextFieldComp(labelField = "Nombre de la tarea", name) { name = it }

            TextFieldComp(
                "Descripción",
                height = 120.dp,
                maxLin = 5,
                singleL = false,
                text = description
            ) { description = it }

            DropDowsMenuCategories(categories, categorySelected = category) { category = it }


            // Components to manage the date selected
            /////////

            val millisToLocalDate = dateState.selectedDateMillis?.let {
                DateUtilsClass().convertMillisToLocalDate(it)
            }
            val dateToString = millisToLocalDate?.let {
                DateUtilsClass().dateToString(millisToLocalDate)
            } ?: ""

            DateFieldComp(dateToString)

            TonalButton(title = " Elegir Fecha ") {
                closeDate()
            }

            // Show DatePicker component to select a date
            if (showDate) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DatePickerDialog(
                        onDismissRequest = {},
                        confirmButton = {
                            TextButton(onClick = { closeDate() }) {
                                Text(text = "Aceptar")
                            }
                        }) {
                        DatePicker(state = dateState)
                    }
                }
            }

            /////////
            /////////

            TextFieldComp(
                labelField = "Entregables",
                maxLin = 4,
                height = 100.dp,
                singleL = false,
                text = entregables
            ) {
                entregables = it
            }


            TonalButton(
                title = "Crear",
                modifier = Modifier,
                color = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E56FD))
            )
            {
                viewModel.createTask(
                    name = name,
                    description = description,
                    date = dateToString,
                    check = false,
                    deliverables = listOf(),
                    deliverablesDescription = entregables,
                    users = listOf(),
                    category = category
                )
            }
        }
    }
}

@Composable
fun DateFieldComp(date: String) {
    var text by rememberSaveable {
        mutableStateOf(date)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
    ) {

        Text(
            text = "Fecha Límite",
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = date,
            onValueChange = {
                text = it
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 50.dp, width = 0.dp),
            textStyle = TextStyle(fontSize = 14.sp),
            singleLine = true,
            readOnly = true
        )
    }
}

@Composable
fun DropDowsMenuCategories(
    categories: List<Category>?,
    categorySelected: String,
    categoryValue: (String) -> Unit
) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = categorySelected,
            onValueChange = {}, // Read-only, updates on selection from dropdown
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 60.dp, width = 0.dp)
                .menuAnchor(),
            shape = RoundedCornerShape(20.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories?.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        expanded = false
                        categoryValue(category.name)
                    }
                )
            }
        }
    }
}

@Composable
fun TaskCheckBox(
    task: TaskDom,
    onCheckedChange: (String, Boolean) -> Unit,
    goToDetail: (String) -> Unit,
    viewModel: TaskScreenViewModel
) {
    Spacer(modifier = Modifier.size(8.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(4.dp)
            .clickable {
                goToDetail(task.taskId)
            }
    ) {
        val checkedState = rememberSaveable { mutableStateOf(task.check) }

        Text(
            text = task.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(9f),
            fontSize = 16.sp
        )
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                onCheckedChange(task.taskId, checkedState.value)
            },
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
        )
        DropDownDelete(task, viewModel)

    }
    Spacer(modifier = Modifier.size(8.dp))
}

@Composable
fun DropDownDelete(task: TaskDom, viewModel:TaskScreenViewModel) {
    var openDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = {
                expanded = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Open Menu"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = "Eliminar")
                },
                onClick = {
                    expanded = false
                    openDialog = true
                }
            )
        }
    }
    if (openDialog) {
        DialogDelete(task = task, viewModel) { openDialog = false }
    }
}

@Composable
fun DialogDelete(task: TaskDom, viewModel: TaskScreenViewModel, close: () -> Unit) {
    AlertDialog(onDismissRequest = {}, confirmButton = {
        TextButton(onClick = {
            viewModel.deleteTask(task.taskId)
            close()
        }) {
            Text(text = "Eliminar")
        }
    },
        dismissButton = {
            TextButton(onClick = {
                close()
            }) {
                Text(text = "Cancelar")

            }
        },
        title = {
            Text(text = "Eliminar tarea")
        },
        text = {
            Text(text = "¿Quieres eliminar la tarea: ${task.name}?")
        })
}



