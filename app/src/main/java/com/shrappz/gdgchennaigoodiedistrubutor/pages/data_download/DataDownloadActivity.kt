package com.shrappz.gdgchennaigoodiedistrubutor.pages.data_download

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import com.shrappz.gdgchennaigoodiedistrubutor.ui.theme.GDGChennaiGoodieDistrubutorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DataDownloadActivity : ComponentActivity() {

    private val viewModel: DataDownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GDGChennaiGoodieDistrubutorTheme {
                val scaffoldState: ScaffoldState = rememberScaffoldState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState,
                    content = {
                        Column(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val uiData = viewModel.currentUiData
                            if (uiData.showDownloadButton) {
                                Button(onClick = { viewModel.onDownloadDataClicked() }) {
                                    Text(text = "Download Day 2 data")
                                }
                            }
                            if (uiData.showShareButton) {
                                Button(onClick = { viewModel.onShareClicked() }) {
                                    Text(text = "Share Day 2 Data file")
                                }
                            }

                            val action = uiData.uiAction
                            LaunchedEffect(key1 = action) {
                                when (action) {
                                    is DataDownloadViewModel.UiAction.ShareFile -> {
                                        val contentUri = FileProvider.getUriForFile(
                                            this@DataDownloadActivity.baseContext,
                                            FILE_PROVIDER_AUTHORITY,
                                            action.file
                                        )
                                        /** FILE_PROVIDER_AUTHORITY - "applicationId" + ".fileprovider" */

                                        if (contentUri != null) {
                                            val shareIntent = Intent(Intent.ACTION_SEND)
                                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            shareIntent.type = "application/excel"
                                            /** set the corresponding mime type of the file to be shared */
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

                                            startActivity(
                                                Intent.createChooser(
                                                    shareIntent,
                                                    "Share to"
                                                )
                                            )
                                        }
                                    }
                                    is DataDownloadViewModel.UiAction.ShowMessage -> {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = action.message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    null -> Unit
                                }
                                viewModel.uiActionCompleted()
                            }
                        }
                    }
                )
            }
        }
    }

    companion object {
        private const val FILE_PROVIDER_AUTHORITY =
            "com.shrappz.gdgchennaigoodiedistrubutor.fileprovider"

    }
}