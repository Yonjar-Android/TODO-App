package com.example.todoapp.tasks.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BackgroundScreen(@DrawableRes image: Int, screen: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = image), contentDescription = "background",
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
fun TextFieldComp(
    labelField: String,
    text:String,
    height: Dp = 50.dp,
    singleL:Boolean = true,
    maxLin:Int = 1,
    readOnlyValue:Boolean = false,
    onValueChanged: (String) -> Unit
) {
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
            onValueChange = {
                onValueChanged(it)
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .size(height = height, width = 0.dp),
            textStyle = TextStyle(fontSize = 14.sp),
            singleLine = singleL,
            maxLines = maxLin,
            readOnly = readOnlyValue
        )
    }
}

@Composable
fun TonalButton(
    title: String,
    modifier: Modifier = Modifier,
    color: ButtonColors = ButtonDefaults.buttonColors(containerColor = Color.White),
    onClick: () -> Unit
) {
    FilledTonalButton(
        modifier = modifier
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = color
    ) {
        Text(text = title, fontSize = 28.sp, color = Color.Black)
    }
}

@Composable
fun Loading() {
    Box(
        Modifier
            .background(Color.White)
            .padding(5.dp)
    ) {
        CircularProgressIndicator()
    }

}

fun errorFun(error: String, context: Context) {
    if (error.isNotBlank()) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }
}