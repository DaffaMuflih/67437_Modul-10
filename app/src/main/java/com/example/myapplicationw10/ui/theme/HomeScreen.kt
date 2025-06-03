package com.example.myapplicationw10.ui.theme

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher untuk memilih gambar dari storage (image/*)
    val imagePickerLauncher = rememberLauncherForActivityResult(contract = GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Upload gambar ke Firebase Storage dan update profile picture
            authViewModel.uploadProfilePicture(it) { success, message ->
                snackbarMessage = if (success) {
                    "Profile picture updated!"
                } else {
                    message ?: "Upload failed."
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() }) { data ->
                Snackbar {
                    Text(data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome, ${authViewModel.getCurrentUser()?.email}")
            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Upload / Change Profile Picture
            Button(onClick = {
                imagePickerLauncher.launch("image/*")
            }) {
                Text("Upload/Change Profile Picture")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Delete Profile Picture
            Button(onClick = {
                authViewModel.deleteProfilePicture { success, message ->
                    snackbarMessage = if (success) {
                        "Profile picture deleted!"
                    } else {
                        message ?: "Delete failed."
                    }
                }
            }) {
                Text("Delete Profile Picture")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Logout
            Button(onClick = {
                authViewModel.signOut()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }) {
                Text("Logout")
            }
        }
    }

    // Menampilkan snackbar untuk pesan upload/delete
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }
}
