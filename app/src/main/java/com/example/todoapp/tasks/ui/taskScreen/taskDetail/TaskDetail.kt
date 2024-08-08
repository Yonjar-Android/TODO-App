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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
    val fetchedUsers = taskDetailViewModel.userEmails.collectAsState()

    LaunchedEffect(Unit) {
        taskDetailViewModel.getCategories()
        taskDetailViewModel.getEmails()
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
                            ) {
                                Loading()
                            }
                        }

                        is TaskDetailState.Success -> {
                            if (currentState.message.isNotBlank()) {
                                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            showDialog = true
                            taskDetailViewModel.resetState()
                        }
                    }

                    if (showDialog && taskState.value != null
                        && taskDetailViewModel.category.isNotBlank()
                    ) {
                        DialogTaskAdd(
                            viewModel = taskDetailViewModel,
                            closeDate = { showDate = !showDate },
                            showDate = showDate, // O el estado que necesites para mostrar el date picker
                            categories = fetchedCategories.value, // Pasa las categorías necesarias
                            task = taskState.value!!,
                            category = taskDetailViewModel.category,
                            userEmails = fetchedUsers.value
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
    category: String,
    userEmails: List<String>
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF494848))
            .padding(bottom = 25.dp)
    ) {
        var showAddUsers by remember {
            mutableStateOf(false)
        }

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

        var users by rememberSaveable {
            mutableStateOf(task.users)
        }

        var entregables by rememberSaveable {
            mutableStateOf(task.deliverablesDesc)
        }

        var filteredEmails = remember(userEmails, users) {
            filterEmails(userEmails, users)
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
                labelField = stringResource(id = R.string.users),
                maxLin = 4,
                height = 80.dp,
                singleL = false,
                text = users.toString(),
                readOnlyValue = true
            ) {}

            TonalButton(title = stringResource(id = R.string.addUsers)) {
                showAddUsers = true
            }

            if (showAddUsers) {
                AddUsersDialog(
                    onDismiss = { showAddUsers = false },
                    onConfirm = { showAddUsers = false },
                    usersToAdd = filteredEmails,
                    usersToDelete = users,
                    addToList = {
                        filteredEmails = filteredEmails.toMutableList().apply { remove(it) }
                        users = users.toMutableList().apply { add(it) }
                    },
                    removeToList = {
                        filteredEmails = filteredEmails.toMutableList().apply { add(it) }
                        users = users.toMutableList().apply { remove(it) }
                    }
                )
            }

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
                    category = categoryV,
                    users = users
                )
            }
        }
    }
}

fun filterEmails(userEmails: List<String>, users: List<String>): List<String> {
    return userEmails.filterNot { it in users }
}

@Composable
fun AddUsersDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    usersToAdd: List<String>,
    usersToDelete: List<String>,
    addToList: (String) -> Unit,
    removeToList: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.selectUsers), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))


                LazyColumn(
                    Modifier
                        .weight(1f)
                        .background(Color(0XFFcccccc))
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.usersToAssing),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(usersToAdd) { email ->
                        CardAddUserItem(email = email, iconImage = Icons.Filled.Add, behaviorFun = {
                            addToList(email)
                        })
                    }
                }

                Spacer(modifier = Modifier.padding(16.dp))

                LazyColumn(
                    Modifier
                        .weight(1f)
                        .background(Color(0XFFcccccc))
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.usersAssigned),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(usersToDelete) { email ->
                        CardAddUserItem(
                            email = email,
                            iconImage = Icons.Filled.Clear,
                            emailCreator = usersToDelete[0],
                            behaviorFun = {
                                removeToList(email)
                            })
                    }
                }



                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onConfirm) {
                        Text(stringResource(id = R.string.confirm_button))
                    }
                }
            }
        }
    }
}

@Composable
fun CardAddUserItem(
    email: String,
    iconImage: ImageVector,
    emailCreator: String = "",
    behaviorFun: () -> Unit
) {
    Card(Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = email,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )
            if (email != emailCreator)
                IconButton(onClick = {
                    behaviorFun()
                }) {
                    Icon(imageVector = iconImage, contentDescription = "Add Icon")
                }
        }
    }
}
