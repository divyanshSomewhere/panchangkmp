package com.gometro.base.providers.stringprovider

import org.jetbrains.compose.resources.getString


class StringProviderImpl : StringProvider {
    override suspend fun getString(stringEnum: StringEnum, vararg formatArgs: Any): String {
        val stringRes = stringEnum.getStringRes()
        return getString(stringRes, *formatArgs)
    }
}