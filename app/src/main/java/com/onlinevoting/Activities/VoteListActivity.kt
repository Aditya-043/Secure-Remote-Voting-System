package com.sunayanpradhan.onlinevoting.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.onlinevoting.Adapters.ElectionListAdapter
import com.sunayanpradhan.onlinevoting.Adapters.VoteAdapter
import com.sunayanpradhan.onlinevoting.Models.TeamInformation
import com.sunayanpradhan.onlinevoting.Models.VoteInformation
import com.sunayanpradhan.onlinevoting.R

class VoteListActivity : AppCompatActivity() {

    lateinit var elcListRecyclerView: RecyclerView

    lateinit var list:ArrayList<VoteInformation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_list)

        elcListRecyclerView=findViewById(R.id.elc_list_recyclerView)

        list= ArrayList()

        val layoutManager=LinearLayoutManager(this)

        elcListRecyclerView.layoutManager=layoutManager

        val adapter= ElectionListAdapter(list,this)


        FirebaseDatabase.getInstance().reference.child("Votes").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (datasnapShot in snapshot.children){

                    val data: VoteInformation?=datasnapShot.getValue(VoteInformation::class.java)

                    data?.voteId=datasnapShot.key.toString()

                    list.add(data!!)

                }

                adapter.notifyDataSetChanged()

                elcListRecyclerView.adapter=adapter


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@VoteListActivity, error.message, Toast.LENGTH_SHORT).show()

            }
        })




    }
}