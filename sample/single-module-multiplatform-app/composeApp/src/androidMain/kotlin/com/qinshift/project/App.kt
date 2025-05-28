@file:Suppress("WildcardImport", "NoWildcardImports", "ImportOrdering")
package com.qinshift.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.Credits
import presentation.Demo

import single_module_multiplatform_app.composeapp.generated.resources.Res
import single_module_multiplatform_app.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text(Demo.buttonLabel)
            }
            AnimatedVisibility(showContent) {
                val firstGreeting = remember { Greeting().greet(1) }
                val secondGreeting = remember { Greeting().greet(2) }
                val thirdGreeting = remember { Greeting().alternativeGreet(3) }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $firstGreeting")
                    Text("Compose: $secondGreeting")
                    Text("Compose: $thirdGreeting")
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                Credits.info,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}