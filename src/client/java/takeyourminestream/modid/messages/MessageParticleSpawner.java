package takeyourminestream.modid.messages;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Vec3d;
import takeyourminestream.modid.ModConfig;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageParticleSpawner {
    public static void spawnParticlesForMessage(Message message, MessageParticleManager manager, MinecraftClient client, Vec3d spawnPos) {
        if (client == null || client.textRenderer == null) return;
        Random random = new Random();
        int min = ModConfig.getPARTICLE_MIN_COUNT();
        int max = ModConfig.getPARTICLE_MAX_COUNT();
        int count = min + random.nextInt(max - min + 1);
        int lifetime = ModConfig.getPARTICLE_LIFETIME_TICKS();
        float size = 2.5f + random.nextFloat() * 2.5f;

        // Получаем размеры панели сообщения
        TextRenderer textRenderer = client.textRenderer;
        List<OrderedText> wrappedText = textRenderer.wrapLines(Text.of(message.getText()), 120);
        float totalTextHeight = wrappedText.size() * textRenderer.fontHeight;
        int textWidth = textRenderer.getWidth(wrappedText.get(0));
        int panelWidth = textWidth + 6 * 2; // PANEL_PADDING_X
        int panelHeight = (int)totalTextHeight + 4 * 2; // PANEL_PADDING_Y

        // Центр панели в мировых координатах
        float yaw = message.getYaw();
        float pitch = message.getPitch();
        double scale = 0.025;

        List<MessageParticle> particles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // Случайная точка внутри панели (в пикселях)
            float px = random.nextFloat() * panelWidth - panelWidth / 2f;
            float py = random.nextFloat() * panelHeight - panelHeight / 2f;
            // Переводим в мировые координаты (с учётом yaw/pitch)
            Vec3d local = new Vec3d(px * scale, py * scale, 0);
            Vec3d world = rotateVec(local, yaw, pitch).add(spawnPos);

            // Случайная скорость: вниз + заметно в стороны
            double vx = (random.nextDouble() - 0.5) * 0.08;
            double vy = -0.07 - random.nextDouble() * 0.06;
            double vz = (random.nextDouble() - 0.5) * 0.08;
            Vec3d velocity = new Vec3d(vx, vy, vz);

            // Размер: от 2.0 до 5.0
            float particleSize = 2.0f + random.nextFloat() * 3.0f;
            // Вращение: случайный стартовый угол и скорость
            float rotation = random.nextFloat() * 360f;
            float rotationSpeed = (random.nextFloat() - 0.5f) * 8f; // -4..+4 градусов за тик

            // Цвет: половина партиклов — цвет текста, половина — цвет фона
            MessageParticle.ParticleType type = (i % 2 == 0) ? MessageParticle.ParticleType.TEXT_COLOR : MessageParticle.ParticleType.BACKGROUND_COLOR;
            Color color = (type == MessageParticle.ParticleType.TEXT_COLOR) ? getTextColor() : getPanelColor();

            particles.add(new MessageParticle(world, velocity, color, particleSize, lifetime, type, rotation, rotationSpeed, yaw, pitch));
        }
        manager.addParticles(particles);
    }

    // Вращение вектора по yaw/pitch (градусы)
    private static Vec3d rotateVec(Vec3d vec, float yaw, float pitch) {
        double yawRad = Math.toRadians(-yaw);
        double pitchRad = Math.toRadians(pitch);
        // Yaw (вокруг Y)
        double x1 = vec.x * Math.cos(yawRad) - vec.z * Math.sin(yawRad);
        double z1 = vec.x * Math.sin(yawRad) + vec.z * Math.cos(yawRad);
        // Pitch (вокруг X)
        double y2 = vec.y * Math.cos(pitchRad) - z1 * Math.sin(pitchRad);
        double z2 = vec.y * Math.sin(pitchRad) + z1 * Math.cos(pitchRad);
        return new Vec3d(x1, y2, z2);
    }

    // Цвет текста (белый, либо можно доработать для поддержки цветных сообщений)
    private static Color getTextColor() {
        return new Color(255, 255, 255);
    }

    // Цвет панели (пример: средний цвет из текстуры или фиксированный)
    private static Color getPanelColor() {
        return new Color(44, 44, 56); // Пример: тёмно-серый, можно заменить на другой
    }
} 