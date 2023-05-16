package com.sunayanpradhan.onlinevoting.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.onlinevoting.Activities.VoteActivity
import com.sunayanpradhan.onlinevoting.Models.VoteInformation
import com.sunayanpradhan.onlinevoting.R
import java.util.*
import kotlin.collections.ArrayList

class ElectionListAdapter(var list:ArrayList<VoteInformation>, var context: Context):RecyclerView.Adapter<ElectionListAdapter.ViewHolder>() {

    private var isVoted=false

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val electionLogo:ImageView=itemView.findViewById(R.id.election_logo)
        val electionName:TextView=itemView.findViewById(R.id.election_name)
        val electionStatus:TextView=itemView.findViewById(R.id.election_status)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.election_layout,parent,false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        var currentItem=list[position]

        Glide.with(context).load(currentItem.voteLogo).into(holder.electionLogo)

        holder.electionName.text=currentItem.voteTitle

        var currentTime:Long= Date().time

        if (currentItem.voteStartTime>currentTime){

            holder.electionStatus.text="NOT STARTED"

        }

        else if (currentItem.voteStartTime<=currentTime && currentItem.voteEndTime>=currentTime){

            holder.electionStatus.text="IN PROCESS"

        }

        else{

            holder.electionStatus.text="COMPLETED"

        }




        FirebaseDatabase.getInstance().reference.child("VoteResponse")
            .child(currentItem.voteId).child("TotalVoteCount")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (dataSnapshot in snapshot.children){

                        if (FirebaseAuth.getInstance().uid==dataSnapshot.key){

                            isVoted=true

                        }


                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }


            })

        holder.itemView.setOnClickListener {

            if (isVoted==true){

                val dialogView = View.inflate(context, R.layout.submit_dialog_layout, null)

                val builder = android.app.AlertDialog.Builder(context).setView(dialogView).create()


                builder.show()

                builder.setCancelable(false)

                builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

                var dialogOk=dialogView.findViewById(R.id.dialog_ok) as Button
                var dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

                dialogTxt.text="YOU ARE ALREADY VOTED"

                dialogOk.setOnClickListener {

                    builder.dismiss()

                }

            }

            else if(currentItem.voteStartTime>currentTime){

                val dialogView = View.inflate(context, R.layout.submit_dialog_layout, null)

                val builder = android.app.AlertDialog.Builder(context).setView(dialogView).create()


                builder.show()

                builder.setCancelable(false)

                builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

                var dialogOk=dialogView.findViewById(R.id.dialog_ok) as Button
                var dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView
                var checkAnimation=dialogView.findViewById(R.id.check_animation) as LottieAnimationView

                checkAnimation.visibility=View.GONE

                dialogTxt.text="VOTE HAS NOT STARTED"

                dialogOk.setOnClickListener {

                    builder.dismiss()

                }

            }

            else if (currentItem.voteEndTime<currentTime){

                val dialogView = View.inflate(context, R.layout.submit_dialog_layout, null)

                val builder = android.app.AlertDialog.Builder(context).setView(dialogView).create()


                builder.show()

                builder.setCancelable(false)

                builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

                var dialogOk=dialogView.findViewById(R.id.dialog_ok) as Button
                var dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView
                var checkAnimation=dialogView.findViewById(R.id.check_animation) as LottieAnimationView

                checkAnimation.visibility=View.GONE

                dialogTxt.text="VOTE COMPLETED"

                dialogOk.setOnClickListener {

                    builder.dismiss()

                }

            }

            else {
                val intent = Intent(context, VoteActivity::class.java)

                intent.putExtra("voteId", currentItem.voteId)

                context.startActivity(intent)
            }


        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}