package com.example.todoapp.tasks.ui.auth.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.todoapp.tasks.domain.models.UserM
import com.example.todoapp.tasks.ui.Loading
import com.example.todoapp.tasks.ui.TextFieldComp
import com.example.todoapp.tasks.ui.TonalButton
import com.example.todoapp.tasks.ui.errorFun

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navHostController: NavHostController
) {

    val state = viewModel.state.collectAsState()
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val (logo, inputs, button, loading) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.1f)
        val guidelineBottom = createGuidelineFromBottom(0.07f)
        val guidelineStart = createGuidelineFromStart(0.3f)
        val guidelineEnd = createGuidelineFromEnd(0.3f)

        var email by rememberSaveable {
            mutableStateOf("")
        }
        var password by rememberSaveable {
            mutableStateOf("")
        }

        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .clip(CircleShape)
                .size(180.dp)
                .constrainAs(logo) {
                    top.linkTo(guidelineTop)
                    start.linkTo(guidelineStart)
                    end.linkTo(guidelineEnd)
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .constrainAs(inputs) {
                    top.linkTo(logo.bottom)
                    bottom.linkTo(button.top)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldComp(labelField = stringResource(id = R.string.email), email) { email = it }
            TextFieldComp(labelField = stringResource(id = R.string.password), password) { password = it }
            TextButton(onClick = {
                navHostController.navigate("resetPasswordScreen")
            }) {
                Text(
                    text = stringResource(id = R.string.forgotYourPassword),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(button) {
                    bottom.linkTo(guidelineBottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TonalButton(title = stringResource(id = R.string.enterButton)) { viewModel.loginUser(email.lowercase(), password) }

            TextButton(onClick = {
                navHostController.navigate("registerScreen")
            }) {
                Text(
                    text = stringResource(id = R.string.dontHaveAnAccount),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        when (val currentSate = state.value) {
            is LoginState.Error -> {
                errorFun(context = context, error = currentSate.error ?: "")
                viewModel.resetState()
            }

            LoginState.Initial -> {}
            LoginState.Loading -> {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .constrainAs(loading) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                ) {
                    Loading()
                }
            }

            is LoginState.Success -> {
                SuccessFun(context, viewModel, navHostController, currentSate.user)
            }
        }
    }
}

@Composable
fun SuccessFun(
    context: Context,
    viewModel: LoginViewModel,
    navHostController: NavHostController,
    user: UserM
) {
    Toast.makeText(context, stringResource(id = R.string.welcome), Toast.LENGTH_SHORT).show()

    navHostController.navigate("mainTaskScreen/${user.email}")

    viewModel.resetState()
}
