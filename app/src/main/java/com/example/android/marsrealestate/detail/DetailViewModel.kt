/*
 *  Copyright 2018, The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.marsrealestate.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.network.MarsProperty


/**
 * The [ViewModel] that is associated with the [DetailFragment].
 *///@Suppress("UNUSED_PARAMETER")marsProperty: MarsProperty
class DetailViewModel(marsProperty: MarsProperty, app: Application) : AndroidViewModel(app) {

    private val _selectedProperty = MutableLiveData<MarsProperty>()
    val selectedProperty: LiveData<MarsProperty>
        get() = _selectedProperty

    init {
        _selectedProperty.value = marsProperty
    }

    /**livedata transformations to format the relevant marsproperty properties
    since we have the application class handy as param, can use it to get application context
    which allows us to access android string resources from our viewmodel*/
    //properly display property price to a displayable string
    val displayPropertyPrice = Transformations.map(selectedProperty) {
        app.applicationContext.getString(
                //want to get a different string when its a rental vs purchase
                when (it.isRental) {
                    true -> R.string.display_price_monthly_rental //in strings.xml
                    false -> R.string.display_price
                }, it.price) //both strings need a numeric parameter which is the price
    }
    //display whether selectedProperty is for sale or rent
    val displayPropertyType = Transformations.map(selectedProperty) {
        //nest getString calls
        //use result of isRental statement to display_type as a parameter
        app.applicationContext.getString(R.string.display_type,
                app.applicationContext.getString(
                        when(it.isRental) {
                            true -> R.string.type_rent
                            false -> R.string.type_sale
                        }))
    }
}
