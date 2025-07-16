package takeyourminestream.modid;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.util.math.MathHelper;

public class ModConfigScreen extends Screen {
    private TextFieldWidget channelNameField;
    private TextFieldWidget messageLifetimeField;
    private TextFieldWidget messageFallField;
    private TextFieldWidget maxFreezeDistanceField;
    private ButtonWidget freezingToggleButton;
    private ButtonWidget inFrontOnlyToggleButton;
    private final @Nullable Screen parent;
    private String initialChannelName;
    private ButtonWidget donationAlertsToggleButton;
    private TextFieldWidget donationAlertsWidgetUrlField;
    private TextFieldWidget donationAlertsGroupIdField;
    private TextFieldWidget donationAlertsTokenField;
    private int scrollOffset = 0;
    private static final int SCROLL_STEP = 20;
    private static final int LEFT_MARGIN = 40;
    private static final int GAP = 24;
    private int contentHeight = 0;

    public ModConfigScreen() {
        this(null);
    }

    public ModConfigScreen(@Nullable Screen parent) {
        super(Text.translatable("takeyourminestream.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int leftMargin = 40;
        int labelWidth = 180;
        int fieldWidth = Math.min(260, this.width - leftMargin - labelWidth - GAP - 40);
        int urlFieldWidth = Math.min(600, this.width - leftMargin - labelWidth - GAP - 40);
        int fieldHeight = 18;
        int spacing = 32;
        int y = 40;
        TextRenderer textRenderer = this.textRenderer;
        initialChannelName = ModConfig.TWITCH_CHANNEL_NAME;

        // --- Twitch ---
        channelNameField = new TextFieldWidget(textRenderer, leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.channel_name"));
        channelNameField.setText(ModConfig.TWITCH_CHANNEL_NAME);
        channelNameField.setChangedListener(s -> ModConfig.TWITCH_CHANNEL_NAME = s);
        this.addDrawableChild(channelNameField);
        y += spacing * 2;

        // --- Donation Alerts ---
        donationAlertsToggleButton = ButtonWidget.builder(
            Text.translatable(ModConfig.DONATIONALERTS_ENABLED ? "takeyourminestream.config.on" : "takeyourminestream.config.off"),
            btn -> {
                ModConfig.DONATIONALERTS_ENABLED = !ModConfig.DONATIONALERTS_ENABLED;
                btn.setMessage(Text.translatable(ModConfig.DONATIONALERTS_ENABLED ? "takeyourminestream.config.on" : "takeyourminestream.config.off"));
            }
        ).dimensions(leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight).build();
        this.addDrawableChild(donationAlertsToggleButton);
        y += spacing;
        donationAlertsWidgetUrlField = new TextFieldWidget(textRenderer, leftMargin + labelWidth + GAP, y, urlFieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.da_widget_url"));
        donationAlertsWidgetUrlField.setMaxLength(512);
        donationAlertsWidgetUrlField.setText(ModConfig.DONATIONALERTS_WIDGET_URL);
        donationAlertsWidgetUrlField.setChangedListener(s -> ModConfig.DONATIONALERTS_WIDGET_URL = s);
        this.addDrawableChild(donationAlertsWidgetUrlField);
        y += spacing;
        donationAlertsGroupIdField = new TextFieldWidget(textRenderer, leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.da_group_id"));
        donationAlertsGroupIdField.setText(ModConfig.DONATIONALERTS_GROUP_ID);
        donationAlertsGroupIdField.setChangedListener(s -> ModConfig.DONATIONALERTS_GROUP_ID = s);
        this.addDrawableChild(donationAlertsGroupIdField);
        y += spacing;
        donationAlertsTokenField = new TextFieldWidget(textRenderer, leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.da_token"));
        donationAlertsTokenField.setText(ModConfig.DONATIONALERTS_TOKEN);
        donationAlertsTokenField.setChangedListener(s -> ModConfig.DONATIONALERTS_TOKEN = s);
        this.addDrawableChild(donationAlertsTokenField);
        y += spacing * 2;

        // --- Визуальные параметры ---
        messageLifetimeField = new TextFieldWidget(textRenderer, leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.message_lifetime"));
        messageLifetimeField.setText(String.valueOf(ModConfig.MESSAGE_LIFETIME_TICKS));
        messageLifetimeField.setChangedListener(s -> {
            try { ModConfig.MESSAGE_LIFETIME_TICKS = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(messageLifetimeField);
        y += spacing;
        messageFallField = new TextFieldWidget(textRenderer, leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.message_fall"));
        messageFallField.setText(String.valueOf(ModConfig.MESSAGE_FALL_TICKS));
        messageFallField.setChangedListener(s -> {
            try { ModConfig.MESSAGE_FALL_TICKS = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(messageFallField);
        y += spacing;
        freezingToggleButton = ButtonWidget.builder(
            Text.translatable(ModConfig.ENABLE_FREEZING_ON_VIEW ? "takeyourminestream.config.on" : "takeyourminestream.config.off"),
            btn -> {
                ModConfig.ENABLE_FREEZING_ON_VIEW = !ModConfig.ENABLE_FREEZING_ON_VIEW;
                btn.setMessage(Text.translatable(ModConfig.ENABLE_FREEZING_ON_VIEW ? "takeyourminestream.config.on" : "takeyourminestream.config.off"));
            }
        ).dimensions(leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight).build();
        this.addDrawableChild(freezingToggleButton);
        y += spacing;
        inFrontOnlyToggleButton = ButtonWidget.builder(
            Text.translatable(ModConfig.MESSAGES_IN_FRONT_OF_PLAYER_ONLY ? "takeyourminestream.config.fop_only" : "takeyourminestream.config.around_player"),
            btn -> {
                ModConfig.MESSAGES_IN_FRONT_OF_PLAYER_ONLY = !ModConfig.MESSAGES_IN_FRONT_OF_PLAYER_ONLY;
                btn.setMessage(Text.translatable(ModConfig.MESSAGES_IN_FRONT_OF_PLAYER_ONLY ? "takeyourminestream.config.fop_only" : "takeyourminestream.config.around_player"));
            }
        ).dimensions(leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight).build();
        this.addDrawableChild(inFrontOnlyToggleButton);
        y += spacing;
        maxFreezeDistanceField = new TextFieldWidget(textRenderer, leftMargin + labelWidth + GAP, y, fieldWidth, fieldHeight, Text.translatable("takeyourminestream.config.max_freeze_distance"));
        maxFreezeDistanceField.setText(String.valueOf(ModConfig.MAX_FREEZE_DISTANCE));
        maxFreezeDistanceField.setChangedListener(s -> {
            try { ModConfig.MAX_FREEZE_DISTANCE = Double.parseDouble(s); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(maxFreezeDistanceField);
        y += spacing;

        contentHeight = y + 40;

        // Кнопка "Сохранить и выйти" закреплена в правом верхнем углу
        int saveButtonWidth = 160;
        int saveButtonX = this.width - saveButtonWidth - 20;
        int saveButtonY = 20;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("takeyourminestream.config.save_and_exit"), btn -> {
            if (!ModConfig.TWITCH_CHANNEL_NAME.equals(initialChannelName)) {
                takeyourminestream.modid.TwitchManager.onChannelNameChanged(ModConfig.TWITCH_CHANNEL_NAME);
            }
            takeyourminestream.modid.ConfigManager.saveConfig();
            takeyourminestream.modid.DonationAlertsManager.onConfigChanged();
            this.close();
        }).dimensions(saveButtonX, saveButtonY, saveButtonWidth, fieldHeight).build());
    }

    private String getFreezingButtonText() {
        return "Заморозка при взгляде: " + (ModConfig.ENABLE_FREEZING_ON_VIEW ? "ВКЛ" : "ВЫКЛ");
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int maxScroll = Math.max(0, contentHeight - this.height + 40);
        scrollOffset = MathHelper.clamp(scrollOffset - (int)(amount * SCROLL_STEP), 0, maxScroll);
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int leftMargin = 40;
        int labelWidth = 180;
        int fieldHeight = 18;
        int spacing = 32;
        int y = 40 - scrollOffset;
        int labelColor = 0xFFFFFFFF;
        // Заголовки секций
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.section.twitch"), leftMargin, y, labelColor, true);
        y += spacing;
        // Twitch
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.channel_name"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing * 2;
        // Donation Alerts
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.section.donation_alerts"), leftMargin, y, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.da_enabled"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.da_widget_url"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.da_group_id"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.da_token"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing * 2;
        // Визуальные параметры
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.section.visual"), leftMargin, y, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.message_lifetime"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.message_fall"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.freezing_on_view"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.spawn_mode_label"), leftMargin, y + fieldHeight / 2, labelColor, true);
        y += spacing;
        context.drawText(this.textRenderer, Text.translatable("takeyourminestream.config.max_freeze_distance"), leftMargin, y + fieldHeight / 2, labelColor, true);
        // Рендерим все поля и кнопки (они не двигаются физически)
        super.render(context, mouseX, mouseY, delta);
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