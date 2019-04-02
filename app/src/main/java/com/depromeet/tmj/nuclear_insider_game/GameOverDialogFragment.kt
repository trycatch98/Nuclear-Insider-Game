package com.depromeet.tmj.nuclear_insider_game

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit


class GameOverDialogFragment : DialogFragment() {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var listener: Listener


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            RuntimeException("GmaeOverDialogFragment Listener is not implemented")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game_over_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.clear()
    }

    fun setListener(listener: () -> Unit) {
        this.listener = object : Listener {
            override fun onDismiss() {
                listener()
            }
        }
    }

    private fun initUi() {
        compositeDisposable.add(Observable.timer(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dismissAllowingStateLoss()
                    listener.onDismiss()
                })
    }

    interface Listener {
        fun onDismiss()
    }
}
