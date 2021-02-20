package com.ferelin.shared

open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {

    private var mCreator: ((A) -> T)? = creator

    @Volatile
    private var mInstance: T? = null

    fun getInstance(arg: A): T {
        return mInstance ?: run {
            synchronized(this) {
                mInstance ?: run {
                    mCreator!!(arg).also {
                        mInstance = it
                        mCreator = null
                    }
                }
            }
        }
    }
}