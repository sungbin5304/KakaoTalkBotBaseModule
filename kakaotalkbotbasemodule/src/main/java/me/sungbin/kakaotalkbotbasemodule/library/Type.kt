/*
 * Copyright (c) 2021. Sungbin Ji. All rights reserved.
 *
 * KakaoTalkBotBaseModule license is under the MIT license.
 * SEE LICENSE: https://github.com/sungbin5304/KakaoTalkBotBaseModule/blob/master/LICENSE
 */

package me.sungbin.kakaotalkbotbasemodule.library

sealed class Type {

    object PACKAGE : Type()
    object ROOM : Type()
    object SENDER : Type()
}
