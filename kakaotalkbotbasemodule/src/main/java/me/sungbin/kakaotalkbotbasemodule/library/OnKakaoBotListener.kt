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
        bot: KakaoBot
    )

    fun onBotCreate(bot: KakaoBot)
    fun onBotDestroy(bot: KakaoBot)
}
