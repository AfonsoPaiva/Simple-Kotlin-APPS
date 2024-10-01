package com.example.calculator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
   Column(modifier = modifier) {
       Text(text="0")
       Row {
           Button(onClick = { /* COISO*/}) {
               Text(text="7")
           }
           Button(onClick = { /* COISO*/}) {
               Text(text="8")
           }
       }
   }
}




