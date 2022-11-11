@file:OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)

package com.shrappz.gdgchennaigoodiedistrubutor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
import com.shrappz.gdgchennaigoodiedistrubutor.components.DummyProgress
import com.shrappz.gdgchennaigoodiedistrubutor.ui.theme.GDGChennaiGoodieDistrubutorTheme


class GoodieDistributionActivity : ComponentActivity() {

    val TAG = this.javaClass.name

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

                    var showProgressDialog by remember { mutableStateOf(false) }
                    if (showProgressDialog) {
                        DummyProgress(onDismiss = {
                            showProgressDialog = false
                        })
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Devfest 2022 Chennai \n Day 1 \n GOODIE DISTRIBUTION",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray
                        )
                        val openDialog = remember { mutableStateOf(false) }

                        val bookingIdState = remember { mutableStateOf(TextFieldValue("")) }

                        if (openDialog.value) {
                            AlertDialog(
                                onDismissRequest = {
                                    openDialog.value = false
                                    bookingIdState.value = TextFieldValue("")
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
                                                bookingIdState.value = TextFieldValue("")
                                            }
                                        ) {
                                            Text("Dismiss")
                                        }
                                    }
                                }
                            )
                        }

                        val keyboardController = LocalSoftwareKeyboardController.current
                        TextField(
                            modifier = Modifier.padding(top = 50.dp),
                            value = bookingIdState.value,
                            onValueChange = {
                                bookingIdState.value = it
                            },
                            label = { Text(text = "Booking ID") },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )

                        Button(modifier = Modifier.padding(top = 20.dp), onClick = {
                            val bookingId = bookingIdState.value.text
                            if (bookingId.isEmpty()) {
                                alertTitle = "Alert!"
                                alertMessage = "Fill in Booking ID"
                                alertImageRes = R.drawable.user_warning
                                openDialog.value = true
                            } else {
                                showProgressDialog = true
                                val userDetails = mapOf(
                                    "goodie_distributed" to true,
                                )
                                val checkedInUsers = db.collection("checked_in_users")
                                checkedInUsers.document(bookingId).get()
                                    .addOnCompleteListener { checkedInUser ->
                                        val hasReceivedGoodie =
                                            checkedInUser.result.get("goodie_distributed") == true
                                        if (checkedInUser.result.exists() && !hasReceivedGoodie) {
                                            val tShirtSize =
                                                checkedInUser.result.get("t_shirt_size")
                                            checkedInUsers
                                                .document(bookingId)
                                                .update(userDetails)
                                                .addOnCompleteListener { registedUser ->
                                                    showProgressDialog = false
                                                    alertTitle = "Eligible for GOODIE"
                                                    alertMessage =
                                                        "ID : $bookingId \n T-SHIRT SIZE: $tShirtSize"
                                                    alertImageRes = R.drawable.success
                                                    openDialog.value = true
                                                }.addOnFailureListener {
                                                    showProgressDialog = false
                                                    alertTitle = "Booking ID FETCH Failed"
                                                    alertMessage = "ID : $bookingId"
                                                    alertImageRes = R.drawable.warning
                                                    openDialog.value = true
                                                }
                                        } else {
                                            showProgressDialog = false
                                            if (checkedInUser.result.exists()) {
                                                alertTitle = "Goodie Already Distributed"
                                                alertMessage = "ID : $bookingId"
                                                alertImageRes = R.drawable.block
                                                openDialog.value = true
                                            } else {
                                                alertTitle =
                                                    "Unknown Booking ID / User Has not checked In"
                                                alertMessage = "ID : $bookingId"
                                                alertImageRes = R.drawable.block
                                                openDialog.value = true
                                            }
                                        }
                                    }.addOnFailureListener {
                                        showProgressDialog = false
                                        alertTitle = "Booking ID FETCH Failed"
                                        alertMessage = "ID : $bookingId"
                                        alertImageRes = R.drawable.warning
                                        openDialog.value = true
                                    }
                            }
                        }) {
                            Text(text = "DISTRIBUTE GOODIE")
                        }
                        Image(
                            painter = painterResource(id = R.drawable.devfest_logo),
                            contentDescription = "Devfest 2022 logo",
                            modifier = Modifier
                                .height(300.dp)
                                .width(300.dp)
                                .padding(top = 10.dp)
                        )
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