package com.ainotes.di

import android.content.Context
import com.ainotes.data.repository.AuthRepository
import com.ainotes.data.repository.AuthRepositoryImpl
import com.ainotes.data.repository.ProfileRepository
import com.ainotes.data.repository.ProfileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth,
        sessionRepository: com.ainotes.data.repository.SessionRepository
    ): AuthRepository = AuthRepositoryImpl(context, firebaseAuth, sessionRepository)

    @Provides
    @Singleton
    fun provideProfileRepository(
        firebaseFirestore: FirebaseFirestore
    ): ProfileRepository = ProfileRepositoryImpl(firebaseFirestore)
}

