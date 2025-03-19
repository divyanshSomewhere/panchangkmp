package com.gometro.base.utils.result

sealed class UseCaseResult<SUCCESS, FAILURE> {
    data class Success<SUCCESS, FAILURE>(val data: SUCCESS) : UseCaseResult<SUCCESS, FAILURE>()
    data class Failure<SUCCESS, FAILURE>(val error: FAILURE) : UseCaseResult<SUCCESS, FAILURE>()
}

data class GenericFailure(
    val message: String
)
