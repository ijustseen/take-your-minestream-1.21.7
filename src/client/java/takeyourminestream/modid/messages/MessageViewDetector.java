package takeyourminestream.modid.messages;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import takeyourminestream.modid.ModConfig;

/**
 * Отвечает за определение, смотрит ли игрок на сообщение
 */
public class MessageViewDetector {
    
    /**
     * Проверяет, смотрит ли игрок на сообщение
     * @param client Minecraft клиент
     * @param messagePosition позиция сообщения
     * @return true, если игрок смотрит на сообщение
     */
    public static boolean isPlayerLookingAtMessage(MinecraftClient client, Vec3d messagePosition) {
        if (client.player == null) return false;
        
        // Получаем позицию глаз игрока
        Vec3d playerEyePos = client.player.getEyePos();
        
        // Получаем направление взгляда игрока
        Vec3d lookVec = client.player.getRotationVec(1.0f);
        
        // Вектор от глаз игрока к сообщению
        Vec3d toMessage = messagePosition.subtract(playerEyePos);
        
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
} 