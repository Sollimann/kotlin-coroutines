import jdk.nashorn.internal.objects.Global
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

fun main(args: Array<String>) {
    //exampleBlocking()
    //exampleLaunchGlobal()
    //exampleLaunchGlobalWaiting()
    //exampleLaunchcoroutineScope()
    exampleAsyncAwait()
}

suspend fun printlnDelayed(message: String) {
    // complex computation
    delay(1000)
    println(message)
}

suspend fun calculateHardThings(startNum: Int): Int {
    delay(1000)
    return startNum * 10
}

fun exampleBlocking() {
    println("one")
    runBlocking {
        printlnDelayed("two")
    }
    println("three")
}

// Running on another thread but still blocking the main thread
fun exampleBlockingDispatcher() {
    runBlocking (Dispatchers.Default ) {
        println("one - from thread ${Thread.currentThread().name}")
        printlnDelayed("two - from thred ${Thread.currentThread().name}")
    }

    // outside of runBlocking to show that it's running in the blocked main thread
    println("three - from thread ${Thread.currentThread().name}")
    // it still runs only after the runBlocking is fully executed
}

fun exampleLaunchGlobal() = runBlocking {
    println("one - from thead ${Thread.currentThread().name}")

    GlobalScope.launch {
        printlnDelayed("two - from thred ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
    delay(3000)
}


fun exampleLaunchGlobalWaiting() = runBlocking {
    println("one - from thead ${Thread.currentThread().name}")

    val job = GlobalScope.launch {
        printlnDelayed("two - from thred ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
    job.join()
}

fun exampleLaunchcoroutineScope() = runBlocking {
    println("one - from thead ${Thread.currentThread().name}")

    val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    launch(customDispatcher) {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")

    (customDispatcher.executor as ExecutorService).shutdown()
}

fun exampleAsyncAwait() = runBlocking {

    val startTime = System.currentTimeMillis()

    val deferred1 = async { calculateHardThings(10) }
    val deferred2 = async { calculateHardThings(20) }
    val deferred3 = async { calculateHardThings(30) }

    val sum = deferred1.await() + deferred2.await() + deferred3.await()
    println("Async/await result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}