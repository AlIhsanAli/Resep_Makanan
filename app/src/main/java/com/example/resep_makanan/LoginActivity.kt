package com.example.resep_makanan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import app.rive.ExperimentalRiveComposeAPI
import com.example.resep_makanan.ui.theme.BearLoginTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalRiveComposeAPI::class)
class LoginActivity : ComponentActivity() {

    val viewModel: BearViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BearLoginTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val focusManager = LocalFocusManager.current
                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current

                    val usernameTextFieldState = rememberTextFieldState()
                    val passwordTextFieldState = rememberTextFieldState()
                    var isPasswordVisible by remember { mutableStateOf(false) }
                    var isLoggingIn by remember { mutableStateOf(false) }

                    Box {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(16.dp)
                                .fillMaxSize()

                        ) {
                            Bear(
                                viewModel = viewModel,
                                modifier = Modifier.height(300.dp)
                            )
                            TextField(
                                state = usernameTextFieldState,
                                label = "Username",
                                enabled = !isLoggingIn,
                                onFocusChange = {
                                    viewModel.checking = it
                                },
                                onCursorPositionChange = {
                                    viewModel.hlook = it
                                },
                            )
                            Spacer(Modifier.height(16.dp))
                            TextField(
                                state = passwordTextFieldState,
                                label = "Password",
                                enabled = !isLoggingIn,
                                outputTransformation = {
                                    if (!isPasswordVisible) {
                                        for (i in 0 until length)
                                            replace(i, i + 1, "•")
                                    }
                                },
                                trailingIcon = {
                                    val image =
                                        if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                    IconButton(onClick = {
                                        isPasswordVisible = !isPasswordVisible
                                        viewModel.checking = isPasswordVisible
                                    }) {
                                        Icon(
                                            image,
                                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                onFocusChange = {
                                    viewModel.checking = isPasswordVisible
                                    viewModel.handUp = it
                                },
                                onCursorPositionChange = {
                                    viewModel.hlook = it
                                }
                            )
                            Spacer(Modifier.height(32.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        focusManager.clearFocus(true)
                                        isLoggingIn = true
                                        delay(2000)
                                        isLoggingIn = false
                                        if (usernameTextFieldState.text == "cebret" && passwordTextFieldState.text == "cebret12") {
                                            viewModel.success()
                                            snackbarHostState.showSnackbar("Login Success")
                                            delay(1000) // Tunggu sebentar agar pengguna melihat pesan
                                            val intent = Intent(context, MainActivity::class.java)
                                            context.startActivity(intent)
                                            (context as? Activity)?.finish() // Tutup LoginActivity
                                        } else {
                                            viewModel.fail()
                                            snackbarHostState.showSnackbar("Login Failed")
                                        }
                                    }
                                },
                                enabled = usernameTextFieldState.text.isNotBlank() && passwordTextFieldState.text.isNotBlank() && !isLoggingIn,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AnimatedVisibility(isLoggingIn) {
                                    Row {
                                        CircularProgressIndicator(
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                    }
                                }
                                Text("Login")
                            }
                        }
                        SnackbarHost(
                            snackbarHostState,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            val (contentColor, containerColor) =
                                if (it.visuals.message.contains("failed", true))
                                    MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                                else
                                    MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
                            Snackbar(
                                it,
                                contentColor = contentColor,
                                containerColor = containerColor,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}