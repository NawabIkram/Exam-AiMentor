package com.cssaimentor.app.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cssaimentor.app.ui.components.GlassCard
import com.cssaimentor.app.ui.components.MentorButton
import com.cssaimentor.app.ui.components.MentorOutlinedButton
import com.cssaimentor.app.ui.components.MentorTextField
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorError
import com.cssaimentor.app.ui.theme.MentorTextMuted
import com.cssaimentor.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onSignup: () -> Unit,
    onLoggedIn: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(state.authenticated) {
        if (state.authenticated) {
            viewModel.consumeAuthEvent()
            onLoggedIn()
        }
    }

    AuthShell(
        title = "Welcome back",
        subtitle = "Continue your CSS preparation with an AI-native study cockpit."
    ) {
        MentorTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        MentorTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            label = "Password",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = null)
                }
            }
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = viewModel::forgotPassword) {
                Text("Forgot password?", color = MentorCyan)
            }
        }
        StateMessage(error = state.error, info = state.info)
        MentorButton(
            text = "Login",
            onClick = viewModel::login,
            modifier = Modifier.fillMaxWidth(),
            loading = state.loading
        )
        MentorOutlinedButton(
            text = "Continue with Google",
            onClick = viewModel::googleSignInNotConfigured,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("New here?", color = MentorTextMuted)
            TextButton(onClick = onSignup) { Text("Create account", color = MentorCyan) }
        }
    }
}

@Composable
fun SignupScreen(
    onLogin: () -> Unit,
    onSignedUp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(state.authenticated) {
        if (state.authenticated) {
            viewModel.consumeAuthEvent()
            onSignedUp()
        }
    }

    AuthShell(
        title = "Create your mentor profile",
        subtitle = "Set up your study identity and start with curated CSS content."
    ) {
        MentorTextField(state.name, viewModel::updateName, "Full name")
        MentorTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        MentorTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            label = "Password",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = null)
                }
            }
        )
        StateMessage(error = state.error, info = state.info)
        MentorButton(
            text = "Create account",
            onClick = viewModel::signup,
            modifier = Modifier.fillMaxWidth(),
            loading = state.loading
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account?", color = MentorTextMuted)
            TextButton(onClick = onLogin) { Text("Login", color = MentorCyan) }
        }
    }
}

@Composable
private fun AuthShell(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = MentorTextMuted)
        Spacer(Modifier.height(28.dp))
        GlassCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }
    }
}

@Composable
private fun StateMessage(error: String?, info: String?) {
    when {
        error != null -> Text(error, color = MentorError, style = MaterialTheme.typography.bodyMedium)
        info != null -> Text(info, color = MentorCyan, style = MaterialTheme.typography.bodyMedium)
    }
}
