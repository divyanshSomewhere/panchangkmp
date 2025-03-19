package com.gometro.network.exception

class NetworkErrorResponseParseException(private val errorMessage: String) : Exception(errorMessage)
