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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


enum class MarsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    //private val _status = MutableLiveData<String>()
    // The external immutable LiveData for the request status String
    //val status: LiveData<String>
        //get() = _status
    private val _status = MutableLiveData<MarsApiStatus>()
    val status: LiveData<MarsApiStatus>
        get() = _status


    //liveData to store properties
    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    //live data for when to navigate to detail
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    // coroutine Job and a CoroutineScope using the Main Dispatcher
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
        //retrofit does all its work in a background thread so no need to use any other thread for scope

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL) //filter show all properties when app first loads
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    // calls retrofit service and then handles the returned JSON string
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        /* //CALLBACK VERSION
        //MarsApi.retrofitService.getProperties() returns a call object
        //call enqueue on that callback to start a network request on a background thread
        //
        //enqueue takes a retrofit callback class as input that contains methods that will
        //be called when the request is complete. which has both success and failure methods
        //for when retrofit is successful/fail in fetching the JSON

        //---MarsApi.retrofitService.getProperties().enqueue( object: Callback<String> {
        MarsApi.retrofitService.getProperties().enqueue( object: Callback<List<MarsProperty>> {
            //Define a kotlin object that implements the Callback
            //Press CTRL-I to implement the methods, select both methods

            //---override fun onFailure(call: Call<String>, t: Throwable) {
            override fun onFailure(call: Call<List<MarsProperty>>, t: Throwable) {
                //_response is a string livedata that determines what is shown in text view

                //to see error case, turn on airplane mode
                _response.value = "Failure: " + t.message  //message from throwable
            }

            //---override fun onResponse(call: Call<String>, response: Response<String>) {
            override fun onResponse(call: Call<List<MarsProperty>>, response: Response<List<MarsProperty>>) {\
                //response body no longer a string, now a list of mars properties
                _response.value = "Success: ${response.body()?.size} Mars properties retrieved"
                //_response.value = response.body()
            }
        })*/

        //COROUTINE VERSION
        //In order to use deferred, must be inside coroutine scope
        //still running on main thread, but letting coroutines manage concurrency
        coroutineScope.launch {
            var getPropertiesDeferred = MarsApi.retrofitService.getProperties(filter.value)
                //calling getProperties creates and starts the network call on a background thread returning the deferred

            //try/catch to have same error handling we had in the call back version
            try{
                //set loading state at beginning
                _status.value = MarsApiStatus.LOADING

                //calling await on the deferred returns the result from the network call when the value is ready
                //await is nonblocking, so will retrieve data without blocking our current thread
                //which is important since in scope of the UI thread
                var listResult = getPropertiesDeferred.await()

                //_status.value = "Success: ${listResult.size} Mars properties retrieved"
                _status.value = MarsApiStatus.DONE //set DONE when we are finished

                //set _property to first MarsProperty from listResult
                if (listResult.size > 0) {
                    _properties.value = listResult
                }
            } catch (t:Throwable){
                //_status.value = "Failure: " + t.message  //message from throwable
                _status.value = MarsApiStatus.ERROR  //set ERROR when fail
                _properties.value = ArrayList()  //set to empty list to clear recyclerview
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        //cancel the Job when the ViewModel is finished, necessary for the coroutine version
        viewModelJob.cancel()
    }

    fun displayPropertyDetails(marsProperty: MarsProperty) {
        //set _navigateToSelectedProperty to marsProperty and initiate navigation to the detail screen on button click:
        _navigateToSelectedProperty.value = marsProperty
    }
    fun displayPropertyDetailsComplete() {
        //set _navigateToSelectedProperty to false once navigation is completed to prevent unwanted extra navigations:
        _navigateToSelectedProperty.value = null
    }

    //takes filter input and reloads properties using that filter
    fun updateFilter(filter: MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }
}
