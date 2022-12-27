package com.shrappz.gdgchennaigoodiedistrubutor.datasource

import com.shrappz.gdgchennaigoodiedistrubutor.model.CheckedInUser

interface GdgEventDataSource {

    suspend fun fetchAllCheckedInUsers(eventDayKey: String): List<CheckedInUser>
}