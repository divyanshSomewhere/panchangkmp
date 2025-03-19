package com.gometro.network.rest.request

enum class PriorityLevel(val value: Int) {
    PRIORITY_TYPE_LOW(1), PRIORITY_TYPE_NORMAL(2), PRIORITY_TYPE_HIGH(3);

    companion object {
        fun valueOf(state: Int): PriorityLevel {
            for (policy in values()) {
                if (policy.value == state) {
                    return policy
                }
            }
            return PRIORITY_TYPE_NORMAL
        }
    }
}
