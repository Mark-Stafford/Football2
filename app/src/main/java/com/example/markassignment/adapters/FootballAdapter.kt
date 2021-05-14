package com.example.markassignment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.markassignment.R
import com.example.markassignment.models.FootballModel
import kotlinx.android.synthetic.main.card_addfball.view.*
import kotlinx.android.synthetic.main.fragment_addfball.view.*


interface FootballListener {
    fun onFootballClick(football: FootballModel)
}

class FootballAdapter constructor(var balls: ArrayList<FootballModel>,
                                   private val listener: FootballListener)
    : RecyclerView.Adapter<FootballAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent?.context).inflate(
                R.layout.card_addfball,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val ball = balls[holder.adapterPosition]
        holder.bind(ball,listener)
    }

    override fun getItemCount(): Int = balls.size

    fun removeAt(position: Int) {
        balls.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(football: FootballModel, listener: FootballListener) {

            itemView.tag = football
            itemView.bName.text = football.ballname
            itemView.bDescription.text = football.balldescription
            itemView.bCountry.text = football.ballcountry
            itemView.setOnClickListener { listener.onFootballClick(football) }
        }
    }
}