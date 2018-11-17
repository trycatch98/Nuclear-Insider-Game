package com.depromeet.tmj.nuclear_insider_game


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_start.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class StartFragment : Fragment() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
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
        compositeDisposable.add(et_nick_name.textChanges()
                .map { text -> text.length >= 0 }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { btn_start.isEnabled = it })

        compositeDisposable.add(btn_start.clicks()
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(validate(et_nick_name.text.toString())) {
                        // TODO: 게임 시작으로 이동
                    } else {
                        Toast.makeText(context, "올바르지 않은 닉네임 형태입니다.", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun validate(nickname: String): Boolean {
        val validator = "/^[가-힣a-zA-Z]+\$/;"

        return nickname.length <= 12 && Pattern.matches(validator, nickname)
    }
}
