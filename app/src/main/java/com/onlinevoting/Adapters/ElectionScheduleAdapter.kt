package com.sunayanpradhan.onlinevoting.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sunayanpradhan.onlinevoting.Activities.VoteActivity
import com.sunayanpradhan.onlinevoting.Activities.VoteCountActivity
import com.sunayanpradhan.onlinevoting.Models.VoteInformation
import com.sunayanpradhan.onlinevoting.R
import java.text.SimpleDateFormat

class ElectionScheduleAdapter(var list: ArrayList<VoteInformation>,var context: Context):RecyclerView.Adapter<ElectionScheduleAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val electionLogo: ImageView = itemView.findViewById(R.id.election_logo)
        val electionName: TextView = itemView.findViewById(R.id.election_name)
        val electionTime: TextView = itemView.findViewById(R.id.election_time)
        val electionResultTime:TextView=itemView.findViewById(R.id.election_result_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.vote_schedule_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var currentItem=list[position]

        Glide.with(context).load(currentItem.voteLogo).into(holder.electionLogo)

        holder.electionName.text=currentItem.voteTitle

        holder.electionTime.text= SimpleDateFormat.getInstance().format(currentItem?.voteStartTime).toString() +"-"+SimpleDateFormat.getInstance().format(currentItem?.voteEndTime).toString()

        holder.electionResultTime.text="Result Date: "+SimpleDateFormat.getInstance().format(currentItem?.resultDate).toString()



    }

    override fun getItemCount(): Int {
        return list.size
    }

}
