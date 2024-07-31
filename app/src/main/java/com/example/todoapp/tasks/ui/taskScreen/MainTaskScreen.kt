package com.example.todoapp.tasks.ui.taskScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.R
import com.example.todoapp.tasks.ui.taskScreen.tasks.TaskScreen
import com.example.todoapp.tasks.ui.taskScreen.tasks.TaskScreenViewModel
import com.example.todoapp.tasks.ui.user.UserScreen
import com.example.todoapp.tasks.ui.user.UserScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainTaskScreen(
    navHostController: NavHostController,
    email: String,
    taskScreenViewModel: TaskScreenViewModel,
    userScreenViewModel: UserScreenViewModel
) {

    val navigationController = rememberNavController()

    Scaffold(
        content = { padding ->

            NavHost(
                modifier = Modifier.padding(padding),
                navController = navigationController,
                startDestination = "taskScreen"
            ) {
                composable(route = "taskScreen") {
                    TaskScreen(email, taskScreenViewModel, navHostController)
                }

                composable(route = "userScreen") {
                    UserScreen(userScreenViewModel, navHostController, email)
                }
            }

        },
        bottomBar = { BottomNavTasks(navHostController = navigationController) })
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
                navHostController.navigate("taskScreen")
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
                navHostController.navigate("userScreen")
            }, icon = {
                Icon(
                    painter = painterResource(id = R.drawable.user_solid),
                    contentDescription = "User Icon",
                    modifier = Modifier.size(35.dp)
                )
            })
    }
}

