package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import takeyourminestream.modid.ModConfig;

public class MessageSpawner {

    private static final List<MessageDisplay> activeMessages = new ArrayList<>();
    private static int tickCounter = 0;
    private static final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private static long lastMessageSpawnTime = 0;
    private static final int MIN_TICKS_BETWEEN_MESSAGES = 20; // Minimum ticks to wait between spawning messages (1 second at 20 ticks/sec)

    public MessageSpawner() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                tickCounter++; // Increment our custom tick counter

                // If there are messages in the queue and enough time has passed since the last spawn
                if (!messageQueue.isEmpty() && (tickCounter - lastMessageSpawnTime >= MIN_TICKS_BETWEEN_MESSAGES)) {
                    String messageToDisplay = messageQueue.poll(); // Get and remove the head of the queue

                    Random random = client.world.random;
                    Vec3d playerPos = client.player.getPos();

                    double angle = random.nextDouble() * 2 * Math.PI;
                    double radius = 2 + (5 - 2) * random.nextDouble(); // Random radius between 2 and 5
                    double xOffset = radius * Math.cos(angle);
                    double zOffset = radius * Math.sin(angle);
                    double yOffset = (random.nextDouble() * 2 - 1); // Random offset between -1 and 1

                    Vec3d spawnPos = new Vec3d(playerPos.x + xOffset, playerPos.y + client.player.getEyeHeight(client.player.getPose()) + yOffset, playerPos.z + zOffset);

                    activeMessages.add(new MessageDisplay(messageToDisplay, spawnPos, tickCounter, 0)); // Add the message from the queue
                    lastMessageSpawnTime = tickCounter; // Update last spawn time
                }
                
                // Обновляем замороженное время для всех сообщений
                if (ModConfig.ENABLE_FREEZING_ON_VIEW) {
                    for (MessageDisplay message : activeMessages) {
                        if (isPlayerLookingAtMessage(client, message.pos())) {
                            // Увеличиваем счетчик замороженных тиков
                            message.incrementFrozenTicks();
                        }
                    }
                }
                
                // Remove old messages с учетом замороженного времени
                if (ModConfig.ENABLE_FREEZING_ON_VIEW) {
                    activeMessages.removeIf(message -> {
                        int effectiveAge = getEffectiveAge(message);
                        return effectiveAge > (ModConfig.MESSAGE_LIFETIME_TICKS + ModConfig.MESSAGE_FADEOUT_TICKS);
                    });
                } else {
                    // Старая логика без заморозки
                    activeMessages.removeIf(message -> tickCounter - message.spawnTick > (ModConfig.MESSAGE_LIFETIME_TICKS + ModConfig.MESSAGE_FADEOUT_TICKS));
                }
            }
        });
    }
    
    /**
     * Вычисляет эффективный возраст сообщения с учетом замороженного времени
     * @param message сообщение для проверки
     * @return эффективный возраст сообщения в тиках
     */
    private static int getEffectiveAge(MessageDisplay message) {
        int baseAge = tickCounter - (int)message.spawnTick();
        // Вычитаем замороженные тики из базового возраста
        return Math.max(0, baseAge - message.frozenTicks());
    }
    
    /**
     * Проверяет, смотрит ли игрок на сообщение
     * @param client Minecraft клиент
     * @param messagePos позиция сообщения
     * @return true, если игрок смотрит на сообщение
     */
    private static boolean isPlayerLookingAtMessage(MinecraftClient client, Vec3d messagePos) {
        if (client.player == null) return false;
        
        // Получаем позицию глаз игрока
        Vec3d playerEyePos = client.player.getEyePos();
        
        // Получаем направление взгляда игрока
        Vec3d lookVec = client.player.getRotationVec(1.0f);
        
        // Вектор от глаз игрока к сообщению
        Vec3d toMessage = messagePos.subtract(playerEyePos);
        
        // Нормализуем вектор к сообщению
        double distanceToMessage = toMessage.length();
        if (distanceToMessage < 0.1) return false; // Слишком близко
        
        Vec3d toMessageNormalized = toMessage.normalize();
        
        // Вычисляем угол между направлением взгляда и направлением к сообщению
        double dotProduct = lookVec.dotProduct(toMessageNormalized);
        double angle = Math.acos(MathHelper.clamp(dotProduct, -1.0, 1.0));
        
        // Угол в радианах, в пределах которого игрок считается "смотрящим" на сообщение
        double viewAngle = Math.toRadians(ModConfig.VIEW_ANGLE_DEGREES);
        
        // Также проверяем расстояние - игрок должен быть достаточно близко
        double maxDistance = ModConfig.MAX_FREEZE_DISTANCE;
        
        return angle < viewAngle && distanceToMessage < maxDistance;
    }

    public static void setCurrentMessage(String message) {
        // Clear queue if empty message is set, implying stop command
        if (message.isEmpty()) {
            messageQueue.clear();
            activeMessages.clear(); // Clear currently displayed messages too
            tickCounter = 0; // Reset tick counter when stopping
        } else {
            messageQueue.offer(message); // Add message to the queue
        }
    }

    public static List<MessageDisplay> getActiveMessages() {
        return activeMessages;
    }

    public static int getTickCounter() {
        return tickCounter;
    }

    public static class MessageDisplay {
        private final String text;
        private final Vec3d pos;
        private final long spawnTick;
        private int frozenTicks;
        
        public MessageDisplay(String text, Vec3d pos, long spawnTick, int frozenTicks) {
            this.text = text;
            this.pos = pos;
            this.spawnTick = spawnTick;
            this.frozenTicks = frozenTicks;
        }
        
        public String text() { return text; }
        public Vec3d pos() { return pos; }
        public long spawnTick() { return spawnTick; }
        public int frozenTicks() { return frozenTicks; }
        
        public void incrementFrozenTicks() {
            this.frozenTicks++;
        }
    }
} 