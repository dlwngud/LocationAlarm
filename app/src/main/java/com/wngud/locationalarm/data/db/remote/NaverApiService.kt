package com.wngud.locationalarm.data.db.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NaverApiService {
    @GET("v1/search/local.json")
    suspend fun searchLocation(
        @Query("query") query: String,
        @Query("display") display: Int = 10,
        @Query("start") start: Int = 1
    ): NaverSearchResponse
}

data class NaverSearchResponse(
    val items: List<NaverSearchItem>
)

data class NaverSearchItem(
    val title: String,
    val category: String,
    val description: String,
    val address: String,
    val roadAddress: String
)