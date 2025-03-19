package com.gometro.base.providers.stringprovider

import gometro.composeapp.generated.resources.Res
import gometro.composeapp.generated.resources.female
import gometro.composeapp.generated.resources.male
import gometro.composeapp.generated.resources.not_set
import gometro.composeapp.generated.resources.other
import gometro.composeapp.generated.resources.something_went_wrong
import org.jetbrains.compose.resources.StringResource

fun StringEnum.getStringRes(): StringResource {
    return when (this) {
        StringEnum.SOMETHING_WENT_WRONG -> Res.string.something_went_wrong
        StringEnum.MALE -> Res.string.male
        StringEnum.FEMALE -> Res.string.female
        StringEnum.OTHER_SEX -> Res.string.other
        StringEnum.NOT_SET -> Res.string.not_set
    }
}
