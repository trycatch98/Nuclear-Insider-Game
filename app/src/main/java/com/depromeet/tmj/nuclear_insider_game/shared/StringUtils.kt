package com.depromeet.tmj.nuclear_insider_game.shared

class StringUtils {
    companion object {
        fun unicodeToEmoji(unicode: String): String {
            return String(Character.toChars(Integer.parseInt(unicode,16)))
        }

        fun removeSpace(string: String): String {
            return string.replace(" ", "")
        }
    }
}