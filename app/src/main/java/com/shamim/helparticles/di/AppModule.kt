package com.shamim.helparticles.di

import android.content.Context
import android.net.ConnectivityManager
import com.shamim.helparticles.NetworkConnectivityChecker
import com.shamim.helparticles.NetworkConnectivityCheckerImpl
import com.shamim.helparticles.data.network.ArticleApiService
import com.shamim.helparticles.data.network.ArticleRepository
import com.shamim.helparticles.data.network.ArticleRepositoryImpl
import com.shamim.helparticles.data.network.MockInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Singleton
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideJson(): Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    @Provides
    fun provideRandomProvider(): () -> Int = {
        Random.nextInt(0, 101)
    }

    @Provides
    fun getConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun provideNetworkConnectivityChecker(connectivityManager: ConnectivityManager): NetworkConnectivityChecker =
        NetworkConnectivityCheckerImpl(connectivityManager)


    @Provides
    fun provideMockInterceptor(
        connectivityChecker: NetworkConnectivityChecker,
        json: Json,
        randomProvider: () -> Int
    ): Interceptor =
        MockInterceptor(
            networkConnectivityChecker = connectivityChecker,
            json = json,
            randomProvider = randomProvider,
            timeoutProbability = 5,
            serverErrorProbability = 5,
            malformedResponseProbability = 5,
            emptyArticleListProbability = 5,
        )

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://fake.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ArticleApiService =
        retrofit.create(
            ArticleApiService::class.java
        )

    @Provides
    fun provideLocale(): Locale = Locale.getDefault()

    @Provides
    fun provideDateFormatter(locale: Locale): SimpleDateFormat =
        SimpleDateFormat("MMM dd, yyyy", locale)


    @Singleton
    @Provides
    fun provideArticleRepository(
        apiService: ArticleApiService,
        dateFormat: SimpleDateFormat,
        json: Json,
    ): ArticleRepository =
        ArticleRepositoryImpl(
            apiService = apiService,
            simpleDateFormat = dateFormat,
            json = json,
        )

}