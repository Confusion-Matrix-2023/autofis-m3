package me.siddheshkothadi.autofism3.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.database.PendingUploadFishDatabase
import me.siddheshkothadi.autofism3.database.UploadHistoryFishDatabase
import me.siddheshkothadi.autofism3.datastore.LocalDataStore
import me.siddheshkothadi.autofism3.datastore.LocalDataStoreImpl
import me.siddheshkothadi.autofism3.network.AWSFileAPI
import me.siddheshkothadi.autofism3.network.FileAPI
import me.siddheshkothadi.autofism3.repository.FishRepository
import me.siddheshkothadi.autofism3.repository.FishRepositoryImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

const val BASE_URL = "https://file-upload-server.siddheshkothadi.repl.co/"
//const val BASE_URL = "https://autofis-server.siddheshkothadi.repl.co/"
//const val BASE_URL = "http://127.0.0.1:5000/"
const val AWS_URL = "http://20.244.36.11:8001/"

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
    fun provideAWSRetrofit(): AWSFileAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl(AWS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AWSFileAPI::class.java)
    }

    @Singleton
    @Provides
    fun providePendingUploadFishDatabase(appContext: FishApplication): PendingUploadFishDatabase {
        return Room.databaseBuilder(
            appContext,
            PendingUploadFishDatabase::class.java,
            "pending_upload_fish_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUploadHistoryFishDatabase(appContext: FishApplication): UploadHistoryFishDatabase {
        return Room.databaseBuilder(
            appContext,
            UploadHistoryFishDatabase::class.java,
            "upload_history_fish_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideLocalDataStore(appContext: FishApplication): LocalDataStore {
        return LocalDataStoreImpl(appContext)
    }

    @Singleton
    @Provides
    fun provideFishRepository(
        pendingUploadFishDatabase: PendingUploadFishDatabase,
        uploadHistoryFishDatabase: UploadHistoryFishDatabase,
        localDataStore: LocalDataStore,
        fileAPI: FileAPI,
        awsFileAPI: AWSFileAPI,
        context: FishApplication
    ): FishRepository {
        return FishRepositoryImpl(
            pendingUploadFishDatabase.fishDAO(),
            uploadHistoryFishDatabase.fishDAO(),
            localDataStore,
            fileAPI,
            awsFileAPI,
            context
        )
    }
}