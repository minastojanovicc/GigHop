package com.example.gighop.screens

import Screen
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gighop.R
import com.example.gighop.components.ButtonComponent
import com.example.gighop.components.ClickableLoginTextComponent
import com.example.gighop.components.DividerTextComponent
import com.example.gighop.components.HeadingTextComponent
import com.example.gighop.components.PasswordTextFieldComponent
import com.example.gighop.components.TextFieldComponent
import com.example.gighop.viewmodel.AuthViewModel
import com.example.gighop.viewmodel.RegistrationState


@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState = viewModel.registrationState
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.homescreenimage),
                contentDescription = "GigHop Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
//            Spacer(modifier = Modifier.height(16.dp))

            HeadingTextComponent(value = "Welcome to GigHop!")
            Spacer(modifier = Modifier.height(20.dp))

            TextFieldComponent(
                labelValue = "Email",
                painterResource(id = R.drawable.email),
                value = email,
                onValueChange = { email = it }
            )
            PasswordTextFieldComponent(
                labelValue = "Password",
                painterResource(id = R.drawable.password),
                value = password,
                onValueChange = { password = it }
            )

            Spacer(modifier = Modifier.height(30.dp))

            ButtonComponent(value = "Login", onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.loginUser(email, password)
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            })

            when (loginState) {
                is RegistrationState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is RegistrationState.Success -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Map.name) {
                            popUpTo(Screen.LogIn.name) { inclusive = true }
                        }
                    }
                }
                is RegistrationState.Error -> {
                    val errorMessage = (loginState as RegistrationState.Error).message
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                else -> Unit
            }

            Spacer(modifier = Modifier.height(10.dp))

            DividerTextComponent()

            ClickableLoginTextComponent (tryingToLogin = false, onTextSelected = {
                navController.navigate(Screen.SignUp.name)
            })

        }
    }
}

