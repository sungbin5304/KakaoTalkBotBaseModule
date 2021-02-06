/*
 * Copyright (c) 2021. Sungbin Ji. All rights reserved.
 *
 * KakaoTalkBotBaseModule license is under the MIT license.
 * SEE LICENSE: https://github.com/sungbin5304/KakaoTalkBotBaseModule/blob/master/LICENSE
 */

package me.sungbin.kakaotalkbotbasemodule.library

import android.app.Notification
import android.graphics.Bitmap

interface OnKakaoBotListener {

    fun onMessageReceive(
        sender: String,
        message: String,
        room: String,
        isGroupChat: Boolean,
        action: Notification.Action,
        profileImage: Bitmap,
        packageName: String,
        bot: KakaoBot,
    )

    fun onBotCreate(bot: KakaoBot)
    fun onBotDestroy(bot: KakaoBot)
}
