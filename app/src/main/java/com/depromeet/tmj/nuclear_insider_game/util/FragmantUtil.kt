package com.depromeet.tmj.nuclear_insider_game.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun replace(fragmentManager: FragmentManager, container: Int,
            newFragment: Fragment, tag: String) {
    fragmentManager.beginTransaction()
            .replace(container, newFragment, tag)
            .commitAllowingStateLoss()
}