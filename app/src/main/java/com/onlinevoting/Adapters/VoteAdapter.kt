package com.sunayanpradhan.onlinevoting.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sunayanpradhan.onlinevoting.Activities.VoteActivity.Companion.voteId
import com.sunayanpradhan.onlinevoting.Activities.VoteActivity.Companion.voteSubmit
import com.sunayanpradhan.onlinevoting.Activities.VoteListActivity
import com.sunayanpradhan.onlinevoting.R
import com.sunayanpradhan.onlinevoting.Models.TeamInformation
import com.sunayanpradhan.onlinevoting.Models.VoteResponseInformation
import java.util.*
import kotlin.collections.ArrayList

class VoteAdapter(var list: ArrayList<TeamInformation>, var context: Context) : RecyclerView.Adapter<VoteAdapter.ViewHolder>(){

    var singleitem_selection_position=-1

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val layoutCard:CardView=itemView.findViewById(R.id.layout_card)
        val teamLogo:ImageView=itemView.findViewById(R.id.team_logo)
        val teamName:TextView=itemView.findViewById(R.id.team_name)
        val teamSlogan:TextView=itemView.findViewById(R.id.team_slogan)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.layout_vote,parent,false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem=list[position]

        holder.layoutCard.setCardBackgroundColor(Color.parseColor(currentItem.teamColor))

        Glide.with(context).load(currentItem.teamLogo).into(holder.teamLogo)

        holder.teamName.text=currentItem.teamName

        holder.teamSlogan.text=currentItem.teamSlogan


        if (singleitem_selection_position==position){
//            holder.itemView.setBackgroundColor(context.getColor(com.google.android.material.R.color.m3_dynamic_highlighted_text))

            holder.itemView.setBackgroundColor(context.getColor(R.color.grey))


        }
        else{
            holder.itemView.setBackgroundColor(context.getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
        }

        holder.itemView.setOnClickListener {

            setSingleSelection(position)

        }



    }

    override fun getItemCount(): Int {

        return list.size

    }

    private fun setSingleSelection(adapterPosition:Int){

        if (adapterPosition==RecyclerView.NO_POSITION){

            voteSubmit.visibility=View.GONE

            return
        }

        else{
            notifyItemChanged(singleitem_selection_position)

            singleitem_selection_position=adapterPosition

            notifyItemChanged(singleitem_selection_position)

            voteSubmit.visibility=View.VISIBLE

            voteSubmit.setOnClickListener {

                var vrInformation=VoteResponseInformation()

                vrInformation.voterId=FirebaseAuth.getInstance().uid.toString()
                vrInformation.voteTeam=list[adapterPosition].teamId
                vrInformation.voteTime=Date().time

                FirebaseDatabase.getInstance().reference.child("VoteResponse")
                    .child(voteId)
                    .child("TeamsVoteCount")
                    .child(vrInformation.voteTeam).child(FirebaseAuth.getInstance().uid.toString()).setValue(vrInformation)
                    .addOnSuccessListener {

                        val dialogView = View.inflate(context, R.layout.submit_dialog_layout, null)

                        val builder = android.app.AlertDialog.Builder(context).setView(dialogView).create()


                        builder.show()

                        builder.setCancelable(false)

                        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

                        var dialogOk=dialogView.findViewById(R.id.dialog_ok) as Button
                        var dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

                        dialogTxt.text="YOUR VOTE IS DONE"

                        dialogOk.setOnClickListener {

                            val intent=Intent(context,VoteListActivity::class.java)

                            context.startActivity(intent)

                            (context as Activity).finish()

                            builder.dismiss()

                        }


                    }

                FirebaseDatabase.getInstance().reference.child("VoteResponse")
                    .child(voteId)
                    .child("TotalVoteCount")
                    .child(FirebaseAuth.getInstance().uid.toString()).setValue(true)


            }


        }

    }



}