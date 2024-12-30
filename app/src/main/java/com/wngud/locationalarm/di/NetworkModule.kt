package com.wngud.locationalarm.di

import com.wngud.locationalarm.BuildConfig
import com.wngud.locationalarm.data.db.remote.NaverApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Request

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://openapi.naver.com/"

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .header("X-Naver-Client-Id", BuildConfig.CLIENT_ID)
                .header("X-Naver-Client-Secret", BuildConfig.CLIENT_SECRET)
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }.build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): NaverApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NaverApiService::class.java)
    }
}