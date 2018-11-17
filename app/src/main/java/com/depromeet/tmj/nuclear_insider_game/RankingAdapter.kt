package com.depromeet.tmj.nuclear_insider_game

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.viewholder_ranking.view.*

class RankingAdapter(val context: Context, val data: List<RankingDataModel.Score>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_ranking, parent, false)
        return RankingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        if(viewholder is RankingViewHolder) {
            viewholder.bind(data[position], position)
        }
    }

    inner class RankingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRankg = view.tv_rank
        val tvNickname = view.tv_nick_name
        val tvScore = view.tv_answer

        fun bind(data: RankingDataModel.Score, position: Int) {
            tvRankg.text = (position + 1).toString()
            tvNickname.text = data.nickName
            tvScore.text = data.answerNum.toString()
        }
    }
}