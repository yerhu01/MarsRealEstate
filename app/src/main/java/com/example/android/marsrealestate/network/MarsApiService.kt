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

package com.example.android.marsrealestate.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://mars.udacity.com/"

//an enum to define some constants to match the query values our web service expects
enum class MarsApiFilter(val value: String) { SHOW_RENT("rent"), SHOW_BUY("buy"), SHOW_ALL("all") }

//build moshi object using moshi builder
//In order for moshi's annotations to work properly with kotlin,
//add kotlinjsonadapterfactory
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


//(old) Build retrofit object with scalarsconverterfactory and base_url
//Build retrofit object with moshiconverterfactory and base_url
private val retrofit = Retrofit.Builder()
        //.addConverterFactory(ScalarsConverterFactory.create())  //converts json response into string
        .addConverterFactory(MoshiConverterFactory.create(moshi)) //converts json response into kotlin objects
        .addCallAdapterFactory(CoroutineCallAdapterFactory()) //enable retrofit to produce a coroutine based api
            //calladapters add the ability for retrofit to create api's that returns other than the default call class
            //allows us to replace the call in getProperties, with coroutine deferred
        .baseUrl(BASE_URL)
        .build()

//Create a MarsApiService interface that explains how retrofit talks to our webserver
// using HTTP requests
// Retrofit will create an object that implements our interface
// = define a getProperties() method to request the JSON response string
interface MarsApiService {
    @GET("realestate")  //path/endpoint we want for the JSON response
    fun getProperties(@Query("filter") type: String): //creates Call object
        //use query annotation so getProperties will take
        // string input for the filter query that the web service expects

        //Call<String>   //Call object used to start the request
        //Call<List<MarsProperty>>  //return a List of MarsProperty objects

        //after adding coroutinecalladaptor to build, replace call with deferred.
        //deferred is a coroutine job that directly returns a result.
        //retrofit will return a deferred and then you will await() the result
        //which won't block the code (appears synchronously),
        //if theres an error, await will throw exception
        Deferred<List<MarsProperty>>
}

// Passing in the service API you just defined, create a public object called MarsApi
// to expose the Retrofit service to the rest of the app
//-
//Calling MarsApi.retrofitService will return a retrofit object that implements
//MarsApiService
object MarsApi {
    val retrofitService : MarsApiService by lazy { //lazily initialized retrofit object
        retrofit.create(MarsApiService::class.java)
            //gets initialized by retrofit.create with MarsApiService interface
    }
}


