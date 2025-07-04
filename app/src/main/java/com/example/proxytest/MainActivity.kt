package com.example.proxytest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proxytest.ui.theme.ProxyTestTheme
import kotlinx.coroutines.launch
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    suspend fun callApisAndPrintLogs() {
        val result = viewModel.getImages()
        val resultContext = viewModel.getImagesContext()
        val resultDelay = viewModel.getImagesDelay()
        val resultFull = viewModel.getImagesFull()
        Log.v("ResultApi", "Returned value for getImages is $result")
        Log.v("ResultApi", "Returned value for getImagesContext is $resultContext")
        Log.v("ResultApi", "Returned value for getImagesDelay is $resultDelay")
        Log.v("ResultApi", "Returned value for getImagesFull is $resultFull")
    }

    suspend fun callApisAndPrintLogsUsingYield() {
        val result = viewModel.getImagesUsingYield()
        val resultContext = viewModel.getImagesContextUsingYield()
        val resultDelay = viewModel.getImagesDelayUsingYield()
        val resultFull = viewModel.getImagesFullUsingYield()
        Log.v("ResultApi", "Returned value for getImagesUsingYield is $result")
        Log.v("ResultApi", "Returned value for getImagesContextUsingYield is $resultContext")
        Log.v("ResultApi", "Returned value for getImagesDelayUsingYield is $resultDelay")
        Log.v("ResultApi", "Returned value for getImagesFullUsingYield is $resultFull")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProxyTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting(
                            "Click here for a Result log",
                            { callApisAndPrintLogs() },
                        )

                        Greeting(
                            "Click here for a Result log using yield on the proxy",
                            { callApisAndPrintLogsUsingYield() },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(text: String, onClick: suspend () -> Unit, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    Button(
        modifier = modifier,
        onClick = { coroutineScope.launch { onClick.invoke() } }
    ) {
        Text(
            text = text,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProxyTestTheme { Greeting("Test text", {}) }
}