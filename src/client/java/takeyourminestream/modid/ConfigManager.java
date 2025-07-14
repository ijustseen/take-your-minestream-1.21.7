package takeyourminestream.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "take-your-minestream.json");

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfigData data = GSON.fromJson(reader, ModConfigData.class);
                if (data != null) {
                    ModConfig.TWITCH_CHANNEL_NAME = data.twitchChannelName;
                    ModConfig.MESSAGE_LIFETIME_TICKS = data.messageLifetimeTicks;
                    ModConfig.MESSAGE_FALL_TICKS = data.messageFallTicks;
                    ModConfig.ENABLE_FREEZING_ON_VIEW = data.enableFreezingOnView;
                    ModConfig.MAX_FREEZE_DISTANCE = data.maxFreezeDistance;
                    ModConfig.MESSAGES_IN_FRONT_OF_PLAYER_ONLY = data.messagesInFrontOfPlayerOnly;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig() {
        ModConfigData data = new ModConfigData();
        data.twitchChannelName = ModConfig.TWITCH_CHANNEL_NAME;
        data.messageLifetimeTicks = ModConfig.MESSAGE_LIFETIME_TICKS;
        data.messageFallTicks = ModConfig.MESSAGE_FALL_TICKS;
        data.enableFreezingOnView = ModConfig.ENABLE_FREEZING_ON_VIEW;
        data.maxFreezeDistance = ModConfig.MAX_FREEZE_DISTANCE;
        data.messagesInFrontOfPlayerOnly = ModConfig.MESSAGES_IN_FRONT_OF_PLAYER_ONLY;
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ModConfigData {
        public String twitchChannelName;
        public int messageLifetimeTicks;
        public int messageFallTicks;
        public boolean enableFreezingOnView;
        public double maxFreezeDistance;
        public boolean messagesInFrontOfPlayerOnly;
    }
} 