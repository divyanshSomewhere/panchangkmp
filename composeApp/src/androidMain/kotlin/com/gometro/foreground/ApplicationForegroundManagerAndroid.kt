package com.gometro.foreground

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

class ApplicationForegroundManagerAndroid (
    context: Context
) : ApplicationForegroundManager {

    override var isInForeground = false
        private set

    private val listeners = mutableListOf<ForegroundListener>()
        get() {
            return synchronized(this) {
                field
            }
        }

    init {
        (context.applicationContext as? Application)?.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {

                override fun onActivityResumed(activity: Activity) {
                    val wasPreviouslyInForeground = isInForeground
                    isInForeground = true
                    if (wasPreviouslyInForeground.not()) {
                        // was not in foreground earlier and now it is, inform listeners
                        val currentListeners = listeners.toList()
                        currentListeners.forEach {
                            it.onBecameForeground()
                        }
                    }
                }

                override fun onActivityPaused(activity: Activity) {
                    val wasPreviouslyInForeground = isInForeground
                    isInForeground = false

                    if (wasPreviouslyInForeground) {
                        // was previously in foreground and now in background, inform listeners
                        val currentListeners = listeners.toList()
                        currentListeners.forEach {
                            it.onBecameBackground()
                        }
                    }
                }

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

                override fun onActivityStarted(activity: Activity) {}

                override fun onActivityStopped(activity: Activity) {}

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

                override fun onActivityDestroyed(activity: Activity) {}
            }
        )
    }

    override fun addListener(listener: ForegroundListener) {
        synchronized(this) {
            listeners.add(listener)
        }
    }

    override fun removeListener(listener: ForegroundListener) {
        synchronized(this) {
            listeners.remove(listener)
        }
    }
}