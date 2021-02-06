/*
 * Copyright (c) 2021. Sungbin Ji. All rights reserved.
 *
 * KakaoTalkBotBaseModule license is under the MIT license.
 * SEE LICENSE: https://github.com/sungbin5304/KakaoTalkBotBaseModule/blob/master/LICENSE
 */

package me.sungbin.kakaotalkbotbasemodule.library

import android.content.Context

internal object DataUtil {

    fun read(context: Context, type: Type) =
        context.getSharedPreferences("pref", Context.MODE_PRIVATE).getString(
            when (type) {
                Type.ROOM -> "room"
                Type.SENDER -> "sender"
                Type.PACKAGE -> "package"
            },
            ""
        )!!

    fun remove(context: Context, type: Type, value: String) {
        save(context, type, read(context, type).replace("\n$value", ""))
    }

    fun save(context: Context, type: Type, value: String) {
        val preData = read(context, type)
        if (!preData.contains(value)) {
            context.getSharedPreferences("pref", Context.MODE_PRIVATE).edit().apply {
                putString(
                    when (type) {
                        Type.ROOM -> "room"
                        Type.SENDER -> "sender"
                        Type.PACKAGE -> "package"
                    },
                    "$preData\n$value"
                )
            }.apply()
        }
    }

    fun clear(context: Context) {
        context.getSharedPreferences("pref", Context.MODE_PRIVATE).edit().apply {
            clear()
        }.apply()
    }
}
