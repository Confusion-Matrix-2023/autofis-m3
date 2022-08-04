package me.siddheshkothadi.autofism3.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.database.DataStoreRepositoryImpl
import me.siddheshkothadi.autofism3.database.FishDatabase
import me.siddheshkothadi.autofism3.database.FishRepositoryImpl
import me.siddheshkothadi.autofism3.network.FileAPI
import me.siddheshkothadi.autofism3.repository.DataStoreRepository
import me.siddheshkothadi.autofism3.repository.FishRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

const val BASE_URL = "https://file-upload-server.siddheshkothadi.repl.co/"
//const val BASE_URL = "https://autofis-server.siddheshkothadi.repl.co/"
//const val BASE_URL = "http://127.0.0.1:5000/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext appContext: Context): FishApplication {
        return appContext as FishApplication
    }

    @Singleton
    @Provides
    fun provideRetrofit(): FileAPI {
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(FileAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideFishDatabase(appContext: FishApplication): FishDatabase {
        return Room.databaseBuilder(
            appContext,
            FishDatabase::class.java,
            "fish_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideFishRepository(fishDatabase: FishDatabase, fileAPI: FileAPI): FishRepository {
        return FishRepositoryImpl(fishDatabase.fishDAO(), fileAPI)
    }

    @Singleton
    @Provides
    fun provideDataStoreRepository(appContext: FishApplication): DataStoreRepository {
        return DataStoreRepositoryImpl(appContext)
    }
}