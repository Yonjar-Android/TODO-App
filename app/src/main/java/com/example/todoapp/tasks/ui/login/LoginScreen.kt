package com.example.todoapp.tasks.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.tasks.ui.TextFieldComp
import com.example.todoapp.tasks.ui.TonalButton

@Composable
fun LoginScreen(navHostController: NavHostController) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val (logo, inputs, button) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.1f)
        val guidelineBottom = createGuidelineFromBottom(0.02f)
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
                .padding(vertical = 20.dp)
                .constrainAs(inputs) {
                    top.linkTo(logo.bottom)
                    bottom.linkTo(button.top)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldComp(labelField = "Correo electrónico") {}
            TextFieldComp(labelField = "Contraseña") {}
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(button) {
                    bottom.linkTo(guidelineBottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TonalButton(title = "Ingresar") { }

            TextButton(onClick = {
                navHostController.navigate("registerScreen")
            }) {
                Text(
                    text = "¿No tienes una cuenta?",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }


    }
}
