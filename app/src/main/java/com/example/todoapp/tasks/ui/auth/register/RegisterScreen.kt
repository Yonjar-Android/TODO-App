package com.example.todoapp.tasks.ui.auth.register

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun RegisterScreen(
    navHostController: NavHostController,
    viewModel: RegisterViewModel
) {


    val state = viewModel.state.collectAsState()

    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val (logo, inputs, button,loading) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.1f)
        val guidelineBottom = createGuidelineFromBottom(0.07f)
        val guidelineStart = createGuidelineFromStart(0.3f)
        val guidelineEnd = createGuidelineFromEnd(0.3f)

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
                .constrainAs(inputs) {
                    top.linkTo(logo.bottom)
                    bottom.linkTo(button.top)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldComp("Nombre Completo",name) { name = it }
            TextFieldComp("Correo Electrónico",email) { email = it }
            TextFieldComp("Contraseña",password) { password = it }
            TextFieldComp("Repetir contraseña", repeatPassword) { repeatPassword = it }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(button) {
                    bottom.linkTo(guidelineBottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TonalButton(title = "Registrar") {
                viewModel.createUser(
                    name = name,
                    password = password,
                    password2 = repeatPassword,
                    email = email.lowercase()
                )
            }
            TextButton(onClick = {
                navHostController.navigate("loginScreen")
            }) {
                Text(
                    text = "¿Ya tienes una cuenta?",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        ////////////////////

        when (val currentState = state.value) {

            is RegisterState.Error -> {
                errorFun(currentState.error ?: "", context)
                viewModel.resetState()
            }

            RegisterState.Loading -> Box(modifier = Modifier.clip(CircleShape)
                .constrainAs(loading) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            ) {
                Loading()
            }

            is RegisterState.Success -> {
                SuccessFun(context, currentState.user, navHostController, viewModel)
            }

            RegisterState.Initial -> {}
        }

        /////////////////
    }
}

@Composable
fun SuccessFun(
    context: Context,
    user: UserM,
    navHostController: NavHostController,
    viewModel: RegisterViewModel
) {
    Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()

    navHostController.navigate("loginScreen")

    viewModel.resetState()
}



