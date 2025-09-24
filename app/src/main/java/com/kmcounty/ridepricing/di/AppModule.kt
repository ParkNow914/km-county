package com.kmcounty.ridepricing.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kmcounty.core.parser.RideParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Main Hilt module for app-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // DataStore extension
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideRideParser(): RideParser {
        return RideParser()
    }
}
