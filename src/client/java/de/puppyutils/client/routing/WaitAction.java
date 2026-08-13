package de.puppyutils.client.routing;

public record WaitAction(int ticks) implements MacroAction {
    @Override
    public String type() {
        return "wait";
    }

    @Override
    public float red() {
        return 1.0f;
    }

    @Override
    public float green() {
        return 1.0f;
    }

    @Override
    public float blue() {
        return 0.0f;
    }

    @Override
    public float alpha() {
        return 0.35f;
    }
}
