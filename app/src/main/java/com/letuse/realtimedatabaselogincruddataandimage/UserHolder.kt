package com.letuse.realtimedatabaselogincruddataandimage

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.letuse.firebasegit.model.Message
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_data.view.*
import kotlinx.android.synthetic.main.item_next.view.*

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(message: Message?) {
        with(message!!) {
            itemView.txt_author.text = author
            itemView.txt_body.text = body
            itemView.txt_time.text = time
            Picasso.get().load(itemImageUrl).into(itemView.show_img)
        }
    }
}