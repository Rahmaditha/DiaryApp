package com.cookiss.diaryapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.cookiss.diaryapp.data.repository.AuthRepositoryImpl
import com.cookiss.diaryapp.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(dataStore: DataStore<Preferences>): AuthRepository {
        return AuthRepositoryImpl(dataStore)
    }

//    @Provides
//    @Singleton
//    fun provideUserRepository(ktorApi: KtorApi): UserRepository {
//        return UserRepositoryImpl(ktorApi)
//    }
}