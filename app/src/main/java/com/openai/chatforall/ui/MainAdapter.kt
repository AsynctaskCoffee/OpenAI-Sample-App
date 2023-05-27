package com.openai.chatforall.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.openai.chatforall.data.MessageData
import com.openai.chatforall.data.MessageViewType
import com.openai.chatforall.databinding.ItemLoadingMessageBinding
import com.openai.chatforall.databinding.ItemOtherMessageBinding
import com.openai.chatforall.databinding.ItemUserMessageBinding

class MainAdapter(private val messages: List<MessageData>) :
    RecyclerView.Adapter<MainAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = if (viewType == MessageViewType.MESSAGE_USER.value) {
            ItemUserMessageBinding.inflate(layoutInflater, parent, false)
        } else if (viewType == MessageViewType.MESSAGE_OTHER.value) {
            ItemOtherMessageBinding.inflate(layoutInflater, parent, false)
        } else{
            ItemLoadingMessageBinding.inflate(layoutInflater, parent, false)
        }
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        if (message.messageType == MessageViewType.MESSAGE_USER) {
            (holder.binding as ItemUserMessageBinding).tvUserMessage.text = message.content
        } else if (message.messageType == MessageViewType.MESSAGE_OTHER) {
            val otherMessageBinding = (holder.binding as ItemOtherMessageBinding)
            message.content?.let {
                otherMessageBinding.tvOtherMessage.text = it
                otherMessageBinding.tvOtherMessage.visibility = View.VISIBLE
            }
            message.imageData?.let {
                if (it.isNotEmpty()) {
                    otherMessageBinding.tvOtherMessage.visibility = View.GONE
                    otherMessageBinding.layoutImages.visibility = View.VISIBLE

                    // Assuming you are using Glide or Picasso for image loading
                    Glide.with(otherMessageBinding.root.context)
                        .load(it[0]?.url)
                        .into(otherMessageBinding.imgOther1)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].messageType.value
    }

}
