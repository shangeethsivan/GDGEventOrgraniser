@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.shrappz.gdgchennaigoodiedistrubutor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


class MainActivity : ComponentActivity() {

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

    private var goodieChangeCounter = 0

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
                            text = "Devfest 2022 Chennai \n Day 1 CHECK IN",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(20.dp)
                        )
                        val openDialog = remember { mutableStateOf(false) }

                        val bookingIdState = remember { mutableStateOf(TextFieldValue("")) }
                        var selectedTShirtSize by remember { mutableStateOf("") }

                        if (openDialog.value) {
                            AlertDialog(
                                onDismissRequest = {
                                    openDialog.value = false
                                    bookingIdState.value = TextFieldValue("")
                                    selectedTShirtSize = ""
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
                                                selectedTShirtSize = ""
                                            }
                                        ) {
                                            Text("Dismiss")
                                        }
                                    }
                                }
                            )
                        }
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
                                imeAction = ImeAction.Next
                            )
                        )

                        val listItems = arrayOf("S", "M", "L", "XL", "XXL")

                        var expanded by remember {
                            mutableStateOf(false)
                        }

                        // the box
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {
                            // text field
                            TextField(
                                value = selectedTShirtSize,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(text = "T-Shirt Size") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors()
                            )

                            // menu
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listItems.forEach { selectedOption ->
                                    // menu item
                                    DropdownMenuItem(onClick = {
                                        selectedTShirtSize = selectedOption
                                        expanded = false
                                    }) {
                                        Text(text = selectedOption)
                                    }
                                }
                            }
                        }
                        var showProgressDialog by remember { mutableStateOf(false) }
                        if (showProgressDialog) {
                            DummyProgress(onDismiss = {
                                showProgressDialog = false
                            })
                        }

                        Button(modifier = Modifier.padding(top = 20.dp), onClick = {
                            val bookingId = bookingIdState.value.text
                            if (bookingId.isEmpty() || selectedTShirtSize.isEmpty()) {
                                alertTitle = "Alert!"
                                alertMessage = "Fill in Booking ID & T-Shirt Size"
                                alertImageRes = R.drawable.user_warning
                                openDialog.value = true
                            } else {
                                showProgressDialog = true
                                val userDetails = hashMapOf(
                                    "t_shirt_size" to selectedTShirtSize,
                                    "goodie_distributed" to false
                                )
                                db.collection("registered_users")
                                    .document(bookingId)
                                    .get().addOnCompleteListener { registedUser ->
                                        val result = registedUser.result
                                        val ticketName = result.get("ticket_name")
                                        val isConferencePass =
                                            ticketName == "Conference pass - Nov 12th only"
                                        if (result.exists() && isConferencePass) {
                                            val userName = registedUser.result.get("name")
                                            val userEmail = registedUser.result.get("email_id")
                                            val checkedInUsers = db.collection("checked_in_users")
                                            checkedInUsers.document(bookingId).get()
                                                .addOnCompleteListener {
                                                    if (!it.result.exists()) {
                                                        checkedInUsers
                                                            .document(bookingId)
                                                            .set(userDetails)
                                                            .addOnCompleteListener {
                                                                showProgressDialog = false
                                                                // show alert checked in successful
                                                                alertTitle = "Welcome $userName"
                                                                alertMessage = "Check In successful"
                                                                alertImageRes = R.drawable.success
                                                                openDialog.value = true
                                                            }.addOnFailureListener {
                                                                showProgressDialog = false
                                                                alertTitle = "Alert!"
                                                                alertMessage = "Check In Failed"
                                                                alertImageRes = R.drawable.warning
                                                                openDialog.value = true
                                                            }
                                                    } else {
                                                        showProgressDialog = false
                                                        alertTitle = "Already Checked IN!"
                                                        alertMessage =
                                                            "Name: $userName Email :$userEmail"
                                                        alertImageRes = R.drawable.block
                                                        openDialog.value = true
                                                    }
                                                }.addOnFailureListener {
                                                    showProgressDialog = false
                                                    alertTitle = "Alert!"
                                                    alertMessage = "Check In Failed"
                                                    alertImageRes = R.drawable.warning
                                                    openDialog.value = true
                                                }
                                        } else {
                                            showProgressDialog = false
                                            if (result.exists()) {
                                                alertTitle = "Not Eligible for Day 1 Conference"
                                                alertMessage =
                                                    "ID : $bookingId, \n Ticket Type : $ticketName"
                                                alertImageRes = R.drawable.block
                                                openDialog.value = true
                                            } else {
                                                alertTitle = "Unknown Booking ID"
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
                            Text(text = "CHECK-IN")
                        }

                        Image(
                            painter = painterResource(id = R.drawable.devfest_logo),
                            contentDescription = "Devfest 2022 logo",
                            modifier = Modifier
                                .height(250.dp)
                                .width(250.dp)
                                .padding(top = 10.dp)
                                .combinedClickable(onClick = {
                                    goodieChangeCounter++
                                    if (goodieChangeCounter > 10) {
                                        //launch Goodie page
                                        goodieChangeCounter = 0
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                GoodieDistributionActivity::class.java
                                            )
                                        )
                                    }
                                }, onLongClick = {
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            AdminPanel::class.java
                                        )
                                    )
                                }),
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