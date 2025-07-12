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
    
    public Message(String text, Vec3d position, long spawnTick) {
        this.text = text;
        this.position = position;
        this.spawnTick = spawnTick;
        this.frozenTicks = 0;
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