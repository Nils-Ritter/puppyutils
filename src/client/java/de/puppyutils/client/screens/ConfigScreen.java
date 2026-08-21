package de.puppyutils.client.screens;

import de.puppyutils.client.utils.ConfigManager;
import de.puppyutils.Puppyutils;
import de.puppyutils.client.utils.Config;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.CheckboxComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.SliderComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ConfigScreen extends BaseUIModelScreen<FlowLayout> {

    private final Config workingConfig;

    private Category selectedCategory;


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
     * owo-ui
     * ============================================================
     *
     * BaseUIModelScreen requires build().
     *
     * The actual static layout is supplied by config_screen.xml.
     * We populate the "options" container dynamically below.
     */
    @Override
    protected void build(FlowLayout root) {
        root.surface(
                io.wispforest.owo.ui.core.Surface.VANILLA_TRANSLUCENT
        );

        FlowLayout sidebar = root.childById(
                FlowLayout.class,
                "sidebar"
        );

        sidebar.horizontalSizing(
                Sizing.fixed(150)
        );

        FlowLayout content = root.childById(
                FlowLayout.class,
                "content"
        );

        content.horizontalSizing(
                Sizing.fill(100)
        );

        FlowLayout categories = root.childById(
                FlowLayout.class,
                "categories"
        );

        categories.horizontalSizing(
                Sizing.fill(100)
        );

        categories.childById(
                ButtonComponent.class,
                "category-general"
        ).horizontalSizing(Sizing.fill(100));

        categories.childById(
                ButtonComponent.class,
                "category-mining"
        ).horizontalSizing(Sizing.fill(100));

        categories.childById(
                ButtonComponent.class,
                "category-farming"
        ).horizontalSizing(Sizing.fill(100));

        categories.childById(
                ButtonComponent.class,
                "category-combat"
        ).horizontalSizing(Sizing.fill(100));

        categories.childById(
                ButtonComponent.class,
                "category-rendering"
        ).horizontalSizing(Sizing.fill(100));
    }


    @Override
    protected void init() {
        super.init();

        bindCategoryButtons();
        bindBottomButtons();

        showCategory(selectedCategory);
    }


    /*
     * ============================================================
     * Root / component helpers
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


    /*
     * ============================================================
     * Category buttons
     * ============================================================
     */

    private void bindCategoryButtons() {

        button("category-general")
                .onPress(button ->
                        showCategory(Category.GENERAL)
                );

        button("category-mining")
                .onPress(button ->
                        showCategory(Category.MINING)
                );

        button("category-farming")
                .onPress(button ->
                        showCategory(Category.FARMING)
                );

        button("category-combat")
                .onPress(button ->
                        showCategory(Category.COMBAT)
                );

        button("category-rendering")
                .onPress(button ->
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

        FlowLayout options = options();

        /*
         * Remove the old category's widgets.
         */
        options.clearChildren();

        /*
         * Build the newly selected category.
         */
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
     * General
     * ============================================================
     */

    private void buildGeneral(FlowLayout options) {

        addHeading(
                options,
                "General"
        );

        addDescription(
                options,
                "General PuppyUtils settings."
        );


        CheckboxComponent enabled =
                UIComponents.checkbox(
                        Component.literal(
                                "Enable PuppyUtils"
                        )
                );

        enabled.checked(
                workingConfig.enabled
        );

        enabled.onChanged(value -> {
            workingConfig.enabled = value;
        });

        options.child(enabled);
    }


    /*
     * ============================================================
     * Mining
     * ============================================================
     */

    private void buildMining(FlowLayout options) {

        addHeading(
                options,
                "Mining"
        );

        addDescription(
                options,
                "Configure automatic mining."
        );


        /*
         * Auto mine
         */

        CheckboxComponent autoMine =
                UIComponents.checkbox(
                        Component.literal(
                                "Enable Auto Mining"
                        )
                );

        autoMine.checked(
                workingConfig.mining.autoMine
        );

        autoMine.onChanged(value -> {
            workingConfig.mining.autoMine = value;
        });

        options.child(autoMine);


        /*
         * Mining speed
         */

        options.child(
                createSlider(
                        "Mining Speed",
                        workingConfig.mining.miningSpeed,
                        value ->
                                workingConfig.mining.miningSpeed = value
                )
        );


        /*
         * Mining radius
         */

        options.child(
                createSlider(
                        "Mining Radius",
                        workingConfig.mining.miningRadius,
                        value ->
                                workingConfig.mining.miningRadius = value
                )
        );


        /*
         * Mining mode
         */

        ButtonComponent miningMode =
                createEnumButton(
                        "Mining Mode",
                        workingConfig.mining.mode
                );

        miningMode.onPress(button -> {

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


        /*
         * Only ores
         */

        CheckboxComponent onlyOres =
                UIComponents.checkbox(
                        Component.literal(
                                "Only Mine Ores"
                        )
                );

        onlyOres.checked(
                workingConfig.mining.onlyOres
        );

        onlyOres.onChanged(value -> {
            workingConfig.mining.onlyOres = value;
        });

        options.child(onlyOres);
    }


    /*
     * ============================================================
     * Farming
     * ============================================================
     */

    private void buildFarming(FlowLayout options) {

        addHeading(
                options,
                "Farming"
        );

        addDescription(
                options,
                "Configure automatic farming."
        );


        /*
         * Auto farm
         */

        CheckboxComponent autoFarm =
                UIComponents.checkbox(
                        Component.literal(
                                "Enable Auto Farming"
                        )
                );

        autoFarm.checked(
                workingConfig.farming.autoFarm
        );

        autoFarm.onChanged(value -> {
            workingConfig.farming.autoFarm = value;
        });

        options.child(autoFarm);


        /*
         * Farming radius
         */

        options.child(
                createSlider(
                        "Farming Radius",
                        workingConfig.farming.radius,
                        value ->
                                workingConfig.farming.radius = value
                )
        );


        /*
         * Replant
         */

        CheckboxComponent replant =
                UIComponents.checkbox(
                        Component.literal(
                                "Automatically Replant"
                        )
                );

        replant.checked(
                workingConfig.farming.replant
        );

        replant.onChanged(value -> {
            workingConfig.farming.replant = value;
        });

        options.child(replant);


        /*
         * Farming mode
         */

        ButtonComponent mode =
                createEnumButton(
                        "Farming Mode",
                        workingConfig.farming.mode
                );

        mode.onPress(button -> {

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

        addHeading(
                options,
                "Combat"
        );

        addDescription(
                options,
                "Configure automatic combat."
        );


        /*
         * Auto attack
         */

        CheckboxComponent autoAttack =
                UIComponents.checkbox(
                        Component.literal(
                                "Enable Auto Attack"
                        )
                );

        autoAttack.checked(
                workingConfig.combat.autoAttack
        );

        autoAttack.onChanged(value -> {
            workingConfig.combat.autoAttack = value;
        });

        options.child(autoAttack);


        /*
         * Attack range
         */

        options.child(
                createSlider(
                        "Attack Range",
                        workingConfig.combat.attackRange,
                        value ->
                                workingConfig.combat.attackRange = value
                )
        );


        /*
         * Combat mode
         */

        ButtonComponent mode =
                createEnumButton(
                        "Combat Mode",
                        workingConfig.combat.mode
                );

        mode.onPress(button -> {

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

        addHeading(
                options,
                "Rendering"
        );

        addDescription(
                options,
                "Configure PuppyUtils rendering."
        );


        /*
         * Particles
         */

        CheckboxComponent particles =
                UIComponents.checkbox(
                        Component.literal(
                                "Render Particles"
                        )
                );

        particles.checked(
                workingConfig.rendering.particles
        );

        particles.onChanged(value -> {
            workingConfig.rendering.particles = value;
        });

        options.child(particles);


        /*
         * Render distance
         */

        options.child(
                createSlider(
                        "Render Distance",
                        workingConfig.rendering.renderDistance,
                        value ->
                                workingConfig.rendering.renderDistance = value
                )
        );


        /*
         * Render mode
         */

        ButtonComponent mode =
                createEnumButton(
                        "Render Mode",
                        workingConfig.rendering.mode
                );

        mode.onPress(button -> {

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
     * Slider helper
     * ============================================================
     */
    private FlowLayout createSlider(
            String name,
            double initialValue,
            java.util.function.DoubleConsumer consumer
    ) {
        FlowLayout row = UIContainers.horizontalFlow(
                Sizing.fill(100),
                Sizing.fixed(20)
        );

        LabelComponent label = UIComponents.label(
                Component.literal(
                        formatSliderText(name, initialValue)
                )
        );

        // Give the label a fixed/known width so the slider has
        // an unambiguous amount of space remaining.
        label.horizontalSizing(Sizing.fixed(120));

        SliderComponent slider = UIComponents.slider(
                Sizing.fill(100)
        );

        slider.value(initialValue);

        slider.onChanged().subscribe(value -> {
            label.text(
                    Component.literal(
                            formatSliderText(name, value)
                    )
            );

            consumer.accept(value);
        });

        row.child(label);
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
     * Enum buttons
     * ============================================================
     */

    private ButtonComponent createEnumButton(
            String name,
            Enum<?> value
    ) {

        return UIComponents.button(
                Component.literal(
                        formatEnumButtonText(
                                name,
                                value
                        )
                ),
                ignored -> {
                    // The actual handler is attached by the caller.
                }
        );
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
     * Labels
     * ============================================================
     */

    private void addHeading(
            FlowLayout parent,
            String text
    ) {

        parent.child(
                UIComponents.label(
                        Component.literal(text)
                )
        );
    }


    private void addDescription(
            FlowLayout parent,
            String text
    ) {

        parent.child(
                UIComponents.label(
                        Component.literal(text)
                )
        );
    }


    /*
     * ============================================================
     * Save / Reset / Cancel
     * ============================================================
     */

    private void bindBottomButtons() {

        /*
         * Save
         */

        button("save").onPress(ignored -> {

            ConfigManager
                    .get()
                    .copyFrom(workingConfig);

            ConfigManager.save();

            Minecraft
                    .getInstance()
                    .setScreen(null);
        });


        /*
         * Reset
         *
         * This only modifies the working copy.
         * The real config isn't changed until Save.
         */

        button("reset").onPress(ignored -> {

            workingConfig.reset();

            showCategory(
                    selectedCategory
            );
        });


        /*
         * Cancel
         */

        button("cancel").onPress(ignored -> {

            Minecraft
                    .getInstance()
                    .setScreen(null);
        });
    }


    /*
     * ============================================================
     * Runtime XML reload
     * ============================================================
     *
     * Creating a new BaseUIModelScreen causes owo to load the
     * XML DataSource again.
     *
     * We preserve:
     *   - current edited config values
     *   - currently selected category
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

        minecraft.setScreen(replacement);
    }


    /**
     * Same as reloadIfOpen(), but safe to call from a thread
     * other than the Minecraft client thread.
     */
    public static void reloadIfOpenAsync() {

        Minecraft
                .getInstance()
                .execute(
                        ConfigScreen::reloadIfOpen
                );
    }
}
