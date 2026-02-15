package net.sourceforge.plantuml.klimt.awt;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// ::comment when __TEAVM__
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;
// ::done

// ::uncomment when __TEAVM__
//import org.teavm.jso.JSBody;
//import org.teavm.jso.canvas.CanvasImageSource;
//import org.teavm.jso.canvas.CanvasRenderingContext2D;
//import org.teavm.jso.canvas.ImageData;
//import org.teavm.jso.dom.html.HTMLCanvasElement;
//import org.teavm.jso.dom.html.HTMLDocument;
//import org.teavm.jso.typedarrays.Uint8ClampedArray;
// ::done

/**
 * Portable image abstraction that works both in standard Java (using BufferedImage)
 * and in TeaVM (using an in-memory pixel array with browser canvas for PNG export).
 */
public class PortableImage {

	// ::comment when __TEAVM__
	public static final int TYPE_INT_RGB = BufferedImage.TYPE_INT_RGB;
	public static final int TYPE_INT_ARGB = BufferedImage.TYPE_INT_ARGB;
	public static final int TYPE_INT_ARGB_PRE = BufferedImage.TYPE_INT_ARGB_PRE;

	private final BufferedImage image;

	public PortableImage(int width, int height, int imageType) {
		this.image = new BufferedImage(width, height, imageType);
	}

	public PortableImage(BufferedImage image) {
		this.image = image;
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public int getRGB(int x, int y) {
		return image.getRGB(x, y);
	}

	public void setRGB(int x, int y, int rgb) {
		image.setRGB(x, y, rgb);
	}

	public int getType() {
		return image.getType();
	}

	public BufferedImage getBufferedImage() {
		return image;
	}

	public PortableImage getSubimage(int x, int y, int width, int height) {
		return new PortableImage(image.getSubimage(x, y, width, height));
	}

	public Graphics2D createGraphics() {
		return image.createGraphics();
	}

	public int getTransparency() {
		return image.getTransparency();
	}

	public Graphics getGraphics() {
		return image.getGraphics();
	}
	
	/**
	 * Converts this image to a PNG data URL (data:image/png;base64,...).
	 * In standard Java, uses ImageIO to encode the BufferedImage.
	 * 
	 * @return PNG data URL string
	 */
	public String toPngDataUrl() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", baos);
			byte[] bytes = baos.toByteArray();
			String base64 = Base64.getEncoder().encodeToString(bytes);
			return "data:image/png;base64," + base64;
		} catch (IOException e) {
			throw new RuntimeException("Failed to encode image as PNG", e);
		}
	}
	
	/**
	 * Returns all pixels as an ARGB int array (row-major order).
	 * 
	 * @return pixel array of size width * height
	 */
	public int[] getPixels() {
		int w = image.getWidth();
		int h = image.getHeight();
		return image.getRGB(0, 0, w, h, null, 0, w);
	}
	// ::done

	// ::uncomment when __TEAVM__
//	public static final int TYPE_INT_RGB = 1;
//	public static final int TYPE_INT_ARGB = 2;
//	public static final int TYPE_INT_ARGB_PRE = 3;
//
//	private final int width;
//	private final int height;
//	private final int imageType;
//	private final int[] pixels;
//
//	public PortableImage(int width, int height, int imageType) {
//		this.width = width;
//		this.height = height;
//		this.imageType = imageType;
//		this.pixels = new int[width * height];
//		// Initialize with default color based on type
//		if (imageType == TYPE_INT_RGB) {
//			// Default to white for RGB
//			java.util.Arrays.fill(pixels, 0xFFFFFFFF);
//		} else {
//			// Default to transparent for ARGB
//			java.util.Arrays.fill(pixels, 0x00000000);
//		}
//	}
//
//	/**
//	 * Creates a PortableImage from an existing pixel array.
//	 * The array is copied, not referenced.
//	 */
//	public PortableImage(int width, int height, int imageType, int[] sourcePixels) {
//		this.width = width;
//		this.height = height;
//		this.imageType = imageType;
//		this.pixels = new int[width * height];
//		System.arraycopy(sourcePixels, 0, this.pixels, 0, Math.min(sourcePixels.length, this.pixels.length));
//	}
//
//	public int getWidth() {
//		return width;
//	}
//
//	public int getHeight() {
//		return height;
//	}
//
//	public int getRGB(int x, int y) {
//		if (x < 0 || x >= width || y < 0 || y >= height)
//			return 0;
//		return pixels[y * width + x];
//	}
//
//	public void setRGB(int x, int y, int rgb) {
//		if (x < 0 || x >= width || y < 0 || y >= height)
//			return;
//		pixels[y * width + x] = rgb;
//	}
//
//	public int getType() {
//		return imageType;
//	}
//
//	public PortableImage getSubimage(int x, int y, int w, int h) {
//		// Clamp bounds
//		if (x < 0) { w += x; x = 0; }
//		if (y < 0) { h += y; y = 0; }
//		if (x + w > width) w = width - x;
//		if (y + h > height) h = height - y;
//		if (w <= 0 || h <= 0)
//			return new PortableImage(1, 1, imageType);
//		
//		int[] subPixels = new int[w * h];
//		for (int row = 0; row < h; row++) {
//			int srcOffset = (y + row) * width + x;
//			int dstOffset = row * w;
//			System.arraycopy(pixels, srcOffset, subPixels, dstOffset, w);
//		}
//		return new PortableImage(w, h, imageType, subPixels);
//	}
//
//	public int getTransparency() {
//		// 1 = OPAQUE, 2 = BITMASK, 3 = TRANSLUCENT
//		return (imageType == TYPE_INT_RGB) ? 1 : 3;
//	}
//
//	/**
//	 * Returns all pixels as an ARGB int array (row-major order).
//	 * Returns a copy to prevent external modification.
//	 * 
//	 * @return pixel array of size width * height
//	 */
//	public int[] getPixels() {
//		int[] copy = new int[pixels.length];
//		System.arraycopy(pixels, 0, copy, 0, pixels.length);
//		return copy;
//	}
//
//	/**
//	 * Converts this image to a PNG data URL using browser's canvas API.
//	 * Creates an offscreen canvas, writes pixels via ImageData, and exports as PNG.
//	 * 
//	 * @return PNG data URL string (data:image/png;base64,...)
//	 */
//	public String toPngDataUrl() {
//		HTMLDocument doc = HTMLDocument.current();
//		HTMLCanvasElement canvas = (HTMLCanvasElement) doc.createElement("canvas");
//		canvas.setWidth(width);
//		canvas.setHeight(height);
//
//		CanvasRenderingContext2D ctx = (CanvasRenderingContext2D) canvas.getContext("2d");
//		ImageData imageData = ctx.createImageData(width, height);
//		Uint8ClampedArray data = imageData.getData();
//
//		// Convert ARGB (0xAARRGGBB) to RGBA bytes
//		int p = 0;
//		for (int i = 0; i < pixels.length; i++) {
//			int c = pixels[i];
//			int a = (c >>> 24) & 0xFF;
//			int r = (c >>> 16) & 0xFF;
//			int g = (c >>> 8) & 0xFF;
//			int b = c & 0xFF;
//
//			data.set(p++, r);
//			data.set(p++, g);
//			data.set(p++, b);
//			data.set(p++, a);
//		}
//
//		ctx.putImageData(imageData, 0, 0);
//		return canvasToDataUrl(canvas);
//	}
//
//	@JSBody(params = { "canvas" }, script = "return canvas.toDataURL('image/png');")
//	private static native String canvasToDataUrl(HTMLCanvasElement canvas);
	// ::done

}
