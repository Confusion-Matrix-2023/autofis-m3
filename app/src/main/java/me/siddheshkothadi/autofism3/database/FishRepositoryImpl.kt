package me.siddheshkothadi.autofism3.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.siddheshkothadi.autofism3.model.Fish
import me.siddheshkothadi.autofism3.network.FileAPI
import me.siddheshkothadi.autofism3.repository.FishRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber

class FishRepositoryImpl(
    private val fishDAO: FishDAO,
    private val fileAPI: FileAPI
): FishRepository {
    override fun getAllFish(): Flow<List<Fish>> {
        return fishDAO.getAll().map {spots ->
            spots.map { spot ->
                spot.toFish()
            }
        }
    }

    override suspend fun getFishList(): List<Fish> {
        return fishDAO.getFishList().map {
            it.toFish()
        }
    }

    override suspend fun getFishByImageUri(imageUri: String): Fish {
        return fishDAO.getByImageUri(imageUri).toFish()
    }

    override suspend fun insertFish(fish: Fish) {
        fishDAO.insert(fish.toFishEntity())
    }

    override suspend fun deleteFish(fish: Fish) {
        fishDAO.delete(fish.toFishEntity())
    }

    override suspend fun deleteAllFish() {
        fishDAO.deleteAll()
    }

    override suspend fun uploadFishData(
        image: MultipartBody.Part,
        longitude: RequestBody,
        latitude: RequestBody,
        quantity: RequestBody,
        timestamp: RequestBody
    ) {
        fileAPI.uploadData(image, longitude, latitude, quantity, timestamp)
    }

    override suspend fun getHistory(): List<Fish> {
        return fileAPI.getHistory()
    }
}