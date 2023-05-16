package com.sunayanpradhan.onlinevoting.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.onlinevoting.Activities.VoteCountActivity.Companion.voteId
import com.sunayanpradhan.onlinevoting.Models.TeamInformation
import com.sunayanpradhan.onlinevoting.Models.VoteResponseInformation
import com.sunayanpradhan.onlinevoting.R

class VoteCountAdapter(var list:ArrayList<TeamInformation>,var context: Context):RecyclerView.Adapter<VoteCountAdapter.ViewHolder>() {

    lateinit var voteCountList:ArrayList<VoteResponseInformation>

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        val layoutCard: CardView =itemView.findViewById(R.id.layout_card)
        val teamLogo: ImageView =itemView.findViewById(R.id.team_logo)
        val teamName: TextView =itemView.findViewById(R.id.team_name)
        val teamVote: TextView =itemView.findViewById(R.id.team_vote)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.vote_count_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem=list[position]

        holder.layoutCard.setCardBackgroundColor(Color.parseColor(currentItem.teamColor))

        Glide.with(context).load(currentItem.teamLogo).into(holder.teamLogo)

        holder.teamName.text=currentItem.teamName

        voteCountList=ArrayList()

        FirebaseDatabase.getInstance().reference.child("VoteResponse").child(voteId).child("TeamsVoteCount")
            .child(currentItem.teamId).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    voteCountList.clear()

                    for (dataSnapshot in snapshot.children){

                        var data:VoteResponseInformation?=dataSnapshot.getValue(VoteResponseInformation::class.java)

                        data?.voterId=dataSnapshot.key.toString()

                        voteCountList.add(data!!)


                    }

                    holder.teamVote.text=voteCountList.size.toString()


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }


            })

    }

    override fun getItemCount(): Int {
        return list.size
    }
}