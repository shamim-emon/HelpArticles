package com.shamim.helparticles.di

import android.content.Context
import android.net.ConnectivityManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shamim.cache.CacheItem
import com.shamim.cache.FileStorage
import com.shamim.cache.SimpleCache
import com.shamim.cache.Storage
import com.shamim.helparticles.NetworkConnectivityChecker
import com.shamim.helparticles.NetworkConnectivityCheckerImpl
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails
import com.shamim.helparticles.data.network.ArticleApiService
import com.shamim.helparticles.data.network.ArticleRepository
import com.shamim.helparticles.data.network.ArticleRepositoryImpl
import com.shamim.helparticles.data.network.MockInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Singleton
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesArticlesFileStorage(
        @ApplicationContext context: Context,
        json: Json
    ): Storage<String, CacheItem<List<Article>>> =
        FileStorage(
            context = context, serializer = CacheItem.serializer(
                ListSerializer(Article.serializer())
            ), json = json
        )

    @Provides
    @Singleton
    fun providesArticleDetailsFileStorage(
        @ApplicationContext context: Context,
        json: Json
    ): Storage<String, CacheItem<ArticleDetails>> = FileStorage(
        context = context, serializer = CacheItem.serializer(
            ArticleDetails.serializer()
        ), json = json
    )

    @Singleton
    @Provides
    fun provideArticlesCache(storage: Storage<String, CacheItem<List<Article>>>): SimpleCache<String, List<Article>> =
        SimpleCache(
            storage = storage,
            clock = { System.currentTimeMillis() }
        )

    @Singleton
    @Provides
    fun provideArticleDetailsCache(storage: Storage<String, CacheItem<ArticleDetails>>): SimpleCache<String, ArticleDetails> =
        SimpleCache(
            storage = storage,
            clock = { System.currentTimeMillis() }
        )

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
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://fake.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
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
        articlesCache: SimpleCache<String, List<Article>>,
        articleDetailsCache: SimpleCache<String, ArticleDetails>,
        apiService: ArticleApiService,
        dateFormat: SimpleDateFormat,
        json: Json,
    ): ArticleRepository =
        ArticleRepositoryImpl(
            articlesCache = articlesCache,
            articleDetailsCache = articleDetailsCache,
            apiService = apiService,
            simpleDateFormat = dateFormat,
            json = json,
        )
}
