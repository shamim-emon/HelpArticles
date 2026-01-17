package com.shamim.helparticles.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shamim.cache.SimpleCache
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails
import com.shamim.helparticles.data.network.ArticleApiService
import com.shamim.helparticles.data.network.ArticleRepository
import com.shamim.helparticles.data.network.ArticleRepositoryImpl
import com.shamim.helparticles.data.network.TestStubInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Singleton
    @Provides
    fun provideArticlesCache(): SimpleCache<String, List<Article>> = SimpleCache(clock = { System.currentTimeMillis()})

    @Singleton
    @Provides
    fun provideArticleDetailsCache(): SimpleCache<String, ArticleDetails> = SimpleCache(clock = { System.currentTimeMillis()})
    @Provides
    fun provideJson(): Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    @Singleton
    @Provides
    fun provideTestStubInterceptor(json: Json): TestStubInterceptor = TestStubInterceptor(json = json)

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: TestStubInterceptor): OkHttpClient =
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