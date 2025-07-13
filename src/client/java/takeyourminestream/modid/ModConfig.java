package takeyourminestream.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.fabricmc.loader.api.FabricLoader;

public class ModConfig {
    // Имя Twitch-канала
    public static String TWITCH_CHANNEL_NAME = "ijustseen_you";

    // Длительность жизни сообщения (в тиках)
    public static int MESSAGE_LIFETIME_TICKS = 80; // 3 секунды при 20 тиках/сек

    // Длительность падения сообщения после истечения срока жизни (в тиках)
    public static int MESSAGE_FALL_TICKS = 20; // 1 секунда при 20 тиках/сек

    // Массив цветов для ников
    public static String[] NICK_COLORS = {"§c", "§9", "§a", "§5"}; // Красный, Синий, Зеленый, Фиолетовый
    
    // Не давать сообщению пропадать пока на него смотрит игрок.
    public static boolean ENABLE_FREEZING_ON_VIEW = true;
    
    // Угол обзора для определения, смотрит ли игрок на сообщение (в градусах)
    public static double VIEW_ANGLE_DEGREES = 16.0; // Точный угол обзора 10 градусов
    
    // Максимальное расстояние для заморозки сообщения (в блоках)
    public static double MAX_FREEZE_DISTANCE = 15.0; // Увеличено с 10 до 15 блоков

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "take-your-minestream.json");

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfigData data = GSON.fromJson(reader, ModConfigData.class);
                if (data != null) {
                    TWITCH_CHANNEL_NAME = data.twitchChannelName;
                    MESSAGE_LIFETIME_TICKS = data.messageLifetimeTicks;
                    MESSAGE_FALL_TICKS = data.messageFallTicks;
                    ENABLE_FREEZING_ON_VIEW = data.enableFreezingOnView;
                    VIEW_ANGLE_DEGREES = data.viewAngleDegrees;
                    MAX_FREEZE_DISTANCE = data.maxFreezeDistance;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig() {
        ModConfigData data = new ModConfigData();
        data.twitchChannelName = TWITCH_CHANNEL_NAME;
        data.messageLifetimeTicks = MESSAGE_LIFETIME_TICKS;
        data.messageFallTicks = MESSAGE_FALL_TICKS;
        data.enableFreezingOnView = ENABLE_FREEZING_ON_VIEW;
        data.viewAngleDegrees = VIEW_ANGLE_DEGREES;
        data.maxFreezeDistance = MAX_FREEZE_DISTANCE;
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
        public double viewAngleDegrees;
        public double maxFreezeDistance;
    }
} 