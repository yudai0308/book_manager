package io.github.yudai0308.honma

import okhttp3.mockwebserver.MockResponse
import java.io.BufferedReader
import java.io.InputStreamReader

fun MockResponse.setBodyFromFileName(name: String): MockResponse {
    val inputStream = javaClass.classLoader?.getResourceAsStream(name)
    val bufferReader = BufferedReader(InputStreamReader(inputStream))
    val builder = StringBuilder()
    bufferReader.forEachLine { builder.append(it) }
    val body = builder.toString()
    this.setBody(body)
    return this
}