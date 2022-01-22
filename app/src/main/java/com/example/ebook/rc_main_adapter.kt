package com.example.ebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*

class rc_main_adapter(private val bookList: ArrayList<Book>) : RecyclerView.Adapter<rc_main_adapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): rc_main_adapter.MyViewHolder {
        val  itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_rc_main,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: rc_main_adapter.MyViewHolder, position: Int) {

        val book : Book = bookList[position]
        holder.bookname.text = book.name!![0].toUpperCase()+ book.name!!.substring(1)
        holder.bookid.text = book.bookid.toString()
        holder.bookauthor.text = book.author


        Glide.with(holder.itemView.getContext()).load(book.url).into(holder.bookimg);
    }

    override fun getItemCount(): Int {
       return bookList.size
    }
 public class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
     val bookname : TextView = itemView.findViewById(R.id.bookname)
     val bookid : TextView = itemView.findViewById(R.id.bookid)
     val bookimg : ImageView = itemView.findViewById(R.id.bookimg)
     val bookauthor : TextView = itemView.findViewById(R.id.bookauthor)
 }
}