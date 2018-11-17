package com.depromeet.tmj.nuclear_insider_game

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class ClearDialogFragment : DialogFragment() {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var listener: Listener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement Listener")
        }
    }

    override fun onStart() {
        super.onStart()
        // 팝업창 배경 투명색 설정 및 전체팝업 설정
        val dialog = dialog
        context?.let { context ->
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val bgDialog = ColorDrawable(ContextCompat.getColor(context, R.color.clear_background))

            if (dialog.window != null) {
                dialog.window!!.setLayout(width, height)
                bgDialog.alpha = 80
                dialog.window!!.setBackgroundDrawable(bgDialog)
                dialog.setOnKeyListener { _, _, _ -> true }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_clear_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.clear()
    }

    private fun initUi() {
        compositeDisposable.add(Observable.timer(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
<<<<<<< HEAD
                    (activity as MainActivity).showRanking()
=======
>>>>>>> parent of f80809d... 화면 전환
                    listener.onDismiss()
                })
    }
}
