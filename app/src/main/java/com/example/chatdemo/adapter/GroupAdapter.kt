import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatdemo.R
import com.example.chatdemo.utils.GroupMessageModal
import com.google.firebase.auth.FirebaseAuth

class GroupAdapter(val context: Context, val groupList: List<GroupMessageModal>,
                   var onVideoPlay :(( videoUrl : String, thumbnail:String) ->Unit), var onItemclick : ((message : String) -> Unit) ):
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>(){

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtRigCht: TextView = itemView.findViewById(R.id.txtRigCht)
        var txtLft: TextView = itemView.findViewById(R.id.txtLft)
        var RightTime: TextView = itemView.findViewById(R.id.RightTime)
        var LeftTime: TextView = itemView.findViewById(R.id.LeftTime)
        var imgRightTime: TextView = itemView.findViewById(R.id.imgRightTime)
        var ImageLeftTime: TextView = itemView.findViewById(R.id.ImageLeftTime)
        var imgRgt: ImageView = itemView.findViewById(R.id.imgRgt)
        var imgLft: ImageView = itemView.findViewById(R.id.imgLft)
        var thumbRight: ImageView = itemView.findViewById(R.id.thumbRight)
        var thumbLeft: ImageView = itemView.findViewById(R.id.thumbLeft)
        var videoRight: VideoView = itemView.findViewById(R.id.videoRight)
        var videoLeft: VideoView = itemView.findViewById(R.id.videoLeft)
        var RightChat: LinearLayout = itemView.findViewById(R.id.RightChat)
        var Left: LinearLayout = itemView.findViewById(R.id.Left)
        var LeftRelative: RelativeLayout = itemView.findViewById(R.id.LeftRelative)
        var RightRelative: RelativeLayout = itemView.findViewById(R.id.RightRelative)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.group_msg_item_file, parent, false)
        return GroupViewHolder(view)
    }
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val groupItem = groupList[position]

        // Reset visibilities to default
        holder.Left.visibility = View.GONE
        holder.RightChat.visibility = View.GONE
        holder.imgLft.visibility = View.GONE
        holder.imgRgt.visibility = View.GONE
        holder.thumbLeft.visibility = View.GONE
        holder.thumbRight.visibility = View.GONE
        holder.videoLeft.visibility = View.GONE
        holder.videoRight.visibility = View.GONE
        holder.LeftRelative.visibility = View.GONE
        holder.RightRelative.visibility = View.GONE

        if (groupItem.senderId == FirebaseAuth.getInstance().currentUser!!.uid) {
            // Handle right side (sent) messages
            holder.RightChat.visibility = View.VISIBLE
            holder.txtRigCht.text = groupItem.message
            holder.RightTime.text = groupItem.currentTime

            holder.txtRigCht.setOnClickListener {
                onItemclick.invoke(groupItem.message)
            }

            if (!groupItem.imageUrl.isNullOrEmpty()) {
                holder.RightRelative.visibility = View.VISIBLE
                Glide.with(context).load(groupItem.imageUrl).into(holder.imgRgt)
                holder.imgRightTime.text = groupItem.currentTime
                holder.imgRgt.visibility = View.VISIBLE
                holder.txtRigCht.visibility = View.GONE
                holder.RightTime.visibility = View.GONE
            }

            if (!groupItem.videoUrl.isNullOrEmpty()) {
                holder.txtRigCht.visibility = View.GONE
                holder.RightTime.visibility = View.GONE
                holder.imgRightTime.text = groupItem.currentTime
                holder.RightRelative.visibility = View.VISIBLE
                Glide.with(context).load(groupItem.videoThumbnailUrl).into(holder.thumbRight)
                holder.thumbRight.visibility = View.VISIBLE
                holder.thumbRight.setOnClickListener {
                    onVideoPlay.invoke(groupItem.videoUrl, groupItem.videoThumbnailUrl)
                }
            }
        } else {
            // Handle left side (received) messages
            holder.Left.visibility = View.VISIBLE
            holder.txtLft.text = groupItem.message
            holder.LeftTime.text = groupItem.currentTime

            if (!groupItem.imageUrl.isNullOrEmpty()) {
                holder.txtLft.visibility = View.GONE
                holder.LeftTime.visibility = View.GONE
                holder.LeftRelative.visibility = View.VISIBLE
                Glide.with(context).load(groupItem.imageUrl).into(holder.imgLft)
                holder.ImageLeftTime.text = groupItem.currentTime
                holder.imgLft.visibility = View.VISIBLE
            }

            if (!groupItem.videoUrl.isNullOrEmpty()) {
                holder.txtLft.visibility = View.GONE
                holder.LeftTime.visibility = View.GONE
                holder.LeftRelative.visibility = View.VISIBLE
                Glide.with(context).load(groupItem.videoThumbnailUrl).into(holder.thumbLeft)
                holder.thumbLeft.visibility = View.VISIBLE
                holder.ImageLeftTime.text = groupItem.currentTime
                holder.thumbLeft.setOnClickListener {
                    onVideoPlay.invoke(groupItem.videoUrl, groupItem.videoThumbnailUrl)
                }
            }
        }

        // Make sure text is always visible if no image or video is present
        if (groupItem.imageUrl.isNullOrEmpty() && groupItem.videoUrl.isNullOrEmpty()) {
            if (groupItem.senderId == FirebaseAuth.getInstance().currentUser!!.uid) {
                holder.RightChat.visibility = View.VISIBLE
            } else {
                holder.Left.visibility = View.VISIBLE
            }
        }
    }



    override fun getItemCount(): Int {
        return groupList.size
    }

}
