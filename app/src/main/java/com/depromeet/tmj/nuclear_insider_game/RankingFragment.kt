package com.depromeet.tmj.nuclear_insider_game

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class RankingFragment : Fragment() {
    private lateinit var listener: Listner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listner) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement Listner")
        }
    }

    override fun onDetach() {
        super.onDetach()
    }


    interface Listner {
    }

}
