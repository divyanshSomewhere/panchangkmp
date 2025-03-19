package com.gometro.userprofile.data.models

import co.touchlab.skie.configuration.annotations.EnumInterop
import com.gometro.base.providers.stringprovider.StringEnum
import kotlinx.serialization.Serializable

@Serializable
@EnumInterop.Enabled
enum class Gender(private val _value: String) {
    MALE("male"),
    FEMALE("female"),
    OTHER("other"),
    NULL("");

    override fun toString(): String {
        return _value
    }

    companion object {
        fun fromString(genderString: String?): Gender {
            if (genderString.isNullOrEmpty()) {
                return MALE
            }
            return values().firstOrNull {
                it._value.equals(genderString, true) ||
                        it.name.equals(genderString, true)
            } ?: MALE
        }
    }
}

val Gender.stringEnum: StringEnum
    get() {
        return when(this) {
            Gender.MALE -> StringEnum.MALE
            Gender.FEMALE -> StringEnum.FEMALE
            Gender.OTHER -> StringEnum.OTHER_SEX
            Gender.NULL -> StringEnum.NOT_SET
        }
    }
