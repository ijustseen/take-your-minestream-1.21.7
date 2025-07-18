package takeyourminestream.modid.messages;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import takeyourminestream.modid.ModConfig;

import java.util.List;

/**
 * Отвечает за определение, смотрит ли игрок на сообщение
 */
public class MessageViewDetector {
    
    private static final int PANEL_PADDING_X = 6;
    private static final int PANEL_PADDING_Y = 4;
    private static final float MESSAGE_SCALE = 0.025f;
    private static final float MAX_FALL = 20.0f; // То же значение, что и в MessageRenderer
    
    /**
     * Проверяет, смотрит ли игрок на сообщение
     * @param client Minecraft клиент
     * @param messagePosition позиция сообщения
     * @param messageYaw поворот сообщения по Y
     * @param messagePitch поворот сообщения по X
     * @param messageText текст сообщения для вычисления размеров
     * @param tickCounter текущий счетчик тиков
     * @param messageSpawnTick тик создания сообщения
     * @param frozenTicks количество замороженных тиков
     * @return true, если игрок смотрит на сообщение
     */
    public static boolean isPlayerLookingAtMessage(MinecraftClient client, Vec3d messagePosition, 
                                                  float messageYaw, float messagePitch, String messageText,
                                                  int tickCounter, long messageSpawnTick, int frozenTicks) {
        if (client.player == null) return false;
        
        // Вычисляем эффективный возраст сообщения (с учетом заморозки)
        int effectiveAge = tickCounter - (int)messageSpawnTick - frozenTicks;
        
        // Проверяем, не истек ли срок жизни сообщения
        if (effectiveAge >= ModConfig.MESSAGE_LIFETIME_TICKS + ModConfig.MESSAGE_FALL_TICKS) {
            return false; // Сообщение уже "разбилось"
        }
        
        // Вычисляем смещение при падении
        Vec3d adjustedPosition = calculateFallingPosition(messagePosition, effectiveAge, messageYaw, messagePitch);
        
        // Получаем позицию глаз игрока
        Vec3d playerEyePos = client.player.getEyePos();
        
        // Проверяем расстояние - игрок должен быть достаточно близко
        double distanceToMessage = playerEyePos.distanceTo(adjustedPosition);
        if (distanceToMessage > ModConfig.MAX_FREEZE_DISTANCE) return false;
        
        // Получаем направление взгляда игрока
        Vec3d lookVec = client.player.getRotationVec(1.0f);
        
        // Вычисляем размеры сообщения
        TextRenderer textRenderer = client.textRenderer;
        List<OrderedText> wrappedText = textRenderer.wrapLines(Text.of(messageText), 120);
        float totalTextHeight = wrappedText.size() * textRenderer.fontHeight;
        int textWidth = textRenderer.getWidth(wrappedText.get(0));
        int panelWidth = textWidth + PANEL_PADDING_X * 2;
        int panelHeight = (int)totalTextHeight + PANEL_PADDING_Y * 2;
        
        // Переводим размеры в блоки (с учетом масштаба)
        double messageWidthBlocks = panelWidth * MESSAGE_SCALE;
        double messageHeightBlocks = panelHeight * MESSAGE_SCALE;
        
        // Проверяем, пересекает ли луч взгляда плоскость сообщения
        return rayIntersectsMessagePlane(playerEyePos, lookVec, adjustedPosition, messageYaw, messagePitch, 
                                       messageWidthBlocks, messageHeightBlocks);
    }
    
    /**
     * Вычисляет позицию сообщения с учетом падения
     */
    public static Vec3d calculateFallingPosition(Vec3d basePosition, int effectiveAge, float yaw, float pitch) {
        int fallTicks = ModConfig.MESSAGE_FALL_TICKS;
        int fallStart = ModConfig.MESSAGE_LIFETIME_TICKS;
        int fallAge = effectiveAge - fallStart;
        
        if (fallAge < 0) {
            // Сообщение еще не падает
            return basePosition;
        }
        
        if (fallAge >= fallTicks) {
            // Сообщение уже "разбилось" - не должно быть видимым
            return basePosition;
        }
        
        // Вычисляем смещение при падении (та же логика, что и в MessageRenderer)
        float fallProgress = (float)fallAge / (float)fallTicks;
        fallProgress = Math.min(Math.max(fallProgress, 0.0f), 1.0f);
        float fallOffsetY = (fallProgress * fallProgress) * MAX_FALL;
        
        // Переводим смещение из пикселей в блоки (с учетом масштаба)
        float fallOffsetBlocks = fallOffsetY * MESSAGE_SCALE;
        
        // Применяем смещение в направлении падения (вниз по Y)
        return new Vec3d(basePosition.x, basePosition.y - fallOffsetBlocks, basePosition.z);
    }
    
    /**
     * Проверяет, пересекает ли луч взгляда плоскость сообщения
     */
    private static boolean rayIntersectsMessagePlane(Vec3d rayOrigin, Vec3d rayDirection, 
                                                    Vec3d planeCenter, float planeYaw, float planePitch,
                                                    double messageWidth, double messageHeight) {
        // Создаем нормаль плоскости сообщения (она всегда направлена к игроку)
        // Поворачиваем единичный вектор (0, 0, -1) на углы сообщения
        double yawRad = Math.toRadians(planeYaw);
        double pitchRad = Math.toRadians(planePitch);
        
        // Нормаль плоскости (направлена от центра сообщения к игроку)
        Vec3d planeNormal = new Vec3d(
            Math.sin(yawRad),
            -Math.sin(pitchRad),
            Math.cos(yawRad)
        ).normalize();
        
        // Вектор от начала луча к центру плоскости
        Vec3d toPlane = planeCenter.subtract(rayOrigin);
        
        // Вычисляем скалярное произведение направления луча и нормали плоскости
        double rayDotNormal = rayDirection.dotProduct(planeNormal);
        
        // Если луч параллелен плоскости, пересечения нет
        if (Math.abs(rayDotNormal) < 1e-6) return false;
        
        // Вычисляем расстояние от начала луча до точки пересечения с плоскостью
        double t = toPlane.dotProduct(planeNormal) / rayDotNormal;
        
        // Если пересечение происходит за лучом, игрок не смотрит на сообщение
        if (t < 0) return false;
        
        // Точка пересечения луча с плоскостью
        Vec3d intersectionPoint = rayOrigin.add(rayDirection.multiply(t));
        
        // Проверяем, находится ли точка пересечения в пределах размеров сообщения
        return isPointWithinMessageBounds(intersectionPoint, planeCenter, planeYaw, planePitch, 
                                        messageWidth, messageHeight);
    }
    
    /**
     * Проверяет, находится ли точка в пределах границ сообщения
     */
    private static boolean isPointWithinMessageBounds(Vec3d point, Vec3d messageCenter, 
                                                     float messageYaw, float messagePitch,
                                                     double messageWidth, double messageHeight) {
        // Переводим точку в локальные координаты сообщения
        Vec3d localPoint = point.subtract(messageCenter);
        
        // Поворачиваем точку обратно, чтобы получить координаты относительно плоскости сообщения
        double yawRad = Math.toRadians(-messageYaw);
        double pitchRad = Math.toRadians(-messagePitch);
        
        // Поворот по Yaw (вокруг оси Y)
        double x1 = localPoint.x * Math.cos(yawRad) - localPoint.z * Math.sin(yawRad);
        double y1 = localPoint.y;
        double z1 = localPoint.x * Math.sin(yawRad) + localPoint.z * Math.cos(yawRad);
        
        // Поворот по Pitch (вокруг оси X)
        double x2 = x1;
        double y2 = y1 * Math.cos(pitchRad) - z1 * Math.sin(pitchRad);
        double z2 = y1 * Math.sin(pitchRad) + z1 * Math.cos(pitchRad);
        
        // Проверяем, находится ли точка в пределах прямоугольника сообщения
        // Z координата должна быть близка к 0 (точка на плоскости)
        if (Math.abs(z2) > 0.5) return false; // Точка слишком далеко от плоскости
        
        // X и Y координаты должны быть в пределах размеров сообщения
        return Math.abs(x2) <= messageWidth / 2.0 && Math.abs(y2) <= messageHeight / 2.0;
    }
} 