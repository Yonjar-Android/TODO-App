@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.tasks.ui.taskScreen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.todoapp.R
import com.example.todoapp.tasks.domain.models.Category
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
    viewModel: TaskScreenViewModel
) {

    val state = viewModel.state.collectAsState()
    val context = LocalContext.current

    // State to hold fetched categories
    val fetchedCategories = rememberSaveable { mutableStateOf<List<Category>?>(null) }

    // Fetch categories once when the screen is composed
    LaunchedEffect(Unit) {
        viewModel.getCategories()
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
                    Box(modifier = Modifier.padding(it.calculateTopPadding()))
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
        // Update fetchedCategories when viewModel state changes
        LaunchedEffect(state.value) {
                fetchedCategories.value = viewModel.categories
        }

        when (val currentState = state.value) {
            is TaskScreenState.Error -> {
                errorFun(currentState.error,context)
                viewModel.resetState()
            }

            TaskScreenState.Initial -> {}
            TaskScreenState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center){
                    Loading()
                }

            }

            is TaskScreenState.Success -> {
                if (showToast.value) {
                    if (currentState.message.isNotBlank()) {
                        Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                    }
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
        val (close, form) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.05f)

        var name by rememberSaveable {
            mutableStateOf("")
        }

        var dateState = rememberDatePickerState()

        var resetDatePicker by remember { mutableStateOf(false) }

        var category by rememberSaveable {
            mutableStateOf("")
        }

        var description by rememberSaveable {
            mutableStateOf("")
        }

        var entregables by rememberSaveable {
            mutableStateOf("")
        }

        LaunchedEffect(viewModel.state.value) {
            if (viewModel.state.value is TaskScreenState.Success){
                name = ""
                category = ""
                description = ""
                entregables = ""
                description = ""
                resetDatePicker = true
            }
        }

        if (resetDatePicker) dateState = rememberDatePickerState()

        Icon(imageVector = Icons.Filled.Close,
            contentDescription = "Close",
            tint = Color.White,
            modifier = Modifier
                .size(35.dp)
                .constrainAs(close) {
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

            TextFieldComp(labelField = "Nombre de la tarea") { name = it }

            TextFieldComp(
                "Descripción",
                height = 120.dp,
                maxLin = 5,
                singleL = false
            ) { description = it }

            DropDowsMenuCategories(categories) { category = it }


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
                singleL = false
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
fun DropDowsMenuCategories(categories: List<Category>?, categoryValue: (String) -> Unit) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedCategory by rememberSaveable { mutableStateOf("") }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = selectedCategory,
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
                        selectedCategory = category.name
                        expanded = false
                        categoryValue(selectedCategory)
                    }
                )
            }
        }
    }
}



