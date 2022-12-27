package com.shrappz.gdgchennaigoodiedistrubutor.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.shrappz.gdgchennaigoodiedistrubutor.model.CheckedInUser
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreDataSource @Inject constructor(private val firebaseFireStoreDB: FirebaseFirestore) :
    GdgEventDataSource {

    override suspend fun fetchAllCheckedInUsers(eventDayKey: String): List<CheckedInUser> {
        return suspendCoroutine { cont ->
            firebaseFireStoreDB.collection(eventDayKey).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val list = mutableListOf<CheckedInUser>()
                    it.result.documents.forEach { docSnapShot ->
                        val id = docSnapShot.id
                        val goodieDistributed =
                            docSnapShot.data?.get("goodie_distributed") as Boolean
                        list.add(CheckedInUser(id, goodieDistributed))
                    }
                    cont.resume(list)
                } else {
                    cont.resumeWithException(Exception("Task not successful"))
                }
            }.addOnFailureListener {
                cont.resumeWithException(it)
            }
        }
    }

}