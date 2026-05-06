package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ljdit.digitalpublishing.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel()
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            // 🔥 Título
            Text(
                text = "Digital Publishing Assistant",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F3B5B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(80.dp))

            // 🔥 Logo placeholder
            Text(
                text = "Logo",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F3B5B)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // 🔥 Card login
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Text(
                        text = "Usuario:",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Contraseña:",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation =
                        PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {

                            viewModel.login(
                                context,
                                username,
                                password
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF28A745)
                        ),

                        shape = RoundedCornerShape(8.dp)
                    ) {

                        Text(
                            text = "Iniciar Sesión",
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text =
                "💡 Contacte al administrador si olvidó sus credenciales",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}