package com.example.result_api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

class ResultApiImpl() : ResultApi {

    /**
     * Directly returns a Result.success with a hardcoded image URL.
     */
    override suspend fun getImages(url: String): Result<String> {
        return Result.success(url)
    }

    /**
     * After a 2 seconds delay, returns a Result.success with a hardcoded image URL.
     */
    override suspend fun getImagesWithDelay(url: String): Result<String> {
        return try {
            // Simulate the delay of a network call to fetch images
            delay(2000L)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Switches coroutines context to IO and returns a Result.success with a hardcoded image URL.
     */
    override suspend fun getImagesWithContextSwitch(url: String): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                Result.success(url)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Switches coroutines context to IO and after a 2 seconds delay returns a Result.success with a
     * hardcoded image URL.
     */
    override suspend fun getImagesFull(url: String): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                // Simulate the delay of a network call to fetch images
                delay(2000L)
                Result.success(url)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}