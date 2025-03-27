package com.electrolytej.main.page.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WelcomeScreen(controller: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = {
            controller.navigate("basic")
        }) {
            Text("Basic Sample", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            controller.navigate("html")
        }) {
            Text("HTML Sample", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            controller.navigate("tab")
        }) {
            Text("SaveState Sample", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            controller.navigate("intercept")
        }) {
            Text("Intercept Request Sample", fontSize = 18.sp)
        }
    }
}