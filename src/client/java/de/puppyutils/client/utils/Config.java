package de.puppyutils.client.utils;

public final class Config {

    /*
     * ============================================================
     * General
     * ============================================================
     */

    public boolean enabled = true;


    /*
     * ============================================================
     * Mining
     * ============================================================
     */

    public final Mining mining = new Mining();


    public static final class Mining {

        /**
         * Automatically mine blocks.
         */
        public boolean autoMine = true;

        /**
         * Mining speed multiplier.
         *
         * 0.0 = disabled
         * 0.5 = 50%
         * 1.0 = 100%
         */
        public double miningSpeed = 0.5;

        /**
         * Mining radius.
         *
         * Stored as a normalized value for the GUI:
         * 0.0 - 1.0
         */
        public double miningRadius = 0.25;

        /**
         * Mining behavior.
         */
        public MiningMode mode = MiningMode.BALANCED;

        /**
         * Only mine ores.
         */
        public boolean onlyOres = true;
    }


    public enum MiningMode {
        PERFORMANCE,
        BALANCED,
        QUALITY
    }


    /*
     * ============================================================
     * Farming
     * ============================================================
     */

    public final Farming farming = new Farming();


    public static final class Farming {

        /**
         * Automatically harvest crops.
         */
        public boolean autoFarm = false;

        /**
         * Farming radius, represented as 0.0 - 1.0.
         */
        public double radius = 0.5;

        /**
         * Automatically replant harvested crops.
         */
        public boolean replant = true;

        /**
         * Farming behavior.
         */
        public FarmingMode mode = FarmingMode.NORMAL;
    }


    public enum FarmingMode {
        NORMAL,
        FAST,
        CONSERVATIVE
    }


    /*
     * ============================================================
     * Combat
     * ============================================================
     */

    public final Combat combat = new Combat();


    public static final class Combat {

        /**
         * Automatically attack nearby targets.
         */
        public boolean autoAttack = false;

        /**
         * Attack range, represented as 0.0 - 1.0.
         */
        public double attackRange = 0.3;

        /**
         * Combat behavior.
         */
        public CombatMode mode = CombatMode.BALANCED;
    }


    public enum CombatMode {
        SAFE,
        BALANCED,
        AGGRESSIVE
    }


    /*
     * ============================================================
     * Rendering
     * ============================================================
     */

    public final Rendering rendering = new Rendering();


    public static final class Rendering {

        /**
         * Render additional particles.
         */
        public boolean particles = true;

        /**
         * Render distance, represented as 0.0 - 1.0.
         */
        public double renderDistance = 0.75;

        /**
         * Rendering quality.
         */
        public RenderMode mode = RenderMode.DEFAULT;
    }


    public enum RenderMode {
        DEFAULT,
        LOW,
        HIGH
    }


    /*
     * ============================================================
     * Copying
     * ============================================================
     */

    /**
     * Creates a completely independent copy of this configuration.
     *
     * This is useful for the configuration GUI:
     *
     * saved config
     *      ↓
     * working copy
     *      ↓
     * user edits
     *      ↓
     * Save
     */
    public Config copy() {
        Config copy = new Config();

        copy.enabled = this.enabled;

        copy.mining.autoMine = this.mining.autoMine;
        copy.mining.miningSpeed = this.mining.miningSpeed;
        copy.mining.miningRadius = this.mining.miningRadius;
        copy.mining.mode = this.mining.mode;
        copy.mining.onlyOres = this.mining.onlyOres;

        copy.farming.autoFarm = this.farming.autoFarm;
        copy.farming.radius = this.farming.radius;
        copy.farming.replant = this.farming.replant;
        copy.farming.mode = this.farming.mode;

        copy.combat.autoAttack = this.combat.autoAttack;
        copy.combat.attackRange = this.combat.attackRange;
        copy.combat.mode = this.combat.mode;

        copy.rendering.particles = this.rendering.particles;
        copy.rendering.renderDistance = this.rendering.renderDistance;
        copy.rendering.mode = this.rendering.mode;

        return copy;
    }


    /**
     * Copies all values from another configuration into this one.
     */
    public void copyFrom(Config other) {

        this.enabled = other.enabled;

        this.mining.autoMine = other.mining.autoMine;
        this.mining.miningSpeed = other.mining.miningSpeed;
        this.mining.miningRadius = other.mining.miningRadius;
        this.mining.mode = other.mining.mode;
        this.mining.onlyOres = other.mining.onlyOres;

        this.farming.autoFarm = other.farming.autoFarm;
        this.farming.radius = other.farming.radius;
        this.farming.replant = other.farming.replant;
        this.farming.mode = other.farming.mode;

        this.combat.autoAttack = other.combat.autoAttack;
        this.combat.attackRange = other.combat.attackRange;
        this.combat.mode = other.combat.mode;

        this.rendering.particles = other.rendering.particles;
        this.rendering.renderDistance = other.rendering.renderDistance;
        this.rendering.mode = other.rendering.mode;
    }


    /*
     * ============================================================
     * Reset
     * ============================================================
     */

    /**
     * Restores every setting to its default value.
     */
    public void reset() {

        // General

        this.enabled = true;


        // Mining

        this.mining.autoMine = true;
        this.mining.miningSpeed = 0.5;
        this.mining.miningRadius = 0.25;
        this.mining.mode = MiningMode.BALANCED;
        this.mining.onlyOres = true;


        // Farming

        this.farming.autoFarm = false;
        this.farming.radius = 0.5;
        this.farming.replant = true;
        this.farming.mode = FarmingMode.NORMAL;


        // Combat

        this.combat.autoAttack = false;
        this.combat.attackRange = 0.3;
        this.combat.mode = CombatMode.BALANCED;


        // Rendering

        this.rendering.particles = true;
        this.rendering.renderDistance = 0.75;
        this.rendering.mode = RenderMode.DEFAULT;
    }
}
