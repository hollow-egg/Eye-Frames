package eggs.eyeframes;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import java.util.HashMap;

public class Input {
    private static final HashMap<String, KeyBinding> keybindings = new HashMap<>();

    public static void addKeybinding(String name, InputUtil.Type type, int key, String Category){
        keybindings.put(name,
                KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                "key.eyeframes." + name,
                type,
                key,
                Category)
        ));
    }

    public static boolean isKeybindingPressed(String name){
        return keybindings.get(name).isPressed();
    }
}
