@file:OptIn(ExperimentalMaterialApi::class)

package com.shrappz.gdgchennaigoodiedistrubutor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
            Toast.makeText(this.baseContext, "Unknow User cannot login", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    val db = Firebase.firestore

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
                    Column {
                        val bookingId = remember { mutableStateOf(TextFieldValue("")) }
                        TextField(value = bookingId.value, onValueChange = {
                            bookingId.value = it
                        }, label = { Text(text = "Booking ID") })

                        val contextForToast = LocalContext.current.applicationContext

                        val listItems = arrayOf("S", "M", "L", "XL", "XXL")

                        var selectedItem by remember { mutableStateOf("") }

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
                                value = selectedItem,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(text = "Label") },
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
                                        selectedItem = selectedOption
                                        Toast.makeText(
                                            contextForToast,
                                            selectedOption,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        expanded = false
                                    }) {
                                        Text(text = selectedOption)
                                    }
                                }
                            }
                        }
                        Button(onClick = {
// Add a new document with a generated ID
                            val userDetails = hashMapOf(
                                "first" to "Ada",
                                "last" to "Lelace2",
                                "born" to 1815
                            )
                            db.collection("users")
                                .document("10134")
                                .get().addOnCompleteListener {
                                    Log.e(TAG, ":: result -- ${it.result.data}")
                                }.addOnSuccessListener {
                                    Log.e(TAG, ":: result success-- ${it.data}")
                                }.addOnFailureListener {
                                    Log.e(TAG, ":: resultfailure --")
                                }

                            db.collection("users")
                                .document("10134")
                                .set(userDetails)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot added with ID: $documentReference"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        }) {
                            Text(text = "Register")
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