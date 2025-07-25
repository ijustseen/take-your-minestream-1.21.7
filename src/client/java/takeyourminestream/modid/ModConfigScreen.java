package takeyourminestream.modid;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.jetbrains.annotations.Nullable;
import takeyourminestream.modid.config.MessageSpawnMode;

public class ModConfigScreen extends Screen {
    private TextFieldWidget channelNameField;
    private TextFieldWidget messageLifetimeField;
    private TextFieldWidget messageFallField;
    private TextFieldWidget maxFreezeDistanceField;
    private ButtonWidget freezingToggleButton;
    private ButtonWidget inFrontOnlyToggleButton;
    private ButtonWidget automoderationToggleButton;
    private final @Nullable Screen parent;
    private String initialChannelName;

    public ModConfigScreen() {
        this(null);
    }

    public ModConfigScreen(@Nullable Screen parent) {
        super(Text.translatable("takeyourminestream.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 4 - 20; // чуть выше
        int labelWidth = 120;
        int fieldWidth = 100;
        int fieldHeight = 18;
        int spacing = 24;
        TextRenderer textRenderer = this.textRenderer;

        initialChannelName = ModConfig.getTWITCH_CHANNEL_NAME();

        // Поле для имени канала
        channelNameField = new TextFieldWidget(textRenderer, centerX + 10, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.channel_name"));
        channelNameField.setText(ModConfig.getTWITCH_CHANNEL_NAME());
        channelNameField.setChangedListener(s -> ConfigManager.getInstance().setConfigValue("twitchChannelName", s));
        this.addDrawableChild(channelNameField);
        y += spacing;

        // Поле для времени жизни сообщения
        messageLifetimeField = new TextFieldWidget(textRenderer, centerX + 10, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.message_lifetime"));
        messageLifetimeField.setText(String.valueOf(ModConfig.getMESSAGE_LIFETIME_TICKS()));
        messageLifetimeField.setChangedListener(s -> {
            try { ConfigManager.getInstance().setConfigValue("messageLifetimeTicks", Integer.parseInt(s)); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(messageLifetimeField);
        y += spacing;

        // Поле для времени падения
        messageFallField = new TextFieldWidget(textRenderer, centerX + 10, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.message_fall"));
        messageFallField.setText(String.valueOf(ModConfig.getMESSAGE_FALL_TICKS()));
        messageFallField.setChangedListener(s -> {
            try { ConfigManager.getInstance().setConfigValue("messageFallTicks", Integer.parseInt(s)); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(messageFallField);
        y += spacing;

        // Кнопка для переключения ENABLE_FREEZING_ON_VIEW
        freezingToggleButton = ButtonWidget.builder(
            Text.translatable(ModConfig.isENABLE_FREEZING_ON_VIEW() ? "takeyourminestream.config.on" : "takeyourminestream.config.off"),
            btn -> {
                ModConfig.setENABLE_FREEZING_ON_VIEW(!ModConfig.isENABLE_FREEZING_ON_VIEW());
                btn.setMessage(Text.translatable(ModConfig.isENABLE_FREEZING_ON_VIEW() ? "takeyourminestream.config.on" : "takeyourminestream.config.off"));
            }
        ).dimensions(centerX + 10, y, fieldWidth, fieldHeight).build();
        this.addDrawableChild(freezingToggleButton);
        y += spacing;

        // Кнопка для переключения режима спавна сообщений
        inFrontOnlyToggleButton = ButtonWidget.builder(
            getSpawnModeButtonText(),
            btn -> {
                var currentMode = ModConfig.getMESSAGE_SPAWN_MODE();
                var nextMode = currentMode.next();
                ModConfig.setMESSAGE_SPAWN_MODE(nextMode);
                btn.setMessage(getSpawnModeButtonText());
            }
        ).dimensions(centerX + 10, y, fieldWidth, fieldHeight).build();
        this.addDrawableChild(inFrontOnlyToggleButton);
        y += spacing;

        // Поле для максимальной дистанции заморозки
        maxFreezeDistanceField = new TextFieldWidget(textRenderer, centerX + 10, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.max_freeze_distance"));
        maxFreezeDistanceField.setText(String.valueOf(ModConfig.getMAX_FREEZE_DISTANCE()));
        maxFreezeDistanceField.setChangedListener(s -> {
            try { ConfigManager.getInstance().setConfigValue("maxFreezeDistance", Double.parseDouble(s)); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(maxFreezeDistanceField);
        y += spacing + 6;

        // Кнопка для переключения ENABLE_AUTOMODERATION
        automoderationToggleButton = ButtonWidget.builder(
            Text.translatable(ModConfig.isENABLE_AUTOMODERATION() ? "takeyourminestream.config.on" : "takeyourminestream.config.off"),
            btn -> {
                ModConfig.setENABLE_AUTOMODERATION(!ModConfig.isENABLE_AUTOMODERATION());
                btn.setMessage(Text.translatable(ModConfig.isENABLE_AUTOMODERATION() ? "takeyourminestream.config.on" : "takeyourminestream.config.off"));
            }
        ).dimensions(centerX + 10, y, fieldWidth, fieldHeight).build();
        this.addDrawableChild(automoderationToggleButton);
        y += spacing;

        // Кнопка "Сохранить и выйти"
        int saveButtonWidth = 160;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("takeyourminestream.config.save_and_exit"), btn -> {
            if (!ModConfig.getTWITCH_CHANNEL_NAME().equals(initialChannelName)) {
                TwitchManager.getInstance(ConfigManager.getInstance()).onChannelNameChanged(ModConfig.getTWITCH_CHANNEL_NAME());
            }
            ConfigManager.getInstance().saveConfig();
            this.close();
        }).dimensions(centerX - saveButtonWidth / 2, y, saveButtonWidth, fieldHeight).build());
    }

    private String getFreezingButtonText() {
        return "Заморозка при взгляде: " + (ModConfig.isENABLE_FREEZING_ON_VIEW() ? "ВКЛ" : "ВЫКЛ");
    }
    
    private Text getSpawnModeButtonText() {
        var mode = ModConfig.getMESSAGE_SPAWN_MODE();
        switch (mode) {
            case AROUND_PLAYER:
                return Text.translatable("takeyourminestream.config.around_player");
            case FRONT_OF_PLAYER:
                return Text.translatable("takeyourminestream.config.fop_only");
            case HUD_WIDGET:
                return Text.translatable("takeyourminestream.config.hud_widget");
            default:
                return Text.translatable("takeyourminestream.config.around_player");
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        int y = this.height / 4 - 48;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, y, 0xFFFFFF);

        // Лейблы слева от каждого поля, длинные разбиты на две строки
        int labelColor = 0xFFFFFFFF;
        int fontHeight = this.textRenderer.fontHeight;
        int fieldHeight = 18;
        int labelOffsetX = 10;
        int labelWidth = 120;
        int x1 = channelNameField.getX() - labelWidth - labelOffsetX;
        int x2 = messageLifetimeField.getX() - labelWidth - labelOffsetX;
        int x3 = messageFallField.getX() - labelWidth - labelOffsetX;
        int x4 = freezingToggleButton.getX() - labelWidth - labelOffsetX;
        int x5 = maxFreezeDistanceField.getX() - labelWidth - labelOffsetX;
        int x6 = inFrontOnlyToggleButton.getX() - labelWidth - labelOffsetX;
        int x7 = automoderationToggleButton.getX() - labelWidth - labelOffsetX;
        int y1 = channelNameField.getY() + (fieldHeight - fontHeight) / 2;
        int y2 = messageLifetimeField.getY() + (fieldHeight - fontHeight) / 2;
        int y3 = messageFallField.getY() + (fieldHeight - fontHeight) / 2;
        int y4 = freezingToggleButton.getY() + (fieldHeight - fontHeight) / 2;
        int y5 = maxFreezeDistanceField.getY() + (fieldHeight - fontHeight) / 2;
        int y6 = inFrontOnlyToggleButton.getY() + (fieldHeight - fontHeight) / 2;
        int y7 = automoderationToggleButton.getY() + (fieldHeight - fontHeight) / 2;
        // Имя Twitch-канала
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.channel_name"), x1, y1, labelColor, true);
        // Время жизни сообщения (тики):
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.message_lifetime"), x2, y2 - fontHeight / 2, labelColor, true);
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.message_lifetime_ticks"), x2, y2 + fontHeight / 2, labelColor, true);
        // Время падения (тики):
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.message_fall"), x3, y3 - fontHeight / 2, labelColor, true);
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.message_fall_ticks"), x3, y3 + fontHeight / 2, labelColor, true);
        // Заморозка при взгляде
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.freezing_on_view"), x4, y4 - fontHeight / 2, labelColor, true);
        // Макс. дистанция заморозки (блоки):
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.max_freeze_distance"), x5, y5 - fontHeight / 2, labelColor, true);
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.max_freeze_distance_blocks"), x5, y5 + fontHeight / 2, labelColor, true);
        // Спавнить только спереди
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.spawn_mode_label"), x6, y6, labelColor, true);
        // Автомодерация
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.automoderation"), x7, y7, labelColor, true);
    }

    @Override
    public void close() {
        if (this.parent != null) {
            this.client.setScreen(this.parent);
        } else {
            this.client.setScreen(null);
        }
    }
} 