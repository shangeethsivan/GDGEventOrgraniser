@file:OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)

package com.shrappz.gdgchennaigoodiedistrubutor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shrappz.gdgchennaigoodiedistrubutor.ui.theme.GDGChennaiGoodieDistrubutorTheme


class AdminPanel : ComponentActivity() {


    val TAG = AdminPanel::class.java.name

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(res: FirebaseAuthUIAuthenticationResult?) {
        if (res?.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            Toast.makeText(this.baseContext, "Unknown User cannot login", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private val db = Firebase.firestore
    private var alertTitle = "Alert!"
    private var alertImageRes = R.drawable.warning
    private var alertMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }

        setContent {
            GDGChennaiGoodieDistrubutorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Devfest 2022 Chennai \n Day 1 \n LIVE CHECK-IN COUNT",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray
                        )

                        var mutableCheckInCount by remember { mutableStateOf(0) }

                        Text(
                            text = "CHECK IN COUNT - $mutableCheckInCount",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(3, 169, 244),
                            modifier = Modifier.padding(top = 40.dp, bottom = 40.dp)
                        )

                        Image(
                            painter = painterResource(id = R.drawable.devfest_logo),
                            contentDescription = "Devfest 2022 logo",
                            modifier = Modifier
                                .height(300.dp)
                                .width(300.dp)
                                .padding(top = 40.dp)
                        )
                        val openDialog = remember { mutableStateOf(false) }

                        if (openDialog.value) {
                            AlertDialog(
                                onDismissRequest = {
                                    openDialog.value = false
                                },
                                title = {
                                    Text(text = alertTitle, Modifier.padding(bottom = 20.dp))
                                },
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = alertImageRes),
                                            contentDescription = "Alert Image",
                                            modifier = Modifier
                                                .height(80.dp)
                                                .width(80.dp)
                                        )
                                        Text(
                                            alertMessage,
                                        )
                                    }
                                },
                                buttons = {
                                    Row(
                                        modifier = Modifier
                                            .padding(all = 8.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = {
                                                openDialog.value = false
                                            }
                                        ) {
                                            Text("Dismiss")
                                        }
                                    }
                                }
                            )
                        }
                        val docRef = db.collection("checked_in_users")
                        docRef.addSnapshotListener { snapshot, e ->

                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e)
                                return@addSnapshotListener
                            }
                            if (snapshot != null) {
                                mutableCheckInCount = snapshot.documents.size
                            } else {
                                mutableCheckInCount = 0
                            }
                        }

                    }
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        GDGChennaiGoodieDistrubutorTheme {
            Greeting("Android")
        }
    }

}