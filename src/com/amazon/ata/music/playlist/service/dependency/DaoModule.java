package com.amazon.ata.music.playlist.service.dependency;


import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.activity.CreatePlaylistActivity;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DaoModule {

//    ServiceComponent dagger = null;
//    CreatePlaylistActivity rootObjOne = dagger.provideCreatePlaylistActivity();
    @Singleton
    @Provides
    public DynamoDBMapper providePlaylistDao() {
        return new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_WEST_2));
    }
}
