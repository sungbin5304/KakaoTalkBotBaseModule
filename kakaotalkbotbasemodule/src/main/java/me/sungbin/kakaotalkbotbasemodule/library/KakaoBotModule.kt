package me.sungbin.kakaotalkbotbasemodule.library

import android.app.Application
import android.app.Notification
import android.content.Context


/**
 * Created by SungBin on 2020-10-19.
 */

class KakaoBotModule : Application() {

    companion object {
        lateinit var context: Context
        var botListener: OnKakaoBotListener? = null
        var kakaoTalkList = arrayListOf("com.kakao.talk")
        var blackRoom = arrayListOf<String>()
        var blackSender = arrayListOf<String>()
        var actions = hashMapOf<String, Notification.Action>()
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}