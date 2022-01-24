package com.example.ebook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ebook.R
import com.example.ebook.models.BookDetails
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class BookListRVAdapter (options: FirestoreRecyclerOptions<BookDetails>, private val listener: IBookListRVAdapter):
    FirestoreRecyclerAdapter<BookDetails, BookListRVAdapter.BookListViewHolder>(options){

    inner class BookListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val bookName: TextView = itemView.findViewById(R.id.bookName)
        val bookId: TextView = itemView.findViewById(R.id.bookId)
        val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        val bookCard: CardView = itemView.findViewById(R.id.readFragmentCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val viewHolder = BookListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_book_details,parent,false))
        viewHolder.bookCard.setOnClickListener{
            listener.onBookNameClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int, model: BookDetails) {
        holder.bookName.text = model.name
        holder.bookId.text = model.bookid.toString()
        holder.bookId.visibility = View.GONE
        holder.bookAuthor.text = "By: "+model.author
        Glide.with(holder.itemView.context).load(model.url).into(holder.bookImage)
    }

}

interface IBookListRVAdapter {
    fun onBookNameClicked(bookId: String)
}