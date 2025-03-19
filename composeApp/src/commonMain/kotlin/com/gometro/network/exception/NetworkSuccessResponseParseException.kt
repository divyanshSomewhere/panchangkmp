package com.gometro.network.exception

class NetworkSuccessResponseParseException(private val errorMessages: String) : Exception(errorMessages)