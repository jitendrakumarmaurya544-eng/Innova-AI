package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.ui.screens.InnovaMainView
import com.example.ui.theme.InnovaAiTheme
import com.example.viewmodel.InnovaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: InnovaViewModel by viewModels()

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission result handled gracefully
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check & request RECORD_AUDIO permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            InnovaAiTheme(darkTheme = true) {
                InnovaMainView(viewModel = viewModel)
            }
        }
    }
}
