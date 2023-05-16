package com.sunayanpradhan.onlinevoting.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sunayanpradhan.onlinevoting.Models.HelplineInformation
import com.sunayanpradhan.onlinevoting.R

class HelplineAdapter(val list: ArrayList<HelplineInformation>,val context: Context):RecyclerView.Adapter<HelplineAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val hlLogo:ImageView=itemView.findViewById(R.id.hl_logo)
        val hlName:TextView=itemView.findViewById(R.id.hl_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.helpline_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem=list[position]

        Glide.with(context).load(currentItem.hlLogo).into(holder.hlLogo)

        holder.hlName.text=currentItem.hlName

        holder.itemView.setOnClickListener {

            val dialogView = View.inflate(context, R.layout.helpline_dialog_layout, null)

            val builder = android.app.AlertDialog.Builder(context).setView(dialogView).create()


            builder.show()


            builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val dialogOk=dialogView.findViewById(R.id.dialog_ok) as Button
            val dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView
            val dialogContext=dialogView.findViewById(R.id.dialog_context) as TextView


            dialogTxt.text=currentItem.hlName
            dialogContext.text=currentItem.hlContext
            dialogOk.setOnClickListener {

                builder.dismiss()

            }


        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


}