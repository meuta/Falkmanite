package com.example.falkmanite.di

import android.content.Context
import com.example.falkmanite.data.PlayerStateCache
import com.example.falkmanite.data.PlaylistDataSource
import com.example.falkmanite.data.SongDataSource
import com.example.falkmanite.data.SongRepository
import com.example.falkmanite.data.db.AppDatabase
import com.example.falkmanite.data.db.PlaylistDao
import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.PlayerStateMapper
import com.example.falkmanite.domain.Repository
import com.example.falkmanite.player.AudioPlayer
import com.example.falkmanite.service.PlayerServiceConnection
import com.example.falkmanite.ui.ProgressMapper
import com.example.falkmanite.ui.ProgressStateFlow
import com.example.falkmanite.ui.StringFormatter
import com.example.falkmanite.ui.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSongDataSource(@ApplicationContext context: Context): SongDataSource {
        return SongDataSource(context)
    }

    @Provides
    @Singleton
    fun providePlaylistDataSource(dao: PlaylistDao): PlaylistDataSource {
        return PlaylistDataSource(dao)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideShopListDao(db: AppDatabase): PlaylistDao {
        return db.playlistDao()
    }

    @Provides
    @Singleton
    fun provideRepository(
        songDataSource: SongDataSource,
        playlistDataSource: PlaylistDataSource
    ): Repository {
        return SongRepository(songDataSource, playlistDataSource)
    }


    @Provides
    @Singleton
    fun provideCache(): InMemoryCache<PlayerState> {
        return PlayerStateCache()
    }


    @Provides
    @Singleton
    fun providePlayerStateUiMapper(): PlayerStateMapper<UiState> {
        return PlayerStateMapper.ToUiState()
    }


    @Provides
    @Singleton
    fun provideProgressUiMapper(): ProgressMapper {
        return ProgressMapper(StringFormatter())
    }

    @Provides
    @Singleton
    fun provideProgressState(): ProgressStateFlow {
        return ProgressStateFlow()
    }

    @Provides
    @Singleton
    fun provideMediaPlayer(
        @ApplicationContext context: Context,
        progressStateFlow: ProgressStateFlow,
        playerState: InMemoryCache<PlayerState>
    ): AudioPlayer {
        return AudioPlayer(context, progressStateFlow, playerState)
    }


    @Provides
    @Singleton
    fun providePlayerServiceConnection(
        @ApplicationContext context: Context
    ): PlayerServiceConnection {
        return PlayerServiceConnection(context)
    }
}