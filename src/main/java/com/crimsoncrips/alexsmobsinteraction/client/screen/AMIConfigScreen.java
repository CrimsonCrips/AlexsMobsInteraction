package com.crimsoncrips.alexsmobsinteraction.client.screen;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class AMIConfigScreen extends Screen {

    private enum Tab {
        CLIENT("misc.alexsmobsinteraction.config_tab_client", false),
        GENERAL("misc.alexsmobsinteraction.config_tab_general", true),
        TARGETS("misc.alexsmobsinteraction.config_tab_targets", true);

        private final String translationKey;
        private final boolean common;

        Tab(String translationKey, boolean common) {
            this.translationKey = translationKey;
            this.common = common;
        }

        ModConfigSpec spec() {
            return switch (this) {
                case CLIENT -> AlexsMobsInteraction.CLIENT_CONFIG_SPEC;
                case GENERAL -> AlexsMobsInteraction.COMMON_CONFIG_SPEC;
                case TARGETS -> AlexsMobsInteraction.TARGETS_CONFIG_SPEC;
            };
        }
    }

    private static final int TAB_TOP = 24;
    private static final int TAB_WIDTH = 100;
    private static final int TAB_HEIGHT = 20;
    private static final int LIST_TOP = 52;
    private static final int FOOTER_HEIGHT = 36;
    private static final int ROW_HEIGHT = 24;
    private static final int ROW_WIDTH = 340;
    private static final int CONTROL_WIDTH = 130;
    private static final int CONTROL_HEIGHT = 18;
    private static final int CYCLE_RANGE_LIMIT = 8;

    private final Screen parent;
    private final boolean remote;
    private Tab tab = Tab.CLIENT;
    private ConfigList list;

    public AMIConfigScreen(Screen parent) {
        super(Component.translatable("misc.alexsmobsinteraction.config_title"));
        this.parent = parent;
        Minecraft minecraft = Minecraft.getInstance();
        this.remote = minecraft.getConnection() != null && !minecraft.hasSingleplayerServer();
    }

    @Override
    protected void init() {
        Tab[] tabs = Tab.values();
        int tabsLeft = this.width / 2 - (tabs.length * TAB_WIDTH + (tabs.length - 1) * 2) / 2;
        for (int i = 0; i < tabs.length; i++) {
            Tab entry = tabs[i];
            Button button = Button.builder(Component.translatable(entry.translationKey), b -> {
                this.tab = entry;
                this.rebuildWidgets();
            }).bounds(tabsLeft + i * (TAB_WIDTH + 2), TAB_TOP, TAB_WIDTH, TAB_HEIGHT).build();
            button.active = entry != this.tab;
            this.addRenderableWidget(button);
        }

        this.list = new ConfigList(this.minecraft);
        boolean readOnly = this.tab.common && this.remote;
        if (readOnly) {
            this.list.addHeader(Component.translatable("misc.alexsmobsinteraction.config_note_remote"));
        }
        populate(this.tab.spec().getValues(), readOnly);
        this.addRenderableWidget(this.list);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    private void populate(UnmodifiableConfig values, boolean readOnly) {
        for (Map.Entry<String, Object> entry : values.valueMap().entrySet()) {
            if (entry.getValue() instanceof UnmodifiableConfig section) {
                this.list.addHeader(Component.literal(prettify(entry.getKey())));
                populate(section, readOnly);
            } else if (entry.getValue() instanceof ModConfigSpec.ConfigValue<?> value) {
                AbstractWidget control = control(value);
                if (control == null)
                    continue;
                String comment = value.getSpec().getComment();
                if (comment != null && !comment.isBlank()) {
                    control.setTooltip(Tooltip.create(Component.literal(comment)));
                }
                if (readOnly) {
                    control.active = false;
                    if (control instanceof EditBox box) {
                        box.setEditable(false);
                    }
                }
                this.list.addRow(label(value), comment, control);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private AbstractWidget control(ModConfigSpec.ConfigValue<?> value) {
        if (value instanceof ModConfigSpec.BooleanValue booleanValue) {
            return booleanButton(booleanValue);
        }
        if (value instanceof ModConfigSpec.IntValue intValue) {
            ModConfigSpec.Range<Integer> range = intValue.getSpec().getRange();
            if (range != null && (long) range.getMax() - range.getMin() < CYCLE_RANGE_LIMIT) {
                return cycleButton(intValue, range);
            }
            return intBox(intValue, range);
        }
        if (value instanceof ModConfigSpec.DoubleValue doubleValue) {
            return doubleBox(doubleValue);
        }
        if (value instanceof ModConfigSpec.EnumValue<?> enumValue) {
            return enumButton(enumValue);
        }
        if (value.getDefault() instanceof String) {
            return stringBox((ModConfigSpec.ConfigValue<String>) value);
        }
        return null;
    }

    private static Component label(ModConfigSpec.ConfigValue<?> value) {
        String translationKey = value.getSpec().getTranslationKey();
        if (translationKey != null && I18n.exists(translationKey)) {
            return Component.translatable(translationKey);
        }
        List<String> path = value.getPath();
        String name = path.get(path.size() - 1);
        if (name.endsWith("_ENABLED")) {
            name = name.substring(0, name.length() - "_ENABLED".length());
        }
        return Component.literal(prettify(name));
    }

    private static String prettify(String name) {
        return Arrays.stream(name.split("[_\\s]+"))
                .filter(word -> !word.isEmpty())
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }

    private Button booleanButton(ModConfigSpec.BooleanValue value) {
        return Button.builder(booleanLabel(value.get()), b -> {
            value.set(!value.get());
            b.setMessage(booleanLabel(value.get()));
        }).bounds(0, 0, CONTROL_WIDTH, CONTROL_HEIGHT).build();
    }

    private static Component booleanLabel(boolean value) {
        return value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
    }

    private Button cycleButton(ModConfigSpec.IntValue value, ModConfigSpec.Range<Integer> range) {
        return Button.builder(Component.literal(String.valueOf(value.get())), b -> {
            int next = value.get() + 1;
            value.set(next > range.getMax() ? range.getMin() : next);
            b.setMessage(Component.literal(String.valueOf(value.get())));
        }).bounds(0, 0, CONTROL_WIDTH, CONTROL_HEIGHT).build();
    }

    private <E extends Enum<E>> Button enumButton(ModConfigSpec.EnumValue<E> value) {
        return Button.builder(Component.literal(value.get().name()), b -> {
            E current = value.get();
            E[] constants = current.getDeclaringClass().getEnumConstants();
            value.set(constants[(current.ordinal() + 1) % constants.length]);
            b.setMessage(Component.literal(value.get().name()));
        }).bounds(0, 0, CONTROL_WIDTH, CONTROL_HEIGHT).build();
    }

    private EditBox intBox(ModConfigSpec.IntValue value, ModConfigSpec.Range<Integer> range) {
        EditBox box = numberBox(value);
        box.setResponder(text -> {
            try {
                int parsed = Integer.parseInt(text.trim());
                value.set(range == null ? parsed : Mth.clamp(parsed, range.getMin(), range.getMax()));
                box.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
            } catch (NumberFormatException ignored) {
                box.setTextColor(0xFF5555);
            }
        });
        return box;
    }

    private EditBox doubleBox(ModConfigSpec.DoubleValue value) {
        ModConfigSpec.Range<Double> range = value.getSpec().getRange();
        EditBox box = numberBox(value);
        box.setResponder(text -> {
            try {
                double parsed = Double.parseDouble(text.trim());
                value.set(range == null ? parsed : Mth.clamp(parsed, range.getMin(), range.getMax()));
                box.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
            } catch (NumberFormatException ignored) {
                box.setTextColor(0xFF5555);
            }
        });
        return box;
    }

    private EditBox numberBox(ModConfigSpec.ConfigValue<?> value) {
        EditBox box = new EditBox(this.font, 0, 0, CONTROL_WIDTH, CONTROL_HEIGHT, label(value));
        box.setMaxLength(32);
        box.setValue(String.valueOf(value.get()));
        return box;
    }

    private EditBox stringBox(ModConfigSpec.ConfigValue<String> value) {
        EditBox box = new EditBox(this.font, 0, 0, CONTROL_WIDTH, CONTROL_HEIGHT, label(value));
        box.setMaxLength(256);
        box.setValue(value.get());
        box.setResponder(value::set);
        return box;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        AlexsMobsInteraction.CLIENT_CONFIG_SPEC.save();
        if (!this.remote) {
            AlexsMobsInteraction.COMMON_CONFIG_SPEC.save();
            AlexsMobsInteraction.TARGETS_CONFIG_SPEC.save();
        }
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry> {

        ConfigList(Minecraft minecraft) {
            super(minecraft, AMIConfigScreen.this.width, AMIConfigScreen.this.height - LIST_TOP - FOOTER_HEIGHT, LIST_TOP, ROW_HEIGHT);
        }

        void addHeader(Component title) {
            this.addEntry(new HeaderEntry(title));
        }

        void addRow(Component label, String comment, AbstractWidget control) {
            this.addEntry(new RowEntry(label, comment, control));
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        @OnlyIn(Dist.CLIENT)
        abstract class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        }

        @OnlyIn(Dist.CLIENT)
        class HeaderEntry extends Entry {
            private final Component title;

            HeaderEntry(Component title) {
                this.title = title;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean hovering, float partialTick) {
                Font font = AMIConfigScreen.this.font;
                guiGraphics.drawString(font, this.title, left + 2, top + height - font.lineHeight - 4, 0xFFD84A, false);
                guiGraphics.fill(left, top + height - 2, left + width, top + height - 1, 0x60FFFFFF);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of();
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of();
            }
        }

        @OnlyIn(Dist.CLIENT)
        class RowEntry extends Entry {
            private final Component label;
            private final String comment;
            private final AbstractWidget control;

            RowEntry(Component label, String comment, AbstractWidget control) {
                this.label = label;
                this.comment = comment;
                this.control = control;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean hovering, float partialTick) {
                Font font = AMIConfigScreen.this.font;
                int available = width - CONTROL_WIDTH - 12;
                int textWidth = font.width(this.label);
                float scale = textWidth > available ? available / (float) textWidth : 1f;

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(left + 4, top + (height - font.lineHeight * scale) / 2f, 0f);
                guiGraphics.pose().scale(scale, scale, 1f);
                guiGraphics.drawString(font, this.label, 0, 0, 0xFFFFFF, false);
                guiGraphics.pose().popPose();

                if (hovering && this.comment != null && !this.comment.isBlank() && mouseX < left + width - CONTROL_WIDTH) {
                    AMIConfigScreen.this.setTooltipForNextRenderPass(font.split(Component.literal(this.comment), 200));
                }

                this.control.setX(left + width - CONTROL_WIDTH);
                this.control.setY(top + (height - CONTROL_HEIGHT) / 2);
                this.control.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(this.control);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(this.control);
            }
        }
    }
}
