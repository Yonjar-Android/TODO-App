@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.tasks.ui.taskScreen.taskDetail

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
import com.example.todoapp.tasks.ui.taskScreen.tasks.DateFieldComp
import com.example.todoapp.tasks.ui.taskScreen.tasks.DropDowsMenuCategories

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskDetail(
    taskId: String,
    navHostController: NavHostController,
    taskDetailViewModel: TaskDetailViewModel
) {

    val context = LocalContext.current
    val state = taskDetailViewModel.state.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var task by rememberSaveable { mutableStateOf<TaskDom?>(null) }

    LaunchedEffect(state.value) {
        if (state.value is TaskDetailState.Success) {
            task = (state.value as TaskDetailState.Success).task
        }
    }


    Scaffold(
        content = {
            BackgroundScreen(image = R.drawable.main_bg) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (val currentState = state.value) {
                        is TaskDetailState.Error -> {
                            errorFun(currentState.error, context)
                        }

                        TaskDetailState.Initial -> {}
                        TaskDetailState.Loading -> {
                            Loading()
                        }

                        is TaskDetailState.Success -> {

                            if (currentState.message.isNotBlank()) {
                                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            showDialog = true
                        }
                    }
                }
            }
        },
        topBar = { MyTopAppBar(navHostController) }
    )

    if (showDialog && task != null) {
        DialogTaskAdd(
            viewModel = taskDetailViewModel,
            closeDate = { showDialog = false },
            showDate = false, // O el estado que necesites para mostrar el date picker
            categories = listOf(), // Pasa las categorías necesarias
            task = task!!
        )
    }
}

@Composable
fun MyTopAppBar(navHostController: NavHostController) {
    TopAppBar(title = { Text(text = "Task Detail") },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go Back",
                modifier = Modifier.clickable {
                    navHostController.navigateUp()
                })
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DialogTaskAdd(
    viewModel: TaskDetailViewModel,
    closeDate: () -> Unit,
    showDate: Boolean,
    categories: List<Category>?,
    task: TaskDom
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF494848))
            .padding(bottom = 25.dp)
    ) {
        val (close, form) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.05f)

        var name by rememberSaveable {
            mutableStateOf(task.name)
        }

        val dateState = rememberDatePickerState()

        var category by rememberSaveable {
            mutableStateOf("")
        }

        var description by rememberSaveable {
            mutableStateOf(task.description)
        }

        var entregables by rememberSaveable {
            mutableStateOf(task.deliverablesDesc)
        }

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
                text = description ?: ""
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
                title = "Actualizar",
                modifier = Modifier,
                color = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E56FD))
            )
            {

            }
        }
    }
}