package takeyourminestream.modid.messages;

import net.minecraft.util.math.Vec3d;

/**
 * Модель данных для отображаемого сообщения
 */
public class Message {
    private final String text;
    private final Vec3d position;
    private final long spawnTick;
    private int frozenTicks;
    private final float yaw;
    private final float pitch;
    private final Integer authorColorRgb; // null если неизвестен
    
    public Message(String text, Vec3d position, long spawnTick, float yaw, float pitch) {
        this(text, position, spawnTick, yaw, pitch, null);
    }

    public Message(String text, Vec3d position, long spawnTick, float yaw, float pitch, Integer authorColorRgb) {
        this.text = text;
        this.position = position;
        this.spawnTick = spawnTick;
        this.frozenTicks = 0;
        this.yaw = yaw;
        this.pitch = pitch;
        this.authorColorRgb = authorColorRgb;
    }
    
    public String getText() { 
        return text; 
    }
    
    public Vec3d getPosition() { 
        return position; 
    }
    
    public long getSpawnTick() { 
        return spawnTick; 
    }
    
    public int getFrozenTicks() { 
        return frozenTicks; 
    }
    
    public void incrementFrozenTicks() {
        this.frozenTicks++;
    }
    
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public Integer getAuthorColorRgb() { return authorColorRgb; }
    
    /**
     * Вычисляет эффективный возраст сообщения с учетом замороженного времени
     * @param currentTick текущий тик
     * @return эффективный возраст сообщения в тиках
     */
    public int getEffectiveAge(int currentTick) {
        int baseAge = currentTick - (int)spawnTick;
        return Math.max(0, baseAge - frozenTicks);
    }
} 