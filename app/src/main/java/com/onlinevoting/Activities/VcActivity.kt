package com.sunayanpradhan.onlinevoting.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.onlinevoting.Adapters.CountListAdapter
import com.sunayanpradhan.onlinevoting.Adapters.ElectionScheduleAdapter
import com.sunayanpradhan.onlinevoting.Models.VoteInformation
import com.sunayanpradhan.onlinevoting.R

class VcActivity : AppCompatActivity() {

    lateinit var resultListRecyclerView:RecyclerView

    lateinit var list:ArrayList<VoteInformation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vc)

        resultListRecyclerView=findViewById(R.id.result_list_recyclerView)

        list= ArrayList()


        val layoutManager= LinearLayoutManager(this)

        resultListRecyclerView.layoutManager=layoutManager

        val adapter= CountListAdapter(list,this)


        FirebaseDatabase.getInstance().reference.child("Votes").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (datasnapShot in snapshot.children){

                    val data: VoteInformation?=datasnapShot.getValue(VoteInformation::class.java)

                    data?.voteId=datasnapShot.key.toString()

                    list.add(data!!)

                }

                adapter.notifyDataSetChanged()

                resultListRecyclerView.adapter=adapter


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@VcActivity, error.message, Toast.LENGTH_SHORT).show()

            }
        })




    }
}