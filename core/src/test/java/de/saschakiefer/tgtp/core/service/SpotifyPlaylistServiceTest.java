package de.saschakiefer.tgtp.core.service;

import de.saschakiefer.tgtp.core.TestConfig;
import de.saschakiefer.tgtp.core.exception.client.PlaylistCreationException;
import de.saschakiefer.tgtp.core.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.michaelthelin.spotify.model_objects.specification.Playlist;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
@ExtendWith(SpringExtension.class)
class SpotifyPlaylistServiceTest {
    @Autowired
    SpotifyPlaylistService playlistService;

    @MockBean
    ChatService chatService;

    @Test
    void createPlaylist_withResult_shouldReturnListOfSongs() {

        // Arrange
        when(chatService.addMessageToChatAndGetResponse(any())).thenReturn(new Message("assistant", getPlaylistResult()));

        // Act
        Playlist result = playlistService.createPlaylist(
                "All the songs Halloween played at their Pumpkins United Tour in Stuttgart",
                "Pumpkins United Tour");

        // Assert
        assertThat(result.getName(), is("Pumpkins United Tour"));
    }

    @Test
    void createPlaylist_withoutAProperResponse_shouldThrowException() {

        // Arrange
        String message = "I'm sorry, I cannot generate a playlist for the \"top ten Heavy Metal hits from 2022\" as it is currently 2023 and there is no data available for Heavy Metal hits from 2022.";
        when(chatService.addMessageToChatAndGetResponse(any())).thenReturn(new Message("assistant", message));
        when(chatService.getMessages()).thenReturn(List.of(new Message("assistant", message)));

        // Act + Assert
        assertThrows(PlaylistCreationException.class, () -> playlistService.createPlaylist(
                "Top ten Heavy Metal hits from 2022",
                "Metal Hits 2022"));
    }


    private String getPlaylistResult() {
        return """
                Helloween %%SEPARATOR%% Halloween %%SEPARATOR%% Keeper of the Seven Keys, Pt. 1
                Helloween %%SEPARATOR%% Dr. Stein %%SEPARATOR%% Keeper of the Seven Keys, Pt. 2
                Helloween %%SEPARATOR%% I'm Alive %%SEPARATOR%% Keeper of the Seven Keys, Pt. 2
                Helloween %%SEPARATOR%% Eagle Fly Free %%SEPARATOR%% Keeper of the Seven Keys, Pt. 2
                Helloween %%SEPARATOR%% Perfect Gentleman %%SEPARATOR%% The Time of the Oath
                Helloween %%SEPARATOR%% Forever and One (Neverland) %%SEPARATOR%% The Time of the Oath
                Helloween %%SEPARATOR%% A Tale That Wasn't Right %%SEPARATOR%% Keeper of the Seven Keys, Pt. 1
                Helloween %%SEPARATOR%% Pumpkins United %%SEPARATOR%% Pumpkins United
                Helloween %%SEPARATOR%% Power %%SEPARATOR%% The Time of the Oath
                Helloween %%SEPARATOR%% How Many Tears %%SEPARATOR%% Helloween
                Helloween %%SEPARATOR%% Invitation/ Eagle Fly Free (Reprise) %%SEPARATOR%% Keeper of the Seven Keys, Pt. 2
                Helloween %%SEPARATOR%% Keeper of the Seven Keys %%SEPARATOR%% Keeper of the Seven Keys, Pt. 2
                """;
    }
}
