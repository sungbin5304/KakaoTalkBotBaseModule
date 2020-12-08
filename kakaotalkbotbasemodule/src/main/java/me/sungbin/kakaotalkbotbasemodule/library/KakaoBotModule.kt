package me.sungbin.kakaotalkbotbasemodule.library

import android.app.Application
import android.app.Notification

/**
 * Created by SungBin on 2020-10-19.
 */

class KakaoBotModule : Application() {

    companion object {
        var botListener: OnKakaoBotListener? = null
        var kakaoTalkList = arrayListOf("com.kakao.talk")
        var blackRoom = ArrayList<String>()
        var blackSender = ArrayList<String>()
        var actions = HashMap<String, Notification.Action>()
        var power = true
    }

    override fun onCreate() {
        super.onCreate()
    }
}
