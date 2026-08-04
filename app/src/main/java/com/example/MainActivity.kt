package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.RouteBuddyApp
import com.example.ui.RouteBuddyViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: RouteBuddyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      RouteBuddyApp(viewModel = viewModel)
    }
  }
}
