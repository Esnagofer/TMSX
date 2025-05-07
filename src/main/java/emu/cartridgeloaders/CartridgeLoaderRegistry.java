package emu.cartridgeloaders;

public class CartridgeLoaderRegistry {

	private static String[] loaders = {
			"Flat",
			"Konami4",
			"Konami5"
	};
	
	public static String[] getCartridgeLoaders() {
		return loaders;
	}
	
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
