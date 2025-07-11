package takeyourminestream.modid;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import takeyourminestream.modid.messages.MessageSpawner;

public class TwitchChatClient {

    private TwitchClient twitchClient;
    private String twitchChannelName;

    public TwitchChatClient(String channelName) {
        this.twitchChannelName = channelName;
        initializeTwitchClient();
    }

    private void initializeTwitchClient() {
        // For public chat reading, we generally don't need a client secret or access token
        // if we are just joining a channel anonymously to read messages.
        // However, if we need to send messages or use other Helix API features, we'd need proper auth.
        this.twitchClient = TwitchClientBuilder.builder()
            .withEnableChat(true)
            .build();

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            String message = event.getMessage();
            String user = event.getUser().getName();
            System.out.println(String.format("[%s] %s: %s", event.getChannel().getName(), user, message));
            // Here we will pass the message to MessageSpawner
            // For now, let's just print it to console to ensure it works.
            MessageSpawner.setCurrentMessage(user + ": " + message);
        });

        twitchClient.getChat().joinChannel(twitchChannelName);
        System.out.println("Joined Twitch channel: " + twitchChannelName);
    }

    public void disconnect() {
        if (twitchClient != null) {
            twitchClient.close();
            System.out.println("Disconnected from Twitch chat.");
        }
    }
} 