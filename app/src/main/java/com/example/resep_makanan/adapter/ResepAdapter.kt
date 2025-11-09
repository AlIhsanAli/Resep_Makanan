package com.example.resep_makanan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resep_makanan.R
import com.example.resep_makanan.model.Resep

class ResepAdapter(private val resepList: List<Resep>) : RecyclerView.Adapter<ResepAdapter.ResepViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resep, parent, false)
        return ResepViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val resep = resepList[position]
        holder.bind(resep)
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(resep, holder.ivImage)
        }
    }

    override fun getItemCount(): Int = resepList.size

    inner class ResepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.iv_resep_image) // Dibuat public
        private val tvName: TextView = itemView.findViewById(R.id.tv_resep_name)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_resep_description)

        fun bind(resep: Resep) {
            Glide.with(itemView.context)
                .load(resep.image)
                .into(ivImage)
            tvName.text = resep.name
            tvDescription.text = resep.description
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Resep, imageView: ImageView) // Ditambahkan ImageView
    }
}
