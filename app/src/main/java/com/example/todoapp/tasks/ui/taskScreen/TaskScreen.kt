package com.example.todoapp.tasks.ui.taskScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.todoapp.R
import com.example.todoapp.tasks.ui.BackgroundScreen

@Composable
fun TaskScreen(email: String) {
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            content = {
                BackgroundScreen(image = R.drawable.main_bg) {
                    Box(modifier = Modifier.padding(it.calculateTopPadding()))
                }
            },
            floatingActionButton = { MyFAB() },
            floatingActionButtonPosition = FabPosition.End
        )
    }
}

@Composable
fun MyFAB() {
    FloatingActionButton(
        onClick = {

        },
    ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Icon")
    }
}
