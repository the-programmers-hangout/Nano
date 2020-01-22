package me.elliott.nano.util

fun <T : Any> T?.notNull(f: (it: T) -> Unit) {
    if (this != null) f(this)
}