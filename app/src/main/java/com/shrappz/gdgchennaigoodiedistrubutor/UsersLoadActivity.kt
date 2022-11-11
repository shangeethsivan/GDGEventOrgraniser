@file:OptIn(ExperimentalMaterialApi::class)

package com.shrappz.gdgchennaigoodiedistrubutor

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shrappz.gdgchennaigoodiedistrubutor.ui.theme.GDGChennaiGoodieDistrubutorTheme
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UsersLoadActivity : ComponentActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GDGChennaiGoodieDistrubutorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        Text(text = "Pls Wait.. inserting data lookout for toast")
                    }
                }
            }
        }

        lifecycleScope.launch {
            try {

                val inputStream: InputStream = assets.open("devfest_data.csv")
                val reader =
                    BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
                var skipFirst = false
                var counter = 0
                reader.readLines().forEach {
                    //get a string array of all items in this line
                    if (!skipFirst) {
                        skipFirst = true
                        return@forEach
                    }
                    val row = it.split(",")
                    val userDetails = mapOf(
                        "name" to row[1],
                        "email_id" to row[2],
                        "ticket_name" to row[3],
                    )
                    suspendCoroutine<Unit> { cont ->
                        db.collection("registered_users")
                            .document(row[0])
                            .set(userDetails)
                            .addOnSuccessListener {
                                counter++
//                                Log.d("Main", "success")
                                cont.resume(Unit)
                            }.addOnFailureListener { exception ->
                                Log.d("Main", "failure ${exception.message}")
                                cont.resumeWithException(exception)
                            }
                    }
                }
                println("Successfully inserted rows: $counter")
            } catch (e: IOException) {
                Log.e("MainActivity", "${e.message}")
            }
        }
    }
}