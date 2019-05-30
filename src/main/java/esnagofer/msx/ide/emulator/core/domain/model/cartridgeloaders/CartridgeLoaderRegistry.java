package esnagofer.msx.ide.emulator.core.domain.model.cartridgeloaders;

/**
 * The Class CartridgeLoaderRegistry.
 */
public class CartridgeLoaderRegistry {

	/** The loaders. */
	private static String[] loaders = {
		"Flat",
		"Konami4",
		"Konami5"
	};
	
	/**
	 * Gets the cartridge loaders.
	 *
	 * @return the cartridge loaders
	 */
	public static String[] getCartridgeLoaders() {
		return loaders;
	}
	
	/**
	 * Gets the single instance of CartridgeLoaderRegistry.
	 *
	 * @param loader the loader
	 * @param name the name
	 * @return single instance of CartridgeLoaderRegistry
	 */
	public static CartridgeLoader getInstance(String loader, String name) {
		switch (loader) {
		case "Flat":
			return new FlatMapper(name);
		case "Konami4":
			return new Konami4Mapper(name);
		case "Konami5":
			return new Konami5Mapper(name);
		}
		throw new RuntimeException("Unknown loader type " + loader);
	}
	
}
