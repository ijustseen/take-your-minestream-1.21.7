package takeyourminestream.modid;

public class ModConfig {
    // Имя Twitch-канала
    public static final String TWITCH_CHANNEL_NAME = "barsigold";

    // Длительность жизни сообщения (в тиках)
    public static final int MESSAGE_LIFETIME_TICKS = 80; // 3 секунды при 20 тиках/сек

    // Длительность фейда (плавного исчезновения) сообщения после истечения срока жизни (в тиках)
    public static final int MESSAGE_FADEOUT_TICKS = 20; // 2 секунды при 20 тиках/сек

    // Массив цветов для ников
    public static final String[] NICK_COLORS = {"§c", "§9", "§a", "§5"}; // Красный, Синий, Зеленый, Фиолетовый
    
    // Не давать сообщению пропадать пока на него смотрит игрок.
    public static final boolean ENABLE_FREEZING_ON_VIEW = true;
    
    // Угол обзора для определения, смотрит ли игрок на сообщение (в градусах)
    public static final double VIEW_ANGLE_DEGREES = 16.0; // Точный угол обзора 10 градусов
    
    // Максимальное расстояние для заморозки сообщения (в блоках)
    public static final double MAX_FREEZE_DISTANCE = 15.0; // Увеличено с 10 до 15 блоков
} 