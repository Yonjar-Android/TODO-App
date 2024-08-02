@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.tasks.ui.taskScreen.taskDetail

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
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
    var showDate by rememberSaveable { mutableStateOf(false) }

    val taskState = taskDetailViewModel.task.collectAsState()

    val fetchedCategories = taskDetailViewModel.categories.collectAsState()


    LaunchedEffect(Unit) {
        taskDetailViewModel.getTaskById(taskId)
    }

    Scaffold(
        content = {
            BackgroundScreen(image = R.drawable.main_bg) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    when (val currentState = state.value) {
                        is TaskDetailState.Error -> {
                            errorFun(currentState.error, context)
                            taskDetailViewModel.resetState()
                        }

                        TaskDetailState.Initial -> {}
                        TaskDetailState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ){
                                Loading()
                            }
                        }

                        is TaskDetailState.Success -> {
                            if (currentState.message.isNotBlank()) {
                                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                            }
                            showDialog = true
                            taskDetailViewModel.resetState()
                        }
                    }

                    if (showDialog && taskState.value != null
                        && taskDetailViewModel.category.isNotBlank()
                    ){
                        DialogTaskAdd(
                            viewModel = taskDetailViewModel,
                            closeDate = { showDate = !showDate },
                            showDate = showDate, // O el estado que necesites para mostrar el date picker
                            categories = fetchedCategories.value, // Pasa las categorías necesarias
                            task = taskState.value!!,
                            category = taskDetailViewModel.category
                        )
                    }

                }
            }
        },
        topBar = { MyTopAppBar(navHostController) }
    )


}

@Composable
fun MyTopAppBar(navHostController: NavHostController) {
    TopAppBar(title = { Text(text = stringResource(id = R.string.taskDetail)) },
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
    task: TaskDom,
    category: String
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF494848))
            .padding(bottom = 25.dp)
    ) {
        val (form) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.05f)

        var name by rememberSaveable {
            mutableStateOf(task.name)
        }

        val dateState = rememberDatePickerState()

        var categoryV by rememberSaveable {
            mutableStateOf(category)
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

            TextFieldComp(labelField = stringResource(id = R.string.taskName), name) { name = it }

            TextFieldComp(
                stringResource(id = R.string.description),
                height = 120.dp,
                maxLin = 5,
                singleL = false,
                text = description ?: ""
            ) { description = it }

            DropDowsMenuCategories(categories, categorySelected = categoryV) { categoryV = it }


            // Components to manage the date selected
            /////////

            val millisToLocalDate = dateState.selectedDateMillis?.let {
                DateUtilsClass().convertMillisToLocalDate(it)
            }
            val dateToString = millisToLocalDate?.let {
                DateUtilsClass().dateToString(millisToLocalDate)
            } ?: task.date

            DateFieldComp(dateToString)

            TonalButton(title = " ${stringResource(id = R.string.chooseDate)} ") {
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
                                Text(text = stringResource(id = R.string.acceptButton))
                            }
                        }) {
                        DatePicker(state = dateState)
                    }
                }
            }

            /////////
            /////////

            TextFieldComp(
                labelField = stringResource(id = R.string.deliverables),
                maxLin = 4,
                height = 100.dp,
                singleL = false,
                text = entregables
            ) {
                entregables = it
            }


            TonalButton(
                title = stringResource(id = R.string.updateButton),
                modifier = Modifier,
                color = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E56FD))
            )
            {
                viewModel.updateTask(
                    taskId = task.taskId,
                    name = name,
                    date = dateToString,
                    description = description,
                    deliverables = listOf(),
                    deliverablesDescription = entregables,
                    check = task.check,
                    category = categoryV
                )
            }
        }
    }
}
