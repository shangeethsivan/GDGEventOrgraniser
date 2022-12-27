package com.shrappz.gdgchennaigoodiedistrubutor.repository

import com.shrappz.gdgchennaigoodiedistrubutor.datasource.GdgEventDataSource
import com.shrappz.gdgchennaigoodiedistrubutor.model.CheckedInUser
import javax.inject.Inject

class GdgEventDataRepositoryImpl @Inject constructor(
    private val gdgEventDataSource: GdgEventDataSource
) : GdgEventDataRepository {

    override suspend fun fetchAllCheckedInUsers(eventDayKey: String): List<CheckedInUser> {
        return gdgEventDataSource.fetchAllCheckedInUsers(eventDayKey)
    }
}