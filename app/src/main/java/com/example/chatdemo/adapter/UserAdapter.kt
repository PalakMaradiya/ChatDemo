package com.example.chatdemo.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatdemo.R
import com.example.chatdemo.utils.UserModal
import com.google.firebase.auth.FirebaseAuth

class UserAdapter(
    var context: Context,
    var userList: ArrayList<UserModal>,
    var onItemClick: ((name: String, userId: String, email: String, img: String) -> Unit)
) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    private val selectedItems = mutableListOf<UserModal>()

    class MyViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        var txtFirstName: TextView = itemView.findViewById(R.id.txtFirstName)
        var linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        var img: ImageView = itemView.findViewById(R.id.img)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.users_item_file, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {

        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var userModal: UserModal = userList[position]

        Glide.with(context).load(userModal.img()).into(holder.img)

        holder.txtFirstName.text = userModal.firstName()

        if (userModal.email == FirebaseAuth.getInstance().currentUser?.email) {
            holder.txtFirstName.text = "You"
        } else {
            userModal.firstName()
        }

        holder.linearLayout.setOnClickListener {

            userModal.isSelected = !userModal.isSelected
            if (userModal.isSelected) {
                selectedItems.add(userModal)
            } else {
                selectedItems.remove(userModal)
            }
            notifyItemChanged(position)
            onItemClick.invoke(
                userList[position].firstName,
                userList[position].userId,
                userList[position].img,
                userList[position].email
            )

            Log.e("TAG", "userImg: "+userList[position].img )
            if (userModal.isSelected) {
                holder.checkBox.visibility = View.VISIBLE
            } else {
                holder.checkBox.visibility = View.GONE
            }
            holder.checkBox.isChecked = userModal.isSelected
        }
    }


    fun reorderList() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val currentUserIndex = userList.indexOfFirst { it.email == currentUserEmail }
        if (currentUserIndex != -1) {
            val currentUser = userList.removeAt(currentUserIndex)
            userList.add(0, currentUser)
            notifyDataSetChanged()
        }


    }

}