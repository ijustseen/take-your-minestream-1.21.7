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

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("minestream")
				.then(ClientCommandManager.literal("message")
					.then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
						.executes(context -> {
							MessageSpawner.setCurrentMessage(StringArgumentType.getString(context, "message"));
							if (MinecraftClient.getInstance().player != null) {
								MinecraftClient.getInstance().player.sendMessage(Text.of("Minestream message set to: " + MessageSpawner.getCurrentMessage()), false);
							}
							return 1;
						}))));
		});

		// Initialize the message spawner and renderer
		new MessageSpawner();
		new MessageRenderer();
	}
}