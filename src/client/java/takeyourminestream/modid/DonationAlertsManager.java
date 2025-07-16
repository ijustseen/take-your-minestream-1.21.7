package takeyourminestream.modid;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import takeyourminestream.modid.messages.MessageSpawner;

public class DonationAlertsManager {
    private static DonationAlertsClient donationAlertsClient;
    private static boolean daConnected = false;
    private static MessageSpawner messageSpawner;

    public static void connect(MessageSpawner spawner) {
        messageSpawner = spawner;
        if (donationAlertsClient == null) {
            MinecraftClient.getInstance().player.sendMessage(Text.of("Подключение к Donation Alerts..."), false);
            donationAlertsClient = new DonationAlertsClient(messageSpawner);
            daConnected = true;
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Donation Alerts подключен!"), false);
            }
        } else {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Donation Alerts уже подключен."), false);
            }
        }
    }

    public static void disconnect() {
        if (donationAlertsClient != null) {
            donationAlertsClient.disconnect();
            donationAlertsClient = null;
            daConnected = false;
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Donation Alerts отключен."), false);
            }
        } else {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("Donation Alerts не был подключен."), false);
            }
        }
    }

    public static void onConfigChanged() {
        if (daConnected) {
            disconnect();
            connect(messageSpawner);
        }
    }

    public static boolean isConnected() {
        return daConnected;
    }
} 