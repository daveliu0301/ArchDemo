package com.example.liudong.archdemo.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager


/**
 * A value holder that automatically clears the reference if the Fragment's view is destroyed.
 * @param <T>
</T> */
class AutoClearedValue<T>(fragment: Fragment, var value: T?) {
    init {
        val fragmentManager = fragment.fragmentManager
        fragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentViewDestroyed(fm: FragmentManager?, f: Fragment?) {
                        if (f === fragment) {
                            value = null
                            fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                        }
                    }
                }, false)
    }
}
