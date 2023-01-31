package com.shrappz.gdgchennaigoodiedistrubutor.new.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.export.dataexport.DataExport
import com.regdesk.checkin.CheckIn
import com.regdesk.checkout.CheckOut
import com.regdesk.home.Home
import com.regdesk.livestats.LiveStats
import com.shrappz.gdgchennaigoodiedistrubutor.new.Destination

@Composable
fun RegDeskNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = startDestination.name, modifier) {
        composable(Destination.Home.name) { Home() }
        composable(Destination.CheckIn.name) { CheckIn() }
        composable(Destination.CheckOut.name) { CheckOut() }
        composable(Destination.Stats.name) { LiveStats() }
        composable(Destination.Export.name) { DataExport() }
    }
}