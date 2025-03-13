package com.gometro.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

@Suppress("LeakingThis")
abstract class GometroMviViewModel<ViewIntent, DataState: Any, ViewState: Any, ViewSideEffect: Any> :
    ViewModel() {

    protected var initialIntentHandled = false

    private val _initialDataState = initialDataState()

    private val _dataState = MutableStateFlow(_initialDataState)
    protected val dataState = _dataState.asStateFlow()
    private val _sideEffects = Channel<ViewSideEffect>(Channel.BUFFERED)

    @FlowInterop.Enabled val viewState: StateFlow<ViewState> by lazy {
        dataState
            .map { convertToUiState(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialViewState())
    }

    @FlowInterop.Enabled val sideEffects = _sideEffects.receiveAsFlow()

    private val _coroutineScopeState: MutableStateFlow<CoroutineScope?> = MutableStateFlow(null)
    @FlowInterop.Enabled private val coroutineScopeState = _coroutineScopeState.asStateFlow()

    fun launchInScopeWithViewLifecycle(coroutineContext: CoroutineContext = Dispatchers.Default, jobWhenLaunched: ((Job) -> Unit)? = null, block: suspend () -> Unit) {
        viewModelScope.launch {
            val coroutineScope = coroutineScopeState.value ?: coroutineScopeState.filterNotNull().first()
            val job = coroutineScope.launch(coroutineContext) {
                block.invoke()
            }
            jobWhenLaunched?.invoke(job)
        }
    }

    abstract fun initialDataState(): DataState

    private fun initialViewState(): ViewState {
        return runBlocking { convertToUiState(_initialDataState) }
    }

    protected fun printViewStateChanges(tag: String = this::class.simpleName ?: "ChaloBaseSimpleMviViewModel") {
//        viewModelScope.launch {
//            viewState.collect {
//                ChaloLog.debug(tag, "$it")
//            }
//        }
    }

    @CallSuper
    open fun processIntent(intent: ViewIntent) {
//        ChaloLog.info("ChaloBaseSimpleMviViewModel", "ViewIntent = $intent")
    }

    protected fun emitSideEffect(sideEffect: ViewSideEffect) {
        viewModelScope.launch {
            _sideEffects.send(sideEffect)
        }
    }

    protected fun updateState(block: (currentState: DataState) -> DataState) {
        _dataState.update(block)
    }

    abstract suspend fun convertToUiState(dataState: DataState): ViewState

    protected suspend fun <T> Flow<T>.collectWhenViewStateActive(collector: suspend (T) -> Unit) {
        combine(
            flow = viewStateActiveStatusFlow(),
            flow2 = this
        ) { isViewStateCollectionActive, flowResult ->
            if (isViewStateCollectionActive) {
                ViewStateCollectionResult.Data(flowResult)
            } else {
                ViewStateCollectionResult.ScreenNotActive
            }
        }
            .distinctUntilChanged()
            .filterIsInstance<ViewStateCollectionResult.Data<T>>()
            .map { it.data }
            .collect(collector)
    }

    protected fun viewStateActiveStatusFlow(): Flow<Boolean> {
        return _dataState.subscriptionCount
            .map { it >= 1 }
            .distinctUntilChanged()
    }

    /**
     * Calling this method will start calling `onViewStateActive` and `onViewStateInactive`
     * whenever the view state is active or inactive.
     */
    protected fun registerForViewStateChanges() = viewModelScope.launch{
        _dataState.subscriptionCount
            .map { it >= 1 }
            .distinctUntilChanged()
            .collect { isActive ->
                if (isActive) {
                    onViewStateActive()
                } else {
                    onViewStateInactive()
                }
            }
    }

    protected open fun onViewStateActive() {}

    protected open fun onViewStateInactive() {}

    fun initializeCoroutineScopeWhileVsCollected() {
        viewModelScope.launch {
            viewStateActiveStatusFlow()
                .collectLatest { shouldCreateScope ->
                    if(shouldCreateScope) {
                        _coroutineScopeState.update {
                            CoroutineScope(Dispatchers.Default)
                        }
                    } else {
                        _coroutineScopeState.value?.cancel()
                        _coroutineScopeState.update {
                            null
                        }
                    }
                }
        }
    }
}


sealed class ViewStateCollectionResult<T> {
    data object ScreenNotActive : ViewStateCollectionResult<Nothing>()
    data class Data<T>(val data: T) : ViewStateCollectionResult<T>()
}
