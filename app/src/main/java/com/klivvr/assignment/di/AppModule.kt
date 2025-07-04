package com.klivvr.assignment.di

import android.content.Context
import com.klivvr.assignment.data.CityRepository
import com.klivvr.assignment.data.Trie
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCityRepo(
        @ApplicationContext context: Context,
        trie: Trie,
    ): CityRepository {
        return CityRepository(context, trie)
    }

    @Singleton
    @Provides
    fun provideTrie(): Trie {
        return Trie()
    }

}