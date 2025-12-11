package com.weathersecondapp

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weathersecondapp.ui.theme.WeatherSecondAPPTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSecondAPPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val activity = LocalActivity.current as Activity
    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = "Cadastre-se!!!",
            fontSize = 24.sp
        )

        OutlinedTextField(
            value = nome,
            label = { Text(text = "Digite seu nome") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { nome = it }
        )

        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { email = it }
        )

        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            label = { Text(text = "Confirme sua senha") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { confirmPassword = it }
        )

        Row(modifier = modifier) {
            Button(  onClick = {
                Toast.makeText(activity, "Cadastro feito!", Toast.LENGTH_LONG).show()
                activity.startActivity(
                    Intent(activity, MainActivity::class.java).setFlags(
                        FLAG_ACTIVITY_SINGLE_TOP
                    )
                )
            },
                enabled = email.isNotEmpty() && password.isNotEmpty() && nome.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
                ) {
                Text("Cadastrar-se")
            }

            Button(
                onClick = { email = ""; password = "" }
            ) {
                Text("Limpar")
            }
        }
    }
}