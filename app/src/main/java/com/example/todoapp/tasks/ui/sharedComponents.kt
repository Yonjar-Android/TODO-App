package com.example.todoapp.tasks.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextFieldComp(labelField: String) {
    var text by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
    ) {
        Text(
            text = labelField,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = text,
            onValueChange = { text = it },
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 45.dp, width = 0.dp),
            textStyle = TextStyle(fontSize = 14.sp)
        )
    }
}

@Composable
fun TonalButton(title: String, navigate: () -> Unit) {
    FilledTonalButton(modifier = Modifier
        .padding(vertical = 10.dp, horizontal = 20.dp)
        .fillMaxWidth(),
        onClick = {
            navigate()
        }) {
        Text(text = title, fontSize = 28.sp)
    }
}