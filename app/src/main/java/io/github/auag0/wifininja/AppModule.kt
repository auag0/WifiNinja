package io.github.auag0.wifininja

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.auag0.wifininja.repository.WifiRepository
import io.github.auag0.wifininja.services.WifiServiceManager
import io.github.auag0.wifininja.utils.WifiConfigStorageManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideWifiServiceManager(
        @ApplicationContext appContext: Context
    ): WifiServiceManager {
        return WifiServiceManager(appContext)
    }

    @Provides
    fun provideWifiRepository(
        wifiServiceManager: WifiServiceManager
    ): WifiRepository {
        return WifiRepository(wifiServiceManager)
    }

    @Provides
    fun provideWifiConfigStorageManager(
        @ApplicationContext appContext: Context
    ): WifiConfigStorageManager {
        return WifiConfigStorageManager(appContext)
    }
}