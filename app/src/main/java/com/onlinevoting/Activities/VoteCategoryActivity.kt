package com.sunayanpradhan.onlinevoting.Activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.sunayanpradhan.onlinevoting.R

class VoteCategoryActivity : AppCompatActivity() {

    lateinit var electionsLayout:LinearLayout

    lateinit var scheduleLayout:LinearLayout

    lateinit var resultsLayout: LinearLayout

    lateinit var helplineLayout: LinearLayout

    lateinit var logout:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_category)

        electionsLayout=findViewById(R.id.elections_layout)
        scheduleLayout=findViewById(R.id.schedule_layout)
        resultsLayout=findViewById(R.id.results_layout)
        helplineLayout=findViewById(R.id.helpline_layout)
        logout=findViewById(R.id.logout)

        electionsLayout.setOnClickListener {

            val intent=Intent(this,VoteListActivity::class.java)

            startActivity(intent)


        }

        scheduleLayout.setOnClickListener {

            val intent=Intent(this,VoteScheduleActivity::class.java)

            startActivity(intent)

        }

        resultsLayout.setOnClickListener {

            val intent=Intent(this,VcActivity::class.java)

            startActivity(intent)

        }

        helplineLayout.setOnClickListener {

            val intent=Intent(this,HelplineActivity::class.java)

            startActivity(intent)

        }


        logout.setOnClickListener {

            MaterialAlertDialogBuilder(this)
                .setMessage("DO YOU WANT TO LOGOUT?")
                .setPositiveButton("YES") { dialog, which ->
                    // Respond to positive button press

                    FirebaseAuth.getInstance().signOut()

                    val intent=Intent(this,MainActivity::class.java)

                    startActivity(intent)

                    finish()


                }
                .setNegativeButton("NO") { dialog, which ->
                    // Respond to positive button press
                }
                .setCancelable(true)
                .show()





        }


    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setMessage("DO YOU WANT TO EXIT?")
            .setPositiveButton("YES") { dialog, which ->
                // Respond to positive button press

                finishAffinity()


            }
            .setNegativeButton("NO") { dialog, which ->
                // Respond to positive button press
            }
            .setCancelable(true)
            .show()

    }




}