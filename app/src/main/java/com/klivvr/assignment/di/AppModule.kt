package com.klivvr.assignment.di

import android.content.Context
import com.klivvr.assignment.data.repo.CityRepoImpl
import com.klivvr.assignment.data.search.Trie
import com.klivvr.assignment.domain.repo.CityRepo
import com.klivvr.assignment.domain.search.SearchAlgorithm
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
    fun provideCityRepo(cityRepoImpl: CityRepoImpl): CityRepo {
        return cityRepoImpl
    }

    @Singleton
    @Provides
    fun provideCityRepoImpl(
        @ApplicationContext context: Context,
        searchAlgorithm: SearchAlgorithm,
    ): CityRepoImpl {
        return CityRepoImpl(context, searchAlgorithm)
    }

    @Singleton
    @Provides
    fun provideSearchAlgorithm(searchAlgoImpl: Trie): SearchAlgorithm {
        return searchAlgoImpl
    }

    @Singleton
    @Provides
    fun provideTrie(): Trie = Trie()

}