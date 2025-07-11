package takeyourminestream.modid;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import takeyourminestream.modid.messages.MessageSpawner;
import java.util.Random;
import takeyourminestream.modid.ModConfig;

public class TwitchChatClient {

    private TwitchClient twitchClient;
    private final Random random = new Random();

    public TwitchChatClient(String channelName) {
        initializeTwitchClient();
    }

    private String getRandomColor() {
        return ModConfig.NICK_COLORS[random.nextInt(ModConfig.NICK_COLORS.length)];
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
            System.out.println("[" + event.getChannel().getName() + "] " + user + ": " + message);
            // Here we will pass the message to MessageSpawner
            // For now, we are just printing it to console for verification.
            MessageSpawner.setCurrentMessage(getRandomColor() + user + ": §r" + message);
        });

        twitchClient.getChat().joinChannel(ModConfig.TWITCH_CHANNEL_NAME);
        System.out.println("Joined Twitch channel: " + ModConfig.TWITCH_CHANNEL_NAME);
    }

    public void disconnect() {
        if (twitchClient != null) {
            twitchClient.close();
            System.out.println("Disconnected from Twitch chat.");
        }
    }
} 