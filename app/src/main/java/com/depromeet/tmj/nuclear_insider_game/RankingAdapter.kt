package com.depromeet.tmj.nuclear_insider_game

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.depromeet.tmj.nuclear_insider_game.model.ScoreModel
import kotlinx.android.synthetic.main.viewholder_ranking.view.*

class RankingAdapter(val context: Context, val data: List<ScoreModel>)
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
        private val tvRank = view.tv_rank
        private val tvNickname = view.tv_nick_name
        private val tvScore = view.tv_answer

        fun bind(data: ScoreModel, position: Int) {
            tvRank.text = (position + 1).toString()
            tvNickname.text = data.nickName
            tvScore.text = data.answerNum.toString()
        }
    }
}