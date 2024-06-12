package com.github.aivanovski.picoautomator.android.data.api

object Api {
    private const val SERVER_URL = "http://10.0.2.2:8080"
    private const val LOGIN = "login"
    private const val FLOW = "flow"

    fun buildGetFlowUrl(flowUid: String): String {
        return "$SERVER_URL/$FLOW/$flowUid"
    }

    fun buildLoginUrl(): String {
        return "$SERVER_URL/$LOGIN"
    }
}