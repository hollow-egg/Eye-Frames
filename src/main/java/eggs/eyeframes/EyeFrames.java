package eggs.eyeframes;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EyeFrames implements ModInitializer {
	public static final String MOD_ID = "eyeframes";
    public static final int PlayerHeadTextureWidth = 64;
    public static final int PlayerHeadTextureHeight = 64;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Eye Frames");
	}
}