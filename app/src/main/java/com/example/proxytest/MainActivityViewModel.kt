package com.example.proxytest

import androidx.lifecycle.ViewModel
import com.example.result_api.ResultApi

class MainActivityViewModel : ViewModel() {

    val resultApiUsingYield: ResultApi by lazy {
        PluginProviderImpl.proxyPluginApi(ResultApi::class.java, true)
    }

    val resultApiWithoutYield: ResultApi by lazy {
        PluginProviderImpl.proxyPluginApi(ResultApi::class.java, false)
    }

    suspend fun getImages(): Result<String> {
        return resultApiWithoutYield.getImages("https://picsum.photos/id/1/200/300")
    }

    suspend fun getImagesContext(): Result<String> {
        return resultApiWithoutYield.getImagesWithContextSwitch("https://picsum.photos/id/1/200/300")
    }

    suspend fun getImagesDelay(): Result<String> {
        return resultApiWithoutYield.getImagesWithDelay("https://picsum.photos/id/1/200/300")
    }

    suspend fun getImagesFull(): Result<String> {
        return resultApiWithoutYield.getImagesFull("https://picsum.photos/id/1/200/300")
    }

    suspend fun getImagesUsingYield(): Result<String> {
        return resultApiUsingYield.getImages("https://picsum.photos/id/1/200/300")
    }

    suspend fun getImagesContextUsingYield(): Result<String> {
        return resultApiUsingYield.getImagesWithContextSwitch("https://picsum.photos/id/1/200/300")
    }

    suspend fun getImagesDelayUsingYield(): Result<String> {
        return resultApiUsingYield.getImagesWithDelay("https://picsum.photos/id/1/200/300")
    }

    suspend fun getImagesFullUsingYield(): Result<String> {
        return resultApiUsingYield.getImagesFull("https://picsum.photos/id/1/200/300")
    }
}