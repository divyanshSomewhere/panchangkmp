package com.gometro.login.constants
//
//import android.content.Intent
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.DefaultLifecycleObserver
//import androidx.lifecycle.LifecycleOwner
//import app.chalo.login.data.models.app.TruecallerSetupConfig
//import app.zophop.errorreporting.ErrorReporterContract
//import com.truecaller.android.sdk.*
//import java.lang.ref.WeakReference
//import java.util.concurrent.atomic.AtomicBoolean
//
//class TruecallerSetupHandler(
//    private val fragmentRef: WeakReference<Fragment>,
//    private val errorReporterContract: ErrorReporterContract
//) : DefaultLifecycleObserver {
//
//    init {
//        fragmentRef.get()?.lifecycle?.addObserver(this)
//    }
//
//    private val isTruecallerSdkInitialized = AtomicBoolean(false)
//
//    fun initTruecaller(
//        setupConfig: TruecallerSetupConfig,
//        onSuccess: (TrueProfile) -> Unit,
//        onError: (TrueError) -> Unit
//    ) {
//        val callback = object : ITrueCallback {
//            override fun onSuccessProfileShared(trueProfile: TrueProfile) {
//                onSuccess.invoke(trueProfile)
//            }
//
//            override fun onFailureProfileShared(trueError: TrueError) {
//                onError.invoke(trueError)
//            }
//
//            override fun onVerificationRequired(trueError: TrueError?) {
//                val error = trueError ?: TrueError(TrueError.ERROR_TYPE_CONTINUE_WITH_DIFFERENT_NUMBER)
//                onError.invoke(error)
//            }
//        }
//        fragmentRef.get()?.let {
//            val trueScope = TruecallerSdkScope.Builder(it.requireContext(), callback)
//                .consentMode(setupConfig.consentMode)
//                .buttonColor(setupConfig.buttonColor)
//                .buttonShapeOptions(setupConfig.buttonShapeOptions)
//                .consentTitleOption(setupConfig.consentTitleOption)
//                .footerType(setupConfig.footerType)
//                .loginTextPrefix(setupConfig.loginTextPrefix)
//                .loginTextSuffix(setupConfig.loginTextSuffix)
//                .sdkOptions(setupConfig.sdkOptions)
//                .build()
//
//            TruecallerSDK.init(trueScope)
//            isTruecallerSdkInitialized.set(true)
//        }
//    }
//
//    fun isTruecallerUsable(): Boolean {
//        return if (isTruecallerSdkInitialized.get()) {
//            try {
//                TruecallerSDK.getInstance().isUsable
//            } catch (e: Exception) {
//                errorReporterContract.reportHandledException(e.cause)
//                false
//            }
//        } else {
//            false
//        }
//    }
//
//    fun showTruecallerLoginOption(uid: String) {
//        fragmentRef.get()?.let {
//            if (isTruecallerSdkInitialized.get()) {
//                try {
//                    TruecallerSDK.getInstance().apply {
//                        setRequestNonce(uid)
//                        getUserProfile(it)
//                    }
//                } catch (e: Exception) {
//                    errorReporterContract.reportHandledException(e.cause)
//                }
//            }
//        }
//    }
//
//    fun onActivityResultObtained(requestCode: Int, resultCode: Int, data: Intent?) {
//        fragmentRef.get()?.let {
//            if (isTruecallerSdkInitialized.get()) {
//                try {
//                    TruecallerSDK.getInstance().onActivityResultObtained(it.requireActivity(), requestCode, resultCode, data)
//                } catch (e: Exception) {
//                    errorReporterContract.reportHandledException(e.cause)
//                }
//            }
//        }
//    }
//
//    override fun onDestroy(owner: LifecycleOwner) {
//        super.onDestroy(owner)
//        isTruecallerSdkInitialized.set(false)
//        TruecallerSDK.clear()
//        fragmentRef.clear()
//    }
//}
