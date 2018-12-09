package com.depromeet.tmj.nuclear_insider_game.shared

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable

open class BaseFragment : Fragment() {
    val compositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}