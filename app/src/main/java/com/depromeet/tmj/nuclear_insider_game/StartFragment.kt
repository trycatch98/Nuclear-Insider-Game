package com.depromeet.tmj.nuclear_insider_game


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.depromeet.tmj.nuclear_insider_game.shared.BaseFragment
import com.depromeet.tmj.nuclear_insider_game.shared.THROTTLE_TIME
import com.depromeet.tmj.nuclear_insider_game.util.replace
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import kotlinx.android.synthetic.main.fragment_start.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class StartFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        compositeDisposable.add(et_nick_name.textChanges()
                .map { text -> text.length >= 0 }
                .subscribe { btn_start.isEnabled = it })

        compositeDisposable.add(btn_start.clicks()
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (validate(et_nick_name.text.toString())) {
                        goToGameFragment(et_nick_name.text.toString())
                    } else {
                        Toast.makeText(context, "올바르지 않은 닉네임 형태입니다.", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun validate(nickname: String): Boolean {
        val validator = "^[가-힣a-zA-Z]+\$"

        return nickname.length <= 12 && Pattern.matches(validator, nickname)
    }

    private fun goToGameFragment(name: String) {
        fragmentManager?.let { fragmentManager ->
            replace(fragmentManager, R.id.container,
                    GameFragment.newInstance(name), GameFragment::class.java.simpleName)
        }
    }
}
