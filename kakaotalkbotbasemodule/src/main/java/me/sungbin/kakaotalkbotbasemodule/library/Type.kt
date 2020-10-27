package me.sungbin.kakaotalkbotbasemodule.library

/**
 * Created by SungBin on 2020-10-19.
 */

sealed class Type {
    object ROOM : Type()
    object SENDER : Type()
}
