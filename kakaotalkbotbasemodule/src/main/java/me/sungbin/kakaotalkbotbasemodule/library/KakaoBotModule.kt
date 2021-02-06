/*
 * Copyright (c) 2021. Sungbin Ji. All rights reserved.
 *
 * KakaoTalkBotBaseModule license is under the MIT license.
 * SEE LICENSE: https://github.com/sungbin5304/KakaoTalkBotBaseModule/blob/master/LICENSE
 */

package me.sungbin.kakaotalkbotbasemodule.library

import android.app.Application
import android.app.Notification

class KakaoBotModule : Application() {

    companion object {
        var botListener: OnKakaoBotListener? = null
        val actions = hashMapOf<String, Notification.Action>()
        var power = true
    }
}
