package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeChangeException;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.UpdatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.UpdatePlaylistResult;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the UpdatePlaylistActivity for the MusicPlaylistService's UpdatePlaylist API.
 *
 * This API allows the customer to update their saved playlist's information.
 */
public class UpdatePlaylistActivity implements RequestHandler<UpdatePlaylistRequest, UpdatePlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new UpdatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    @Inject
    public UpdatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    /**
     * This method handles the incoming request by retrieving the playlist, updating it,
     * and persisting the playlist.
     * <p>
     * It then returns the updated playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     * <p>
     * If the request tries to update the customer ID,
     * this should throw an InvalidAttributeChangeException
     *
     * @param updatePlaylistRequest request object containing the playlist ID, playlist name, and customer ID
     *                              associated with it
     * @return updatePlaylistResult result object containing the API defined {@link PlaylistModel}
     */
    @Override
    public UpdatePlaylistResult handleRequest(final UpdatePlaylistRequest updatePlaylistRequest, Context context) {
        log.info("Received UpdatePlaylistRequest {}", updatePlaylistRequest);

        boolean isValidName = MusicPlaylistServiceUtils.isValidString(updatePlaylistRequest.getName());

        if (!isValidName) {
            throw new InvalidAttributeValueException();
        }


        Playlist playlist = new Playlist();
        try {
        playlist = playlistDao.getPlaylist(updatePlaylistRequest.getId());
        } catch (NullPointerException e) {
            throw new PlaylistNotFoundException();
        }

        // Validate playlist customer ID matches Request
        // if the request customer ID and the playlist customer ID are diff
        // throw InvalidAttributeChangeException

        if (!playlist.getCustomerId().equals(updatePlaylistRequest.getCustomerId())) {
            throw new InvalidAttributeChangeException();
        }

        // Update playlist

        // Save Playlist -> Return Updated Playlist object

        playlist = playlistDao.savePlaylist(playlist);
        // create playlistModel
        ModelConverter modelConverter = new ModelConverter();

        PlaylistModel playlistModel = modelConverter.toPlaylistModel(playlist);
        // Set playlistModel with saved playlist data

        playlistModel.setId(updatePlaylistRequest.getId());
        playlistModel.setName(updatePlaylistRequest.getName());
        playlistModel.setCustomerId(updatePlaylistRequest.getCustomerId());
//        playlistModel.setSongCount(updatePlaylistRequest.getSongCount());
//        playlistModel.setTags(updatePlaylistRequest.getTags());

        // create UpdatedPlaylistResponse
        // set PlaylistModel field in UpdatedPlaylistResponse


        return UpdatePlaylistResult.builder()
                .withPlaylist(playlistModel)
                .build();
    }
}
