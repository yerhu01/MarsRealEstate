/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.MarsProperty


//class PhotoGridAdapter : ListAdapter<MarsProperty, PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback) {
    //extend recyclerview ListAdapter, which means must pass list item type, viewholder, and diffutil callback implementation
class PhotoGridAdapter(private val onClickListener: OnClickListener) :
        ListAdapter<MarsProperty, PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback) {

    //viewholder for photogrid adapter
    class MarsPropertyViewHolder(private var binding: GridViewItemBinding):
            RecyclerView.ViewHolder(binding.root) {
        //have gridviewitembinding binding variable around for binding mars property to
        //the layout
        //Base ViewHolder class requires view in constructor so pass in binding.root view

        //takes a MarsProperty and sets the property in the binding class
        fun bind(marsProperty: MarsProperty) {
            binding.property = marsProperty
            binding.executePendingBindings() //cause the property update to execute immediately
        }
    }

    //make DiffCallback as companion object because need in name space of our class but doesn't need a reference
    //our class
    companion object DiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
            //extend DiffUtil.ItemCallback with the type of item we want to compare


        override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem === newItem  //reference equality triple equal sign
        }

        override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGridAdapter.MarsPropertyViewHolder {
        //returns MarsPropertyViewHolder created by inflating GridViewItemBinding
        //using LayoutInflater from parent viewgroup context
        return MarsPropertyViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PhotoGridAdapter.MarsPropertyViewHolder, position: Int) {
        //get marsProperty associated with recycler view position
        //and use it to call bind in ViewHolder
        val marsProperty = getItem(position)

        //call the onclick function from the onclicklistener in a lambda from setonclicklistener
        holder.itemView.setOnClickListener {
            onClickListener.onClick(marsProperty)
        }
        holder.bind(marsProperty)
    }


    //internal OnClickListener class with a lambda in its constructor that initializes a matching onClick function:
    class OnClickListener(val clickListener: (marsProperty: MarsProperty) -> Unit) {
        fun onClick(marsProperty:MarsProperty) = clickListener(marsProperty)
    }
}
