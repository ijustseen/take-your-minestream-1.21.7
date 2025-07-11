package takeyourminestream.modid;

public class ModConfig {
    // Имя Twitch-канала
    public static final String TWITCH_CHANNEL_NAME = "ijustseen_you";

    // Длительность жизни сообщения (в тиках)
    public static final int MESSAGE_LIFETIME_TICKS = 80; // 3 секунды при 20 тиках/сек

    // Длительность фейда (плавного исчезновения) сообщения после истечения срока жизни (в тиках)
    public static final int MESSAGE_FADEOUT_TICKS = 20; // 2 секунды при 20 тиках/сек

    // Массив цветов для ников
    public static final String[] NICK_COLORS = {"§c", "§9", "§a", "§5"}; // Красный, Синий, Зеленый, Фиолетовый
} 