package com.example.todoapp.tasks.ui.taskScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoapp.R

@Composable
fun MainTaskScreen(
    navHostController: NavHostController,
    email: String
) {

    Scaffold(
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.main_bg),
                    contentDescription = "background",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(2.dp),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }
        },
        bottomBar = { BottomNavTasks(navHostController = navHostController) },
        floatingActionButton = {
            MyFAB()
        },
        floatingActionButtonPosition = FabPosition.End,

        )
}


@Composable
fun BottomNavTasks(navHostController: NavHostController) {

    var navIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(selected = navIndex == 0,
            onClick = {
                navIndex = 0
            }, icon = {
                Icon(
                    painter = painterResource(id = R.drawable.task_icon),
                    contentDescription = "Task Icon",
                    modifier = Modifier.size(35.dp)
                )
            })

        NavigationBarItem(selected = navIndex == 1,
            onClick = {
                navIndex = 1
            }, icon = {
                Icon(
                    painter = painterResource(id = R.drawable.user_solid),
                    contentDescription = "User Icon",
                    modifier = Modifier.size(35.dp)
                )
            })
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