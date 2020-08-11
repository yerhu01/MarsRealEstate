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

package com.example.android.marsrealestate

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.MarsApiStatus
import com.example.android.marsrealestate.overview.PhotoGridAdapter

//want this binding adapter executed when xml item has imageUrl as attribute
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    //view parameter is specified as an imageview so only imageview/derived classes
    //ccan use this adapter

    //use let block to handle null URI's
    imgUrl?.let {
        //Glide requires an Uri object so inside adapter, convert image url to an uri
        //make sure the resulting uri has https scheme b/c server we are using
        //requires https
        //toUri is a kotlin extension function android ktx library which is why it looks
        //like part of the string class
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
                .load(imgUri)
                .apply(RequestOptions()
                        //add a placeholder that displays an image while your image downloads,
                        // and an error image in case it can't be retrieved.
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgView)
    }
}

//Use this binding adapter to initialize photogridadapter with list data
//using binding adapter to set the recycler view data will cause databinding
//to automatically observe the live data for the list of MarsProperties
//This adapter will be called automatically when the MarsProperties list changes
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MarsProperty>?) {
    //cast recycleview adapter to photoGridadapter
    val adapter = recyclerView.adapter as PhotoGridAdapter
    adapter.submitList(data)  //submit list with updated list data
}


//affects content and visibility of status view
@BindingAdapter("marsApiStatus")
fun bindStatus(statusImageView: ImageView, status: MarsApiStatus?) {
    when (status) {
        MarsApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        MarsApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        MarsApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}


