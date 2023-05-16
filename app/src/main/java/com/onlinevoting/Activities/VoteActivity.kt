package com.sunayanpradhan.onlinevoting.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.onlinevoting.R
import com.sunayanpradhan.onlinevoting.Models.TeamInformation
import com.sunayanpradhan.onlinevoting.Adapters.VoteAdapter
import com.sunayanpradhan.onlinevoting.Models.VoteInformation
import com.sunayanpradhan.onlinevoting.Models.VoteResponseInformation
import java.text.SimpleDateFormat

class VoteActivity : AppCompatActivity() {

    lateinit var voteLogo:ImageView
    lateinit var voteTitle:TextView
    lateinit var voteDuration:TextView

    lateinit var teamRecyclerView: RecyclerView

    lateinit var list:ArrayList<TeamInformation>



    companion object{

        lateinit var voteSubmit:Button
        lateinit var voteId:String

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote)

        voteLogo=findViewById(R.id.vote_logo)
        voteTitle= findViewById(R.id.vote_title)
        voteDuration=findViewById(R.id.vote_duration)
        teamRecyclerView=findViewById(R.id.team_recyclerView)
        voteSubmit=findViewById(R.id.vote_submit)

        val intent=intent

        voteId=intent.getStringExtra("voteId").toString()


        list= ArrayList()

        val adapter= VoteAdapter(list,this)

        val layoutManager=LinearLayoutManager(this)

        teamRecyclerView.layoutManager=layoutManager


        FirebaseDatabase.getInstance().reference.child("Votes").child(voteId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {



                    val data: VoteInformation?=snapshot.getValue(VoteInformation::class.java)

                    data?.voteId=snapshot.key.toString()

                    Glide.with(this@VoteActivity).load(data?.voteLogo).into(voteLogo)

                    voteTitle.text= data?.voteTitle

                    voteDuration.text= SimpleDateFormat.getInstance().format( data?.voteStartTime).toString()+"-"+SimpleDateFormat.getInstance().format( data?.voteEndTime).toString()




            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@VoteActivity, error.message, Toast.LENGTH_SHORT).show()

            }
        })




        FirebaseDatabase.getInstance().reference.child("Votes").child(voteId).child("voteTeams").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children){

                    FirebaseDatabase.getInstance().reference.child("VoteTeams").child(dataSnapshot.key.toString()).addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot1: DataSnapshot) {
                            var data: TeamInformation? =snapshot1.getValue(TeamInformation::class.java)

                            data?.teamId= snapshot1.key.toString()

                            list.add(data!!)

                            adapter.notifyDataSetChanged()

                        }


                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@VoteActivity, error.message, Toast.LENGTH_SHORT).show()
                        }


                    })


                }


                teamRecyclerView.adapter=adapter

            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VoteActivity, error.message, Toast.LENGTH_SHORT).show()
            }


        })



    }
}