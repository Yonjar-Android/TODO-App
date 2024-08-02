package com.example.todoapp.tasks.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.tasks.ui.BackgroundScreen
import com.example.todoapp.tasks.ui.Loading
import com.example.todoapp.tasks.ui.errorFun

@Composable
fun UserScreen(
    userScreenViewModel: UserScreenViewModel,
    navHostController: NavHostController,
    email: String
) {

    val state = userScreenViewModel.state.collectAsState()
    val context = LocalContext.current

    var openDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userScreenViewModel.getUserByEmail(email)
    }

    BackgroundScreen(image = R.drawable.main_bg) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (profile, logout) = createRefs()

            val guideLineTop = createGuidelineFromTop(0.15f)
            val guideLineEnd = createGuidelineFromEnd(0.03f)

            Image(
                painter = painterResource(id = R.drawable.logout_icon),
                contentDescription = "Log out",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .constrainAs(logout) {
                        top.linkTo(parent.top)
                        end.linkTo(guideLineEnd)
                    }
                    .size(40.dp)
                    .clickable {
                        openDialog = true
                    }
            )

            when (val currrentState = state.value) {
                is UserScreenState.Error -> {
                    errorFun(currrentState.error, context)
                }

                UserScreenState.Initial -> {}
                UserScreenState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Loading()
                    }
                }

                is UserScreenState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(profile) {
                                top.linkTo(guideLineTop)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.user_solid),
                            contentDescription = "User profile image",
                            modifier = Modifier
                                .size(130.dp)
                                .clip(shape = CircleShape),
                        )

                        Spacer(modifier = Modifier.padding(bottom = 20.dp))

                        TextInfoTitle(stringResource(id = R.string.fullName), fontWeight = FontWeight.SemiBold)
                        TextInfoTitle(currrentState.user.name, FontWeight.Normal)
                        TextInfoTitle(stringResource(id = R.string.email), FontWeight.SemiBold)
                        TextInfoTitle(currrentState.user.email, FontWeight.Normal)
                    }
                }
            }
        }
    }

    if (openDialog) {
        DialogOut(navHostController = navHostController,
            close = {
                openDialog = false
            },
            logOut = {
                userScreenViewModel.logOutUser()
            })
    }
}

@Composable
fun TextInfoTitle(info: String, fontWeight: FontWeight) {
    Text(
        text = info,
        fontSize = 20.sp,
        fontWeight = fontWeight,
        modifier = Modifier.padding(vertical = 6.dp),
        color = Color.White
    )
}

@Composable
fun DialogOut(navHostController: NavHostController,
              close: () -> Unit,
              logOut: () -> Unit) {
    AlertDialog(onDismissRequest = {

    }, confirmButton = {
        TextButton(onClick = {
            navHostController.popBackStack(navHostController.graph.startDestinationId, false)
            navHostController.navigate("logoAndButtons") {
                popUpTo(navHostController.graph.startDestinationId) {
                    inclusive = true
                }
                close()
                logOut()
            }
        }) {
            Text(text = stringResource(id = R.string.logOut))
        }
    }, dismissButton = {
        TextButton(onClick = {
            close()
        }) {
            Text(text = stringResource(id = R.string.cancelButton))
        }
    },
        title = {
            Text(text = stringResource(id = R.string.logOut))
        },
        text = {
            Text(text = stringResource(id = R.string.logOutAccount))
        })
}