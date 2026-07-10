package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface IptvApi {

    @GET("channels.json")
    suspend fun getChannels(): List<Channel>

    @GET("streams.json")
    suspend fun getStreams(): List<Stream>

    @GET("logos.json")
    suspend fun getLogos(): List<Logo>

    @GET("categories.json")
    suspend fun getCategories(): List<Category>

    @GET("countries.json")
    suspend fun getCountries(): List<Country>

    companion object {
        private const val BASE_URL = "https://iptv-org.github.io/api/"

        fun create(): IptvApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(IptvApi::class.java)
        }
    }
}
