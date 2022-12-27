package com.shrappz.gdgchennaigoodiedistrubutor.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shrappz.gdgchennaigoodiedistrubutor.csv.CSVHelper
import com.shrappz.gdgchennaigoodiedistrubutor.csv.CSVHelperImpl
import com.shrappz.gdgchennaigoodiedistrubutor.datasource.FirestoreDataSource
import com.shrappz.gdgchennaigoodiedistrubutor.datasource.GdgEventDataSource
import com.shrappz.gdgchennaigoodiedistrubutor.repository.GdgEventDataRepository
import com.shrappz.gdgchennaigoodiedistrubutor.repository.GdgEventDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindRepo(
        repoImpl: GdgEventDataRepositoryImpl
    ): GdgEventDataRepository

    @Binds
    @Singleton
    abstract fun bindDataSource(
        repoImpl: FirestoreDataSource
    ): GdgEventDataSource

    @Binds
    @Singleton
    abstract fun bindCSVHelper(
        repoImpl: CSVHelperImpl
    ): CSVHelper

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore {
            return Firebase.firestore
        }
    }
}