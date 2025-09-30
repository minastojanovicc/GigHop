package com.example.gighop.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.gighop.R
import com.example.gighop.components.ButtonComponent
import com.example.gighop.components.ClickableLoginTextComponent
import com.example.gighop.components.DividerTextComponent
import com.example.gighop.components.HeadingTextComponent
import com.example.gighop.components.ImagePicker
import com.example.gighop.components.TextFieldComponent
import com.example.gighop.components.NormalTextComponent
import com.example.gighop.components.PasswordTextFieldComponent
import com.example.gighop.viewmodel.AuthViewModel
import com.example.gighop.viewmodel.RegistrationState

@Composable
fun SignUpScreen(navController: NavHostController, viewModel: AuthViewModel) {

    var username by remember { mutableStateOf("")}
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var profileImage = remember { mutableStateOf(Uri.EMPTY) }

    val registrationState = viewModel.registrationState
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.height(20.dp))
            HeadingTextComponent(value = "Create an account")
            Spacer(modifier = Modifier.height(20.dp))

            ImagePicker(selectedImageUri = profileImage)

            Spacer(modifier = Modifier.height(20.dp))

            TextFieldComponent(
                labelValue = "Username",
                painterResource(id = R.drawable.profile),
                value = username,
                onValueChange = {username = it}
            )
            TextFieldComponent(
                labelValue = "Fullname",
                painterResource = painterResource(id = R.drawable.profile),
                value = fullname,
                onValueChange = {fullname = it}
            )
            TextFieldComponent(
                labelValue = "Email",
                painterResource = painterResource(id = R.drawable.email),
                value = email,
                onValueChange = { email = it }
            )

            TextFieldComponent(
                labelValue = "Phone",
                painterResource = painterResource(id = R.drawable.phone),
                value = phoneNumber,
                onValueChange = {phoneNumber = it}
            )
            PasswordTextFieldComponent(
                labelValue = "Password",
                painterResource = painterResource(id = R.drawable.password) ,
                value = password,
                onValueChange = {password = it}
            )

            Spacer(modifier = Modifier.height(32.dp))

            ButtonComponent(value = "Register", onClick = {
                val usernameError = viewModel.validateUsername(username)
                val fullnameError = viewModel.validateFullName(fullname)
                val phoneNumberError = viewModel.validatePhoneNumber(phoneNumber)
                val emailError = viewModel.validateEmail(email)
                val passwordError = viewModel.validatePassword(password)
                val imageError = viewModel.validateImage(profileImage.value)

                when {
                    usernameError != null -> {
                        Toast.makeText(context, usernameError, Toast.LENGTH_SHORT).show()
                    }
                    fullnameError != null -> {
                        Toast.makeText(context, fullnameError, Toast.LENGTH_SHORT).show()
                    }
                    phoneNumberError != null -> {
                        Toast.makeText(context, phoneNumberError, Toast.LENGTH_SHORT).show()
                    }
                    emailError != null -> {
                        Toast.makeText(context, emailError, Toast.LENGTH_SHORT).show()
                    }
                    passwordError != null -> {
                        Toast.makeText(context, passwordError, Toast.LENGTH_SHORT).show()
                    }
                    imageError != null -> {
                        Toast.makeText(context, imageError, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        viewModel.registerUser(username, email, password,fullname, phoneNumber, profileImage.value)
                    }
                }
            })

            when (registrationState) {
                is RegistrationState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        CircularProgressIndicator()
                    }
                }
                is RegistrationState.Success -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Map.name) {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                }
                is RegistrationState.Error -> {
                    val errorMessage = (registrationState as RegistrationState.Error).message
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                else -> Unit
            }

            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()

            ClickableLoginTextComponent (tryingToLogin = true, onTextSelected = {
                navController.navigate("login")
            })


        }
    }
}
