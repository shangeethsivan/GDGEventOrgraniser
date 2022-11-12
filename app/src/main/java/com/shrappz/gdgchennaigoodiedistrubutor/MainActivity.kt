@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.shrappz.gdgchennaigoodiedistrubutor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shrappz.gdgchennaigoodiedistrubutor.components.DummyProgress
import com.shrappz.gdgchennaigoodiedistrubutor.ui.theme.GDGChennaiGoodieDistrubutorTheme
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.flow.MutableStateFlow


class MainActivity : ComponentActivity() {

    val TAG = this.javaClass.name

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    val day2TicketTypes = listOf(
        "Android Jetpack Compose Camp by Somasundaram Mahesh",
        "How to build the best packages and plugins in Flutter by Vivek Yadav",
        "Build your first MLOps Pipeline using ZenML on GCP by Jayesh Sharma",
        "Cloud Hero - RGDC - Intro to Infrastructure by Manikandan Krishnamurthy",
    )

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

    private val confirmAlertTitle = "Confirm Details"
    private var confirmUserName = ""
    private var confirmTicketType = ""

    private var goodieChangeCounter = 0
    private var adminPanelCounter = 0

    private val openAlertDialog = MutableStateFlow(false)

    private val openConfirmDialog = MutableStateFlow(false)

    private val selectedTShirtSize = MutableStateFlow("")

    private val showProgressDialog = MutableStateFlow(false)

    private val bookingId = MutableStateFlow(TextFieldValue(""))

    val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result ->
        when (result) {
            is QRResult.QRError -> {
                alertTitle = "Unknown QR"
                alertMessage = "Unknown QR"
                alertImageRes = R.drawable.block
            }
            QRResult.QRMissingPermission -> {
                alertTitle = "CAMERA PERMISSION"
                alertMessage = "GIVE PERMISSION TO APP in APP settings"
                alertImageRes = R.drawable.user_warning
            }
            is QRResult.QRSuccess -> {
                Log.d(TAG, "result content::${result.content.rawValue}")
                bookingId.value = TextFieldValue(result.content.rawValue)
            }
            QRResult.QRUserCanceled -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
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
                            text = "Devfest 2022 Chennai \n Day 2 CHECK IN",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(20.dp)
                        )
                        val bookingIdState = bookingId.collectAsState(TextFieldValue(""))

                        if (openAlertDialog.collectAsState().value) {
                            AlertDialog(
                                onDismissRequest = {
                                    openAlertDialog.value = false
                                    selectedTShirtSize.value = ""
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
                                                .height(50.dp)
                                                .width(50.dp)
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
                                                openAlertDialog.value = false
                                                selectedTShirtSize.value = ""
                                            }
                                        ) {
                                            Text("Dismiss")
                                        }
                                    }
                                }
                            )
                        }

                        if (openConfirmDialog.collectAsState().value) {
                            AlertDialog(
                                onDismissRequest = {
                                    openConfirmDialog.value = false
                                    confirmUserName = ""
                                    confirmTicketType = ""
                                },
                                title = {
                                    Text(text = confirmAlertTitle, Modifier.padding(bottom = 20.dp))
                                },
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.user_warning),
                                            contentDescription = "Confirm Alert Image",
                                            modifier = Modifier
                                                .height(50.dp)
                                                .width(50.dp)
                                        )
                                        Text(
                                            stringResource(
                                                id = R.string.confirm_message,
                                                confirmUserName,
                                                confirmTicketType
                                            ),
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
                                                val localUserName = confirmUserName
                                                onCheckInClick(localUserName)
                                                openConfirmDialog.value = false
                                                confirmUserName = ""
                                                confirmTicketType = ""
                                            }
                                        ) {
                                            Text("Check-IN")
                                        }
                                        Button(
                                            modifier = Modifier.padding(start = 10.dp),
                                            onClick = {
                                                openConfirmDialog.value = false
                                                confirmUserName = ""
                                                confirmTicketType = ""
                                            }
                                        ) {
                                            Text("Dismiss")
                                        }
                                    }
                                }
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.qr_scanner),
                            contentDescription = "QR Scanner",
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                                .padding(top = 20.dp)
                                .clickable(onClick = {
                                    scanQrCodeLauncher.launch(null)
                                })
                        )
                        TextField(
                            modifier = Modifier.padding(top = 20.dp),
                            value = bookingIdState.value,
                            onValueChange = {
                                bookingId.value = it
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "clear text",
                                    modifier = Modifier
                                        .clickable {
                                            bookingId.value = TextFieldValue("")
                                        }
                                )
                            },
                            label = { Text(text = "Booking ID") },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
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
                                value = selectedTShirtSize.collectAsState().value,
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
                                        selectedTShirtSize.value = selectedOption
                                        expanded = false
                                    }) {
                                        Text(text = selectedOption)
                                    }
                                }
                            }
                        }
                        if (showProgressDialog.collectAsState().value) {
                            DummyProgress(onDismiss = {
                                showProgressDialog.value = false
                            })
                        }

                        val context = LocalContext.current
                        Button(modifier = Modifier.padding(top = 20.dp), onClick = {
                            onFetchDetailsClicked(context)
                        }) {
                            Text(text = "Fetch Details")
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
                                    adminPanelCounter++
                                    if (adminPanelCounter > 1) {
                                        //launch Goodie page
                                        adminPanelCounter = 0
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                AdminPanel::class.java
                                            )
                                        )
                                    }
                                }),
                        )
                    }
                }
            }
        }
    }

    fun onFetchDetailsClicked(context: Context) {
        try {
            val bookingIdLocal = bookingId.value.text
            val tShirtSizeLocal = selectedTShirtSize.value
            if (bookingIdLocal.isEmpty() || tShirtSizeLocal.isEmpty()) {
                alertTitle = "Alert!"
                alertMessage = "Fill in Booking ID & T-Shirt Size"
                alertImageRes = R.drawable.user_warning
                openAlertDialog.value = true
            } else {
                showProgressDialog.value = true
                db.collection("registered_users")
                    .document(bookingIdLocal)
                    .get().addOnCompleteListener { registedUserTask ->
                        if (registedUserTask.isSuccessful) {
                            val result = registedUserTask.result
                            val ticketName = result.get("ticket_name")
                            val isValidTicket = ticketName in day2TicketTypes
                            if (result.exists() && isValidTicket) {
                                val userName =
                                    registedUserTask.result.get("name")
                                confirmUserName = userName.toString()
                                confirmTicketType = ticketName.toString()
                                openConfirmDialog.value = true
                            } else {
                                showProgressDialog.value = false
                                if (result.exists()) {
                                    alertTitle =
                                        "Not Eligible for Day 2 Conference"
                                    alertMessage =
                                        "ID : $bookingIdLocal, \n Ticket Type : $ticketName"
                                    alertImageRes = R.drawable.block
                                    openAlertDialog.value = true
                                } else {
                                    alertTitle = "Unknown Booking ID"
                                    alertMessage = "ID : $bookingIdLocal"
                                    alertImageRes = R.drawable.block
                                    openAlertDialog.value = true
                                }
                            }
                        } else {
                            showProgressDialog.value = false
                            alertTitle = "No Permission"
                            alertMessage = "Pls contact admin for permission"
                            alertImageRes = R.drawable.warning
                            openAlertDialog.value = true
                        }
                    }.addOnFailureListener {
                        if (it is FirebaseFirestoreException && it.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            showProgressDialog.value = false
                            alertTitle = "No Check-IN Permission"
                            alertMessage = "Pls contact admin for permission"
                            alertImageRes = R.drawable.user_warning
                            openAlertDialog.value = true
                        } else {
                            showProgressDialog.value = false
                            alertTitle = "Booking ID FETCH Failed"
                            alertMessage = "ID : $bookingIdLocal"
                            alertImageRes = R.drawable.warning
                            openAlertDialog.value = true
                        }
                    }
            }
        } catch (exception: Exception) {
            Toast.makeText(
                context,
                "No Permission/Internet Contact admin",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onCheckInClick(userName: String) {
        val bookingIdLocal = bookingId.value.text
        val tShirtSize = selectedTShirtSize.value
        val userDetails = mapOf(
            "goodie_distributed" to false,
            "t_shirt_size" to tShirtSize,
        )
        val checkedInUsers =
            db.collection("checked_in_users_day2")
        checkedInUsers.document(bookingIdLocal).get()
            .addOnCompleteListener {
                if (!it.result.exists()) {
                    checkedInUsers
                        .document(bookingIdLocal)
                        .set(userDetails)
                        .addOnCompleteListener {
                            showProgressDialog.value = false
                            // show alert checked in successful
                            alertTitle =
                                "Welcome $userName"
                            alertMessage =
                                "Check In successful"
                            alertImageRes =
                                R.drawable.success
                            openAlertDialog.value = true
                        }.addOnFailureListener {
                            showProgressDialog.value = false
                            alertTitle = "Alert!"
                            alertMessage =
                                "Check In Failed"
                            alertImageRes =
                                R.drawable.warning
                            openAlertDialog.value = true
                        }
                } else {
                    showProgressDialog.value = false
                    alertTitle = "Already Checked IN!"
                    alertMessage = "Name: $userName"
                    alertImageRes = R.drawable.block
                    openAlertDialog.value = true
                }
            }.addOnFailureListener {
                showProgressDialog.value = false
                alertTitle = "Alert!"
                alertMessage = "Check In Failed"
                alertImageRes = R.drawable.warning
                openAlertDialog.value = true
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