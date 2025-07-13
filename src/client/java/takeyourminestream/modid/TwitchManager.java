package takeyourminestream.modid;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import takeyourminestream.modid.messages.MessageSpawner;

public class TwitchManager {
    private static TwitchChatClient twitchChatClient;
    private static String lastTwitchChannelName = ModConfig.TWITCH_CHANNEL_NAME;
    private static boolean twitchConnected = false;
    private static MessageSpawner messageSpawner;

    public static void connect(MessageSpawner spawner) {
        messageSpawner = spawner;
        if (twitchChatClient == null) {
            MinecraftClient.getInstance().player.sendMessage(Text.of("Connecting to Twitch chat '" + ModConfig.TWITCH_CHANNEL_NAME + "'..."), false);
            twitchChatClient = new TwitchChatClient(ModConfig.TWITCH_CHANNEL_NAME, messageSpawner);
            twitchConnected = true;
            lastTwitchChannelName = ModConfig.TWITCH_CHANNEL_NAME;
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Successfully connected!"), false);
            }
        } else {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Already connected to Twitch chat."), false);
            }
        }
    }

    public static void disconnect() {
        if (twitchChatClient != null) {
            twitchChatClient.disconnect();
            twitchChatClient = null;
            twitchConnected = false;
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Disconnected from Twitch chat."), false);
            }
        } else {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Not connected to Twitch chat."), false);
            }
        }
    }

    public static void onChannelNameChanged(String newChannelName) {
        if (twitchConnected && !newChannelName.equals(lastTwitchChannelName)) {
            // Переподключаемся к новому каналу
            if (twitchChatClient != null) {
                twitchChatClient.disconnect();
            }
            twitchChatClient = new TwitchChatClient(newChannelName, messageSpawner);
            lastTwitchChannelName = newChannelName;
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Переподключено к Twitch-каналу: " + newChannelName), false);
            }
        }
        lastTwitchChannelName = newChannelName;
    }

    public static boolean isConnected() {
        return twitchConnected;
    }
} 