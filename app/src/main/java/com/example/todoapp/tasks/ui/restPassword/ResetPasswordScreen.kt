package com.example.todoapp.tasks.ui.restPassword

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.tasks.ui.Loading
import com.example.todoapp.tasks.ui.TextFieldComp
import com.example.todoapp.tasks.ui.TonalButton
import com.example.todoapp.tasks.ui.errorFun


@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel,
    navHostController: NavHostController
    ) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val state = viewModel.state.collectAsState()
        val context = LocalContext.current

        val (button, logo, input, loading) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.1f)
        val guidelineBottom = createGuidelineFromBottom(0.08f)
        val guidelineStart = createGuidelineFromStart(0.3f)
        val guidelineEnd = createGuidelineFromEnd(0.3f)

        var email by rememberSaveable {
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
                .constrainAs(input) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldComp(labelField = "Correo electrónico") { email = it }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(button) {
                    bottom.linkTo(guidelineBottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TonalButton(title = "Enviar correo") {
                viewModel.resetPassword(email.lowercase())
            }
        }

        when (val currentSate = state.value) {
            is ResetPasswordState.Error -> {
                errorFun(context = context, error = currentSate.error ?: "")
                viewModel.resetState()
            }

            ResetPasswordState.Initial -> {}

            ResetPasswordState.Loading -> {
                Box(modifier = Modifier
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

            is ResetPasswordState.Success -> {
                Toast.makeText(context,currentSate.success,Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                navHostController.navigate("loginScreen")
            }
        }
    }
}