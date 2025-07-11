package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

                    activeMessages.add(new MessageDisplay(messageToDisplay, spawnPos, tickCounter)); // Add the message from the queue
                    lastMessageSpawnTime = tickCounter; // Update last spawn time
                }
                // Remove old messages (e.g., after 60 ticks or 3 seconds)
                activeMessages.removeIf(message -> tickCounter - message.spawnTick > 60);
            }
        });
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

    public record MessageDisplay(String text, Vec3d pos, long spawnTick) {}
} 