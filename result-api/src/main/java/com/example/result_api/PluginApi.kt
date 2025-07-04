package com.example.result_api

interface PluginApi

interface ResultApi: PluginApi {
    suspend fun getImages(url: String): Result<String>

    suspend fun getImagesWithDelay(url: String): Result<String>

    suspend fun getImagesWithContextSwitch(url: String): Result<String>

    suspend fun getImagesFull(url: String): Result<String>
}