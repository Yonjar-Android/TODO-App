package com.example.todoapp.tasks.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.tasks.ui.TonalButton

@Composable
fun LogoAndButtons(navHostController: NavHostController) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val (logo, buttons) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.15f)
        val guidelineBottom = createGuidelineFromBottom(0.07f)
        val guidelineStart = createGuidelineFromStart(0.3f)
        val guidelineEnd = createGuidelineFromEnd(0.3f)

        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App logo",
            modifier = Modifier
                .clip(CircleShape)
                .size(230.dp)
                .constrainAs(logo) {
                    top.linkTo(guidelineTop)
                    start.linkTo(guidelineStart)
                    end.linkTo(guidelineEnd)
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(buttons) {
                    bottom.linkTo(guidelineBottom)
                }
        ) {
            TonalButton(
                title = stringResource(id = R.string.loginButton),
                onClick = { navHostController.navigate("loginScreen") })
            TonalButton(
                title = stringResource(id = R.string.RegistButton),
                onClick = { navHostController.navigate("registerScreen") })
        }
    }
}


