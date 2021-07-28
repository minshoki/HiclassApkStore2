package com.iscreammedia.app.hiclassapkstore

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iscreammedia.app.hiclassapkstore.databinding.ItemSheetDataBinding
import com.iscreammedia.app.hiclassapkstore.model.SheetDataModel

class SheetAdapter : PagingDataAdapter<SheetDataModel, SheetAdapter.SheetViewHolder>(
    object : DiffUtil.ItemCallback<SheetDataModel>() {
        override fun areItemsTheSame(oldItem: SheetDataModel, newItem: SheetDataModel): Boolean {
            return oldItem.content == newItem.content &&
                    oldItem.version == newItem.version
        }

        override fun areContentsTheSame(oldItem: SheetDataModel, newItem: SheetDataModel): Boolean {
            return oldItem == newItem
        }

    }
) {

    inner class SheetViewHolder(
        private val binding: ItemSheetDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                binding.item?.let { item ->
                    if(item.downloadUrl.isBlank()) {
                        MaterialAlertDialogBuilder(binding.root.context).setMessage("아직 다운로드 받을 수 없어요.")
                            .setCancelable(false)
                            .setPositiveButton("확인") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    } else {
                        binding.root.context.startActivity(Intent(Intent.ACTION_VIEW, item.downloadUrl.toUri()))
                    }
                }
            }
        }

        fun onBind(item: SheetDataModel) {
            binding.item = item
        }
    }

    override fun onBindViewHolder(holder: SheetViewHolder, position: Int) {
        getItem(position)?.let { holder.onBind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetViewHolder {
        return SheetViewHolder(
            binding = ItemSheetDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}