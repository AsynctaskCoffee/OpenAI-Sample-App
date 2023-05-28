package com.openai.chatforall.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.openai.chatforall.data.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var imageUrl: String

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission has been granted, we can proceed with downloading the image
                lifecycleScope.launch {
                    downloadImage(imageUrl)
                }
            } else {
                // Explain to the user that the feature is unavailable because the features requires a permission that the user has denied.
                Toast.makeText(
                    this,
                    "Permission denied, we can't download the image",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen {
                checkPermissionsAndDownload(it)
            }
        }
    }

    private fun checkPermissionsAndDownload(imageUrl: String) {
        this.imageUrl = imageUrl
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // We have the permission, we can proceed with downloading the image
                lifecycleScope.launch {
                    downloadImage(imageUrl)
                }
            }

            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // We should show an explanation to the user, then ask for the permission
                Toast.makeText(
                    this,
                    "We need permission to write to storage to download the image",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            else -> {
                // No explanation needed; request the permission
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private suspend fun downloadImage(imageUrl: String) = withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()

            val input = BufferedInputStream(url.openStream(), 8192)

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString()

            // Using UUID to make the filename unique
            val fileName = "OpenAI_" + UUID.randomUUID().toString() + ".jpg"
            val output = FileOutputStream(File(path, fileName))

            val data = ByteArray(1024)
            var total = 0
            var count: Int

            while (input.read(data).also { count = it } != -1) {
                total += count
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

