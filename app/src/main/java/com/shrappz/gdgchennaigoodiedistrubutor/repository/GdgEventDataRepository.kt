package com.shrappz.gdgchennaigoodiedistrubutor.repository

import com.shrappz.gdgchennaigoodiedistrubutor.model.CheckedInUser

interface GdgEventDataRepository {

    suspend fun fetchAllCheckedInUsers(eventDayKey: String): List<CheckedInUser>
}