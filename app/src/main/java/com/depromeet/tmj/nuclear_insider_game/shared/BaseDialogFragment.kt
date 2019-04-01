package com.depromeet.tmj.nuclear_insider_game.shared

import androidx.fragment.app.DialogFragment
import io.reactivex.disposables.CompositeDisposable

open class BaseDialogFragment: DialogFragment() {
    val compositeDisposable = CompositeDisposable()

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.clear()
    }
}