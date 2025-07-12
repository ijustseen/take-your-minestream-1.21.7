package takeyourminestream.modid.messages;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

/**
 * Отвечает за позиционирование сообщений в мире
 */
public class MessagePosition {
    
    /**
     * Генерирует случайную позицию вокруг игрока для отображения сообщения
     * @param client Minecraft клиент
     * @return позиция для сообщения
     */
    public static Vec3d generateRandomPosition(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return Vec3d.ZERO;
        }
        
        Random random = client.world.random;
        Vec3d playerPos = client.player.getPos();

        // Генерируем случайный угол и радиус
        double angle = random.nextDouble() * 2 * Math.PI;
        double radius = 2 + (5 - 2) * random.nextDouble(); // Случайный радиус между 2 и 5
        double xOffset = radius * Math.cos(angle);
        double zOffset = radius * Math.sin(angle);
        double yOffset = (random.nextDouble() * 2 - 1); // Случайное смещение по Y между -1 и 1

        return new Vec3d(
            playerPos.x + xOffset, 
            playerPos.y + client.player.getEyeHeight(client.player.getPose()) + yOffset, 
            playerPos.z + zOffset
        );
    }
} 