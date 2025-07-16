package takeyourminestream.modid;

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
    
    // Максимальное расстояние для заморозки сообщения (в блоках)
    public static double MAX_FREEZE_DISTANCE = 15.0; // Увеличено с 10 до 15 блоков

    // Спавнить сообщения только в зоне видимости игрока (true) или вокруг (false)
    public static boolean MESSAGES_IN_FRONT_OF_PLAYER_ONLY = false;

    // === Donation Alerts ===
    public static boolean DONATIONALERTS_ENABLED = false;
    public static String DONATIONALERTS_WIDGET_URL = ""; // Полная ссылка на виджет
    public static String DONATIONALERTS_GROUP_ID = "";
    public static String DONATIONALERTS_TOKEN = "";
} 