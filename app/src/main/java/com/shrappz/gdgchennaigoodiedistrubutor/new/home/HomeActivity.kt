package com.shrappz.gdgchennaigoodiedistrubutor.new.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.shrappz.gdgchennaigoodiedistrubutor.new.Destination
import com.shrappz.gdgchennaigoodiedistrubutor.new.navhost.RegDeskNavHost

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold { padding ->
                val navController = rememberNavController()
                var currentDestination by remember {
                    mutableStateOf(Destination.Home)
                }
                Row(Modifier.padding(padding)) {
                    RegDeskNavHost(
                        navController,
                        modifier = Modifier.weight(1f),
                        startDestination = Destination.Home
                    )
                    NavigationRail {
                        Spacer(modifier = Modifier.weight(1f))
                        NavigationRailItem(
                            selected = currentDestination == Destination.Home,
                            onClick = { currentDestination = Destination.Home },
                            icon = {
                                Icon(Icons.Filled.Home, "")
                            })
                        NavigationRailItem(
                            selected = currentDestination == Destination.CheckIn,
                            onClick = { currentDestination = Destination.CheckIn },
                            icon = {
                                Icon(Icons.Filled.Check, "")
                            })
                        NavigationRailItem(
                            selected = currentDestination == Destination.CheckOut,
                            onClick = { currentDestination = Destination.CheckOut },
                            icon = {
                                Icon(Icons.Filled.CheckCircle, "")
                            })
                        NavigationRailItem(
                            selected = currentDestination == Destination.Stats,
                            onClick = { currentDestination = Destination.Stats },
                            icon = {
                                Icon(Icons.Filled.Star, "")
                            })
                        NavigationRailItem(
                            selected = currentDestination == Destination.Export,
                            onClick = { currentDestination = Destination.Export },
                            icon = {
                                Icon(Icons.Filled.Email, "")
                            })
                    }
                }
                BackHandler(enabled = true, onBack = {
                    if (navController.popBackStack()) {
                        navController.currentDestination?.route?.let {
                            currentDestination = Destination.valueOf(it)
                        }
                    } else {
                        finish()
                    }
                })
                navController.navigate(currentDestination.name) {
                    launchSingleTop = true
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
                }
            }

        }
    }
}