package takeyourminestream.modid;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;
import takeyourminestream.modid.messages.MessageSpawner;
import takeyourminestream.modid.messages.MessageRenderer;

public class TakeYourMineStreamClient implements ClientModInitializer {

	private static TwitchChatClient twitchChatClient;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("minestream")
				.then(ClientCommandManager.literal("test")
					.then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
						.executes(context -> {
							String message = StringArgumentType.getString(context, "message");
							MessageSpawner.setCurrentMessage(message);
							if (MinecraftClient.getInstance().player != null) {
								MinecraftClient.getInstance().player.sendMessage(Text.of("Minestream message set to: " + message), false);
							}
							return 1;
						})))
				.then(ClientCommandManager.literal("stop")
					.executes(context -> {
						MessageSpawner.setCurrentMessage("");
						 if (MinecraftClient.getInstance().player != null) {
							 MinecraftClient.getInstance().player.sendMessage(Text.of("Minestream message stopped."), false);
						 }
						 if (twitchChatClient != null) {
                            twitchChatClient.disconnect();
                            twitchChatClient = null;
                        }
						return 1;
					}))
				.then(ClientCommandManager.literal("twitch")
					.then(ClientCommandManager.literal("start")
						.executes(context -> {
							if (twitchChatClient == null) {
								MinecraftClient.getInstance().player.sendMessage(Text.of("Connecting to Twitch chat 'ijustseen_you'..."), false);
								twitchChatClient = new TwitchChatClient("ijustseen_you");
								if (MinecraftClient.getInstance().player != null) {
									MinecraftClient.getInstance().player.sendMessage(Text.of("Successfully connected!"), false);
								}
							} else {
								if (MinecraftClient.getInstance().player != null) {
									MinecraftClient.getInstance().player.sendMessage(Text.of("Already connected to Twitch chat."), false);
								}
							}
							return 1;
						}))
					.then(ClientCommandManager.literal("stop")
						.executes(context -> {
							if (twitchChatClient != null) {
								twitchChatClient.disconnect();
								twitchChatClient = null;
								if (MinecraftClient.getInstance().player != null) {
									MinecraftClient.getInstance().player.sendMessage(Text.of("Disconnected from Twitch chat."), false);
								}
							} else {
								if (MinecraftClient.getInstance().player != null) {
									MinecraftClient.getInstance().player.sendMessage(Text.of("Not connected to Twitch chat."), false);
								}
							}
							return 1;
						})))
			);
		});

		// Initialize the message spawner and renderer
		new MessageSpawner();
		new MessageRenderer();

		// Initialize TwitchChatClient on mod start for the specified channel
		twitchChatClient = new TwitchChatClient("ijustseen_you");
	}
}