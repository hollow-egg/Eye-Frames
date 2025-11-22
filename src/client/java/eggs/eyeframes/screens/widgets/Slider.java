package eggs.eyeframes.screens.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class Slider extends AbstractSliderButton {

    private final java.util.function.IntConsumer callback;

    public Slider(int x, int y, int width, int height, String label, int initialValue, java.util.function.IntConsumer callback) {
        super(x, y, width, height, Component.literal(label + ": " + initialValue), initialValue / 255f);
        this.callback = callback;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal(getLabel() + ": " + (int)(value * 255)));
    }

    @Override
    protected void applyValue() {
        int val = (int)(value * 255);
        callback.accept(val);
    }

    private String getLabel() {
        String msg = getMessage().getString();
        return msg.split(":")[0];
    }
}