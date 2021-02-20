package com.ferelin.shared

open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {

    private var mCreator: ((A) -> T)? = creator

    @Volatile
    private var mInstance: T? = null

    fun getInstance(arg: A): T {
        val i = mInstance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = mInstance
            if (i2 != null) {
                i2
            } else {
                val created = mCreator!!(arg)
                mInstance = created
                mCreator = null
                created
            }
        }
    }
}