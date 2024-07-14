package com.example.todoapp.tasks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R

@Composable
fun BackgroundScreen(screen: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.app_bg), contentDescription = "background",
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
        screen()
    }
}

@Composable
fun LogoAndButtons() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App logo",
            modifier = Modifier
                .clip(CircleShape)
                .size(230.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = 45.dp)) {
            TonalButton(title = "Iniciar Sesión")
            TonalButton(title = "Regístrate")
        }
    }
}

@Composable
fun TonalButton(title:String){
    FilledTonalButton(modifier = Modifier
        .padding(vertical = 10.dp, horizontal = 20.dp)
        .fillMaxWidth(),
        onClick = {

        }) {
        Text(text = title, fontSize = 28.sp)
    }
}