package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class MessageSpawner {

    private static final List<MessageDisplay> activeMessages = new ArrayList<>();
    private static int tickCounter = 0;
    private static String currentMessage = "";

    public MessageSpawner() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                tickCounter++; // Increment our custom tick counter

                // Spawn a new message every 8 ticks if a message is set
                if (!currentMessage.isEmpty() && tickCounter % 8 == 0) {
                    Random random = client.world.random;
                    Vec3d playerPos = client.player.getPos();

                    double angle = random.nextDouble() * 2 * Math.PI;
                    double radius = 2 + (5 - 2) * random.nextDouble(); // Random radius between 2 and 5
                    double xOffset = radius * Math.cos(angle);
                    double zOffset = radius * Math.sin(angle);
                    double yOffset = (random.nextDouble() * 2 - 1); // Random offset between -1 and 1

                    Vec3d spawnPos = new Vec3d(playerPos.x + xOffset, playerPos.y + client.player.getEyeHeight(client.player.getPose()) + yOffset, playerPos.z + zOffset);

                    activeMessages.add(new MessageDisplay(currentMessage, spawnPos, tickCounter)); // Use our custom tickCounter
                }
                // Remove old messages (e.g., after 60 ticks or 3 seconds)
                activeMessages.removeIf(message -> tickCounter - message.spawnTick > 60);
            }
        });
    }

    public static void setCurrentMessage(String message) {
        currentMessage = message;
        tickCounter = 0; // Reset tickCounter to ensure message spawns soon after command
    }

    public static String getCurrentMessage() {
        return currentMessage;
    }

    public static List<MessageDisplay> getActiveMessages() {
        return activeMessages;
    }

    public record MessageDisplay(String text, Vec3d pos, long spawnTick) {}
} 