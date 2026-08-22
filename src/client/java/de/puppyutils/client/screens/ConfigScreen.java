package de.puppyutils.client.screens;

import de.puppyutils.Puppyutils;
import de.puppyutils.client.utils.Config;
import de.puppyutils.client.utils.ConfigManager;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.SliderComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Sizing;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;


public class ConfigScreen extends BaseUIModelScreen<FlowLayout> {

    private final Config workingConfig;
    private Category selectedCategory;


    /*
     * ============================================================
     * Colors
     * ============================================================
     */

    private static final int COLOR_PANEL =
            0xD91A1F29;

    private static final int COLOR_BUTTON =
            0xFF252B36;

    private static final int COLOR_BUTTON_HOVER =
            0xFF303846;

    private static final int COLOR_BUTTON_DISABLED =
            0xFF171B22;

    private static final int COLOR_ACCENT =
            0xFF6C8CFF;

    private static final int COLOR_ACCENT_HOVER =
            0xFF829DFF;

    private static final int COLOR_TEXT =
            0xFFFFFFFF;


    /*
     * ============================================================
     * Categories
     * ============================================================
     */

    private enum Category {
        GENERAL,
        MINING,
        FARMING,
        COMBAT,
        RENDERING
    }


    /*
     * ============================================================
     * Constructors
     * ============================================================
     */

    public ConfigScreen() {
        this(
                ConfigManager.get().copy(),
                Category.GENERAL
        );
    }


    private ConfigScreen(
            Config workingConfig,
            Category selectedCategory
    ) {
        super(
                FlowLayout.class,
                DataSource.asset(
                        Identifier.fromNamespaceAndPath(
                                Puppyutils.MOD_ID,
                                "config_screen"
                        )
                )
        );

        this.workingConfig = workingConfig;
        this.selectedCategory = selectedCategory;
    }


    /*
     * ============================================================
     * Build
     * ============================================================
     */

    @Override
    protected void build(FlowLayout root) {
        /*
         * Everything static is loaded from XML.
         */
    }


    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) {
            return;
        }

        styleStaticButtons();

        bindCategoryButtons();
        bindBottomButtons();

        updateCategoryHeader();
        showCategory(selectedCategory);
    }


    /*
     * ============================================================
     * Helpers
     * ============================================================
     */

    private FlowLayout root() {
        return this.uiAdapter.rootComponent;
    }


    private FlowLayout options() {
        return root().childById(
                FlowLayout.class,
                "options"
        );
    }


    private ButtonComponent button(String id) {
        return root().childById(
                ButtonComponent.class,
                id
        );
    }


    private LabelComponent label(String id) {
        return root().childById(
                LabelComponent.class,
                id
        );
    }


    /*
     * ============================================================
     * Static buttons
     * ============================================================
     */

    private void styleStaticButtons() {

        styleCategoryButton("category-general");
        styleCategoryButton("category-mining");
        styleCategoryButton("category-farming");
        styleCategoryButton("category-combat");
        styleCategoryButton("category-rendering");


        button("save")
                .renderer(
                        modernButtonRenderer(
                                COLOR_ACCENT,
                                COLOR_ACCENT_HOVER,
                                COLOR_BUTTON_DISABLED
                        )
                )
                .textShadow(false);


        button("reset")
                .renderer(
                        modernButtonRenderer(
                                COLOR_BUTTON,
                                COLOR_BUTTON_HOVER,
                                COLOR_BUTTON_DISABLED
                        )
                )
                .textShadow(false);


        button("cancel")
                .renderer(
                        modernButtonRenderer(
                                COLOR_BUTTON,
                                COLOR_BUTTON_HOVER,
                                COLOR_BUTTON_DISABLED
                        )
                )
                .textShadow(false);
    }


    private void styleCategoryButton(String id) {

        button(id)
                .renderer(
                        modernButtonRenderer(
                                COLOR_BUTTON,
                                COLOR_BUTTON_HOVER,
                                COLOR_BUTTON_DISABLED
                        )
                )
                .textShadow(false);
    }


    /*
     * ============================================================
     * Category buttons
     * ============================================================
     */

    private void bindCategoryButtons() {

        button("category-general")
                .onPress(
                        ignored ->
                                showCategory(Category.GENERAL)
                );

        button("category-mining")
                .onPress(
                        ignored ->
                                showCategory(Category.MINING)
                );

        button("category-farming")
                .onPress(
                        ignored ->
                                showCategory(Category.FARMING)
                );

        button("category-combat")
                .onPress(
                        ignored ->
                                showCategory(Category.COMBAT)
                );

        button("category-rendering")
                .onPress(
                        ignored ->
                                showCategory(Category.RENDERING)
                );
    }


    /*
     * ============================================================
     * Category switching
     * ============================================================
     */

    private void showCategory(Category category) {

        this.selectedCategory = category;

        updateCategoryHeader();

        FlowLayout options = options();

        options.clearChildren();


        switch (category) {

            case GENERAL ->
                    buildGeneral(options);

            case MINING ->
                    buildMining(options);

            case FARMING ->
                    buildFarming(options);

            case COMBAT ->
                    buildCombat(options);

            case RENDERING ->
                    buildRendering(options);
        }
    }


    /*
     * ============================================================
     * Header
     * ============================================================
     */

    private void updateCategoryHeader() {

        String title;
        String description;

        switch (selectedCategory) {

            case GENERAL -> {
                title = "General";
                description =
                        "General PuppyUtils settings.";
            }

            case MINING -> {
                title = "Mining";
                description =
                        "Configure automatic mining.";
            }

            case FARMING -> {
                title = "Farming";
                description =
                        "Configure automatic farming.";
            }

            case COMBAT -> {
                title = "Combat";
                description =
                        "Configure automatic combat.";
            }

            case RENDERING -> {
                title = "Rendering";
                description =
                        "Configure PuppyUtils rendering.";
            }

            default -> {
                title = "PuppyUtils";
                description = "";
            }
        }

        label("content-title").text(
                Component.literal(title)
        );

        label("content-description").text(
                Component.literal(description)
        );
    }


    /*
     * ============================================================
     * General
     * ============================================================
     */

    private void buildGeneral(FlowLayout options) {

        options.child(
                createModernToggle(
                        "Enable PuppyUtils",
                        workingConfig.enabled,
                        value ->
                                workingConfig.enabled = value
                )
        );
    }


    /*
     * ============================================================
     * Mining
     * ============================================================
     */

    private void buildMining(FlowLayout options) {

        options.child(
                createModernToggle(
                        "Enable Auto Mining",
                        workingConfig.mining.autoMine,
                        value ->
                                workingConfig.mining.autoMine = value
                )
        );

        options.child(
                createModernSlider(
                        "Mining Speed",
                        workingConfig.mining.miningSpeed,
                        value ->
                                workingConfig.mining.miningSpeed = value
                )
        );

        options.child(
                createModernSlider(
                        "Mining Radius",
                        workingConfig.mining.miningRadius,
                        value ->
                                workingConfig.mining.miningRadius = value
                )
        );


        ButtonComponent miningMode =
                createEnumButton(
                        "Mining Mode",
                        workingConfig.mining.mode
                );

        miningMode.onPress(ignored -> {

            workingConfig.mining.mode =
                    nextEnum(
                            workingConfig.mining.mode
                    );

            updateEnumButton(
                    miningMode,
                    "Mining Mode",
                    workingConfig.mining.mode
            );
        });

        options.child(miningMode);


        options.child(
                createModernToggle(
                        "Only Mine Ores",
                        workingConfig.mining.onlyOres,
                        value ->
                                workingConfig.mining.onlyOres = value
                )
        );
    }


    /*
     * ============================================================
     * Farming
     * ============================================================
     */

    private void buildFarming(FlowLayout options) {

        options.child(
                createModernToggle(
                        "Enable Auto Farming",
                        workingConfig.farming.autoFarm,
                        value ->
                                workingConfig.farming.autoFarm = value
                )
        );

        options.child(
                createModernSlider(
                        "Farming Radius",
                        workingConfig.farming.radius,
                        value ->
                                workingConfig.farming.radius = value
                )
        );

        options.child(
                createModernToggle(
                        "Automatically Replant",
                        workingConfig.farming.replant,
                        value ->
                                workingConfig.farming.replant = value
                )
        );


        ButtonComponent mode =
                createEnumButton(
                        "Farming Mode",
                        workingConfig.farming.mode
                );

        mode.onPress(ignored -> {

            workingConfig.farming.mode =
                    nextEnum(
                            workingConfig.farming.mode
                    );

            updateEnumButton(
                    mode,
                    "Farming Mode",
                    workingConfig.farming.mode
            );
        });

        options.child(mode);
    }


    /*
     * ============================================================
     * Combat
     * ============================================================
     */

    private void buildCombat(FlowLayout options) {

        options.child(
                createModernToggle(
                        "Enable Auto Attack",
                        workingConfig.combat.autoAttack,
                        value ->
                                workingConfig.combat.autoAttack = value
                )
        );

        options.child(
                createModernSlider(
                        "Attack Range",
                        workingConfig.combat.attackRange,
                        value ->
                                workingConfig.combat.attackRange = value
                )
        );


        ButtonComponent mode =
                createEnumButton(
                        "Combat Mode",
                        workingConfig.combat.mode
                );

        mode.onPress(ignored -> {

            workingConfig.combat.mode =
                    nextEnum(
                            workingConfig.combat.mode
                    );

            updateEnumButton(
                    mode,
                    "Combat Mode",
                    workingConfig.combat.mode
            );
        });

        options.child(mode);
    }


    /*
     * ============================================================
     * Rendering
     * ============================================================
     */

    private void buildRendering(FlowLayout options) {

        options.child(
                createModernToggle(
                        "Render Particles",
                        workingConfig.rendering.particles,
                        value ->
                                workingConfig.rendering.particles = value
                )
        );

        options.child(
                createModernSlider(
                        "Render Distance",
                        workingConfig.rendering.renderDistance,
                        value ->
                                workingConfig.rendering.renderDistance = value
                )
        );


        ButtonComponent mode =
                createEnumButton(
                        "Render Mode",
                        workingConfig.rendering.mode
                );

        mode.onPress(ignored -> {

            workingConfig.rendering.mode =
                    nextEnum(
                            workingConfig.rendering.mode
                    );

            updateEnumButton(
                    mode,
                    "Render Mode",
                    workingConfig.rendering.mode
            );
        });

        options.child(mode);
    }


    /*
     * ============================================================
     * Modern Toggle
     * ============================================================
     */
    private FlowLayout createModernToggle(
            String text,
            boolean initialValue,
            Consumer<Boolean> consumer
    ) {

        FlowLayout row =
                UIContainers.horizontalFlow(
                        Sizing.fill(100),
                        Sizing.fixed(32)
                );

        row.padding(
                io.wispforest.owo.ui.core.Insets.of(
                        6,
                        10,
                        6,
                        10
                )
        );

        row.surface(
                io.wispforest.owo.ui.core.Surface.flat(
                        COLOR_PANEL
                )
        );


        LabelComponent label =
                UIComponents.label(
                        Component.literal(text)
                );

        label.color(
                Color.ofArgb(COLOR_TEXT)
        );


        /*
         * Store the state separately so the renderer can read it.
         */
        boolean[] state =
                new boolean[]{initialValue};


        /*
         * Create the button first without referencing it
         * from its own initialization lambda.
         */
        ButtonComponent toggle =
                UIComponents.button(
                        Component.empty(),
                        ignored -> {

                            state[0] = !state[0];

                            consumer.accept(
                                    state[0]
                            );

                            /*
                             * The renderer already references the same
                             * state array, so it automatically reflects
                             * the new value.
                             */
                        }
                );


        toggle.sizing(
                Sizing.fixed(46),
                Sizing.fixed(22)
        );

        toggle.textShadow(false);


        /*
         * Install the renderer after the button has been created.
         */
        toggle.renderer(
                modernToggleRenderer(
                        state
                )
        );


        /*
         * Label on the left.
         */
        row.child(label);


        /*
         * Flexible spacer keeps the toggle pinned to the right.
         */
        FlowLayout spacer =
                UIContainers.horizontalFlow(
                        Sizing.fill(100),
                        Sizing.fixed(1)
                );

        row.child(spacer);


        /*
         * Toggle on the right.
         */
        row.child(toggle);


        return row;
    }


    /*
     * ============================================================
     * Modern Slider
     * ============================================================
     */

    private FlowLayout createModernSlider(
            String name,
            double initialValue,
            DoubleConsumer consumer
    ) {

        FlowLayout row =
                UIContainers.horizontalFlow(
                        Sizing.fill(100),
                        Sizing.fixed(34)
                );

        row.padding(
                io.wispforest.owo.ui.core.Insets.of(
                        6,
                        10,
                        6,
                        10
                )
        );

        row.surface(
                io.wispforest.owo.ui.core.Surface.flat(
                        COLOR_PANEL
                )
        );


        LabelComponent label =
                UIComponents.label(
                        Component.literal(
                                formatSliderText(
                                        name,
                                        initialValue
                                )
                        )
                );

        label.color(
                Color.ofArgb(COLOR_TEXT)
        );


        SliderComponent slider =
                UIComponents.slider(
                        Sizing.fill(60)
                );

        slider.value(initialValue);


        slider.onChanged().subscribe(value -> {

            label.text(
                    Component.literal(
                            formatSliderText(
                                    name,
                                    value
                            )
                    )
            );

            consumer.accept(value);
        });


        row.child(label);


        FlowLayout spacer =
                UIContainers.horizontalFlow(
                        Sizing.fill(100),
                        Sizing.fixed(1)
                );

        row.child(spacer);
        row.child(slider);


        return row;
    }


    private String formatSliderText(
            String name,
            double value
    ) {

        return name +
                ": " +
                Math.round(value * 100) +
                "%";
    }


    /*
     * ============================================================
     * Enum Button
     * ============================================================
     */

    private ButtonComponent createEnumButton(
            String name,
            Enum<?> value
    ) {

        ButtonComponent button =
                UIComponents.button(
                        Component.literal(
                                formatEnumButtonText(
                                        name,
                                        value
                                )
                        ),
                        ignored -> {}
                );


        button.renderer(
                modernButtonRenderer(
                        COLOR_BUTTON,
                        COLOR_BUTTON_HOVER,
                        COLOR_BUTTON_DISABLED
                )
        );

        button.textShadow(false);


        button.sizing(
                Sizing.fill(100),
                Sizing.fixed(30)
        );


        return button;
    }


    private void updateEnumButton(
            ButtonComponent button,
            String name,
            Enum<?> value
    ) {

        button.setMessage(
                Component.literal(
                        formatEnumButtonText(
                                name,
                                value
                        )
                )
        );
    }


    private String formatEnumButtonText(
            String name,
            Enum<?> value
    ) {

        return name +
                ": " +
                formatEnum(value);
    }


    private String formatEnum(
            Enum<?> value
    ) {

        String text =
                value.name()
                        .toLowerCase()
                        .replace('_', ' ');

        return Character.toUpperCase(
                text.charAt(0)
        ) + text.substring(1);
    }


    private <T extends Enum<T>> T nextEnum(
            T current
    ) {

        T[] values =
                current.getDeclaringClass()
                        .getEnumConstants();

        int next =
                (current.ordinal() + 1)
                        % values.length;

        return values[next];
    }


    /*
     * ============================================================
     * Modern Button Renderer
     * ============================================================
     *
     * No access to AbstractWidget.width/height.
     *
     * The XML controls the actual dimensions.
     */

    private static ButtonComponent.Renderer modernButtonRenderer(
            int normal,
            int hovered,
            int disabled
    ) {

        return (graphics, button, delta) -> {

            int color;

            if (!button.active) {
                color = disabled;
            } else if (button.isHovered()) {
                color = hovered;
            } else {
                color = normal;
            }


            /*
             * Use a fixed height matching the XML button height.
             * Width is supplied by the component itself through
             * the renderer's available drawing area.
             */

            int x = button.getX();
            int y = button.getY();


            drawRoundedRect(
                    graphics,
                    x,
                    y,
                    120,
                    28,
                    6,
                    color
            );
        };
    }


    /*
     * ============================================================
     * Modern Toggle Renderer
     * ============================================================
     */

    private static ButtonComponent.Renderer modernToggleRenderer(
            boolean[] state
    ) {

        return (graphics, button, delta) -> {

            int x = button.getX();
            int y = button.getY();

            int width = 46;
            int height = 22;

            int trackColor =
                    state[0]
                            ? COLOR_ACCENT
                            : 0xFF303744;


            drawRoundedRect(
                    graphics,
                    x,
                    y,
                    width,
                    height,
                    height / 2,
                    trackColor
            );


            int knobRadius = 7;

            int knobX =
                    state[0]
                            ? x + width - 11
                            : x + 11;

            int knobY =
                    y + height / 2;


            graphics.drawCircle(
                    knobX,
                    knobY,
                    32,
                    knobRadius,
                    Color.ofArgb(0xFFFFFFFF)
            );
        };
    }


    /*
     * ============================================================
     * Rounded Rectangle
     * ============================================================
     */

    private static void drawRoundedRect(
            io.wispforest.owo.ui.core.OwoUIGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int radius,
            int color
    ) {

        radius =
                Math.min(
                        radius,
                        Math.min(width, height) / 2
                );


        graphics.fill(
                x + radius,
                y,
                x + width - radius,
                y + height,
                color
        );


        graphics.fill(
                x,
                y + radius,
                x + width,
                y + height - radius,
                color
        );


        Color owoColor =
                Color.ofArgb(color);


        graphics.drawCircle(
                x + radius,
                y + radius,
                24,
                radius,
                owoColor
        );

        graphics.drawCircle(
                x + width - radius,
                y + radius,
                24,
                radius,
                owoColor
        );

        graphics.drawCircle(
                x + radius,
                y + height - radius,
                24,
                radius,
                owoColor
        );

        graphics.drawCircle(
                x + width - radius,
                y + height - radius,
                24,
                radius,
                owoColor
        );
    }


    /*
     * ============================================================
     * Save / Clear / Cancel
     * ============================================================
     */

    private void bindBottomButtons() {

        /*
         * Save
         */

        button("save")
                .onPress(ignored -> {

                    ConfigManager
                            .get()
                            .copyFrom(
                                    workingConfig
                            );

                    ConfigManager.save();

                    Minecraft
                            .getInstance()
                            .setScreen(null);
                });


        /*
         * Clear
         */

        button("reset")
                .onPress(ignored -> {

                    workingConfig.reset();

                    showCategory(
                            selectedCategory
                    );
                });


        /*
         * Cancel
         */

        button("cancel")
                .onPress(ignored -> {

                    Minecraft
                            .getInstance()
                            .setScreen(null);
                });
    }


    /*
     * ============================================================
     * Runtime reload
     * ============================================================
     */

    public static void reloadIfOpen() {

        Minecraft minecraft =
                Minecraft.getInstance();


        if (!(minecraft.screen instanceof ConfigScreen oldScreen)) {
            return;
        }


        ConfigScreen replacement =
                new ConfigScreen(
                        oldScreen.workingConfig.copy(),
                        oldScreen.selectedCategory
                );


        minecraft.setScreen(
                replacement
        );
    }


    public static void reloadIfOpenAsync() {

        Minecraft
                .getInstance()
                .execute(
                        ConfigScreen::reloadIfOpen
                );
    }
}
