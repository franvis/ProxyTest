package com.example.proxytest

import com.example.result_api.PluginApi
import com.example.result_api.ResultApiImpl
import kotlinx.coroutines.yield
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

object PluginProviderImpl {

    fun <T: PluginApi> getInvocationHandlerWithYield(type: Class<T>, useYield: Boolean): InvocationHandler =
        object : InvocationHandler {
            override fun invoke(
                proxy: Any,
                method: Method,
                args: Array<Any>
            ): Any? {
                debugLog(method, type, args)

                @Suppress("TooGenericExceptionCaught")
                return invokeSuspendFunction(args.getContinuation()) {
                    val processedSuspendFunResult = try {
                        val pluginApi = ResultApiImpl()
                        // Remove the continuation, this is already implicitly passed down through the
                        // suspending kotlinFunction.
                        val functionArgs = args.dropLast(1)
                        val returnValue = handleInvocationTargetException {
                            method.kotlinFunction?.callSuspend(
                                pluginApi,
                                *functionArgs.toTypedArray()
                            )
                        }
                        // A lot of our suspend functions return a Result type. We intercept those and
                        // run our processing (handleEvent) on both the success and failure cases.
                        if (returnValue is Result<*>) {
                            returnValue
                                .onSuccess { handleEvent(null) }
                                .recoverCatching { handleCoroutinesException(it) }
                        } else {
                            // If it's not a Result type, reaching this point means the call was
                            // successful, otherwise it would've thrown an exception and we'd end up in
                            // the catch block.
                            handleEvent(null)
                            returnValue
                        }
                    } catch (e: Exception) {
                        handleCoroutinesException(e)
                    }

                    if(useYield) {
                        // After we are done processing there's no need to take the current thread anymore
                        // so we call yield() for other operations to take place.
                        // Before doing this we had cases in production having malformed return types.
                        // For example, when using the PluginApi and expecting a Result<String> we were
                        // getting a Result<Result<String>>.
                        //
                        // Calling yield() here makes sure that the returned type is always
                        // COROUTINE_SUSPENDED so that the coroutines framework can handle it correctly.
                        yield()
                    }
                    processedSuspendFunResult
                }
            }
        }


    @Suppress("UNCHECKED_CAST", "LongMethod")
    fun <T : PluginApi> proxyPluginApi(type: Class<T>, useYield: Boolean): T {
        return Proxy.newProxyInstance(
            type.classLoader,
            arrayOf(type),
            getInvocationHandlerWithYield(type, useYield)
        ) as T
    }

    private fun handleEvent(throwable: Throwable?) {
        if (throwable == null) {
            println("Handling Result type for a successful call")
        } else {
            println("Handling Result type when an exception has been thrown. $throwable")
        }
    }

    fun <T : PluginApi> debugLog(method: Method, type: Class<T>, args: Array<Any>) {
        val sanitizedStringArgs = args?.joinToString(",")
        println(
            "Invoking method ${method.name} on ${type.canonicalName} with args $sanitizedStringArgs"
        )
    }

    private fun invokeSuspendFunction(
        continuation: Continuation<*>,
        suspendFunction: suspend () -> Any?,
    ): Any? = handleInvocationTargetException {
        @Suppress("UNCHECKED_CAST") (suspendFunction as (Continuation<*>) -> Any?)(continuation)
    }

    private inline fun handleInvocationTargetException(action: () -> Any?): Any? = try {
        action()
    } catch (e: InvocationTargetException) {
        throw requireNotNull(e.cause) { "Invocation target exception always has a cause" }
    }

    /**
     * Coroutines called through a [Proxy] require checked exceptions to be declared.
     * This is a workaround taken from
     * [this article](https://jakewharton.com/exceptions-and-proxies-and-coroutines-oh-my/).
     */
    private suspend fun Throwable.yieldAndThrow(): Nothing {
        yield()
        throw this
    }

    /**
     * Handle exceptions that are thrown from coroutines. This should be done both for calls
     * throwing exceptions and calls returning a [Result].
     */
    private suspend fun handleCoroutinesException(e: Throwable) {
        handleEvent(e)
        e.yieldAndThrow()
    }
}

fun Array<Any>.getContinuation() = last() as Continuation<*>
