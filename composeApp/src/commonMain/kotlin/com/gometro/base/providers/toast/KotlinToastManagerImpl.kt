package com.gometro.base.providers.toast

import co.touchlab.skie.configuration.annotations.FlowInterop
import com.gometro.base.featurecontracts.KotlinToastData
import com.gometro.base.featurecontracts.KotlinToastManager
import com.gometro.base.featurecontracts.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class KotlinToastManagerImpl (
    coroutineContextProvider: CoroutineContextProvider
) : KotlinToastManager {

    private val _msgStream = MutableSharedFlow<KotlinToastData>()
    @FlowInterop.Enabled
    override val msgStream: SharedFlow<KotlinToastData> = _msgStream.asSharedFlow()

    private val scope = CoroutineScope(coroutineContextProvider.main)

    override fun postToastRequest(request: KotlinToastData) {
        scope.launch {
            _msgStream.emit(request)
        }
    }
}