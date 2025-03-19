package com.gometro.base.providers.stringprovider

interface StringProvider {
    suspend fun getString(stringEnum: StringEnum, vararg formatArgs: Any): String
}
