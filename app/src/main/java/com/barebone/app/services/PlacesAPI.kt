package com.barebone.app.services

import com.barebone.app.datamodel.PlaceResultModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesAPI {
    @GET("textsearch/json")
    fun search(
        @Query("query") query: String,
        @Query("key") key: String
    ): Observable<PlaceResultModel.Results>

    companion object {
        fun create(): PlacesAPI {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .build()

            return retrofit.create(PlacesAPI::class.java)
        }
    }
}
