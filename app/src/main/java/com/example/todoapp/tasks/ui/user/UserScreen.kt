package com.example.todoapp.tasks.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
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
import com.example.todoapp.tasks.ui.BackgroundScreen

@Composable
fun UserScreen(navHostController: NavHostController, email: String) {
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
            )

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

                TextInfoTitle("Nombre Completo", fontWeight = FontWeight.SemiBold)
                TextInfoTitle("Juan Centeno", FontWeight.Normal)
                TextInfoTitle("Correo electrónico", FontWeight.SemiBold)
                TextInfoTitle("juan132y@gmail.com", FontWeight.Normal)

            }
        }
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