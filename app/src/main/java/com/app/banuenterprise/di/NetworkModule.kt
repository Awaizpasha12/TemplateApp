package com.app.banuenterprise.di


import android.content.Context
import com.app.banuenterprise.network.RetrofitInterface
import com.app.banuenterprise.utils.Constants
import com.app.banuenterprise.utils.SessionUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl() = Constants.Config.BASE_URL


    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
//@Provides
//@Singleton
//fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
//    val baseUrl = SessionUtils.getBaseUrl(context)
//    return Retrofit.Builder()
//        .baseUrl(baseUrl)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//}

    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): RetrofitInterface =
        retrofit.create(RetrofitInterface::class.java)
}
