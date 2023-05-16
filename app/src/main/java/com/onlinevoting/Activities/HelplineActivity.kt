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
import com.sunayanpradhan.onlinevoting.Adapters.HelplineAdapter
import com.sunayanpradhan.onlinevoting.Models.HelplineInformation
import com.sunayanpradhan.onlinevoting.R

class HelplineActivity : AppCompatActivity() {

    lateinit var helplineRecyclerView:RecyclerView

    lateinit var list: ArrayList<HelplineInformation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helpline)

        helplineRecyclerView=findViewById(R.id.helpline_recyclerView)

        list= ArrayList()

        val adapter=HelplineAdapter(list,this)

        val layoutManager=LinearLayoutManager(this)

        helplineRecyclerView.layoutManager=layoutManager

        FirebaseDatabase.getInstance().reference.child("Helpline").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (dataSnapshot in snapshot.children){

                    val data:HelplineInformation?=dataSnapshot.getValue(HelplineInformation::class.java)

                    list.add(data!!)


                }
                adapter.notifyDataSetChanged()

                helplineRecyclerView.adapter=adapter


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HelplineActivity, error.message, Toast.LENGTH_SHORT).show()
            }


        })


    }
}