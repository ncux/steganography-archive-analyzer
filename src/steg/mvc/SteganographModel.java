package steg.mvc;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import steg.bitstream.BitInputStream;
import steg.bitstream.BitOutputStream;

public class SteganographModel {

	final int BITS_TO_USE = 2;

	final int NUMBER_OF_COLOR_CHANNELS = 3;

	final int BITS_TO_READ = NUMBER_OF_COLOR_CHANNELS * BITS_TO_USE;

	// Hide
	private File sourceFile = null;

	private File outputFile = null;

	private File secretFile = null;

	// Extract
	private File resultFile = null;

	public void setInitialImage(File selectedFile) {
		sourceFile = selectedFile;
	}

	public void setFileToHide(File selectedFile) {
		secretFile = selectedFile;
	}

	public void setNameFileWithSecret(String secretFileName) {
		if (secretFileName == null) {
			return;
		}
		outputFile = new File(sourceFile.getParentFile().getAbsolutePath()
				+ "\\" + secretFileName + ".bmp");
	}

	public void hideInformation() {

		if (sourceFile == null || secretFile == null || outputFile == null) {
			return;
		}

		BitInputStream theBitStream = null;
		try {
			theBitStream = new BitInputStream(new FileInputStream(secretFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedImage sourceImage = null;
		WritableRaster raster = null;// for get pixels
		// getting colors
		try {
			sourceImage = ImageIO.read(sourceFile);
			raster = sourceImage.getRaster();
			int[] readedTextToSave = null;
			boolean isEverythingWriteln = false;
			for (int i = 0; i < raster.getWidth() && !isEverythingWriteln; i++) {
				for (int j = 0; j < raster.getHeight() && !isEverythingWriteln; j++) {
					int[] pixel = raster.getPixel(i, j,
							new int[raster.getWidth() * raster.getHeight()]);
					readedTextToSave = new int[BITS_TO_READ];
					try {
						readedTextToSave = theBitStream.readBit(BITS_TO_READ);
					} catch (EOFException eof) {
						isEverythingWriteln = true;
					}
					int[] newPixel = hideBits(pixel, readedTextToSave);
					raster.setPixel(i, j, newPixel);
				}
			}
			// sourceImage.setData(raster);
			outputFile.createNewFile();
			ImageIO.write(sourceImage, "bmp", outputFile);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Set int values into pixel's colors
	 * 
	 * @param pixel
	 * @param bitsToSave
	 * @return changed pixel
	 */
	private int[] hideBits(int[] pixel, int[] bitsToSave) {
		int[] resultedPixel = new int[NUMBER_OF_COLOR_CHANNELS];
		// offset for bits to save
		int bitsOffset = 0;
		for (int i = 0; i < NUMBER_OF_COLOR_CHANNELS; i++) {
			byte byteValue = (byte) pixel[i];

			// Clear last bits BITS_TO_USE
			int mask = 0xFF - BITS_TO_USE;
			resultedPixel[i] = byteValue & mask;
			if (resultedPixel[i] < 0) {
				resultedPixel[i] = 256 + resultedPixel[i];
			}

			// result[i] += readedTextToSave[secretOffset] >> 3;
			// Set bits
			for (int j = 0; j < BITS_TO_USE; j++) {
				resultedPixel[i] = setBit(resultedPixel[i],
						bitsToSave[bitsOffset], j);
				bitsOffset += 1;
			}
		}
		return resultedPixel;
	}

	/**
	 * Set bit in the byte at index offset
	 * 
	 * @param b changed byte type of int
	 * @param bit bit to set
	 * @param index offset
	 * @return
	 */
	private int setBit(int b, int bit, int index) {
		int mask = 1;
		mask <<= index;

		if (bit == 1) {
			b |= mask;
		} else {
			b &= ~mask;
		}
		return b;
	}

	public void setNameOfExtractedInfo(String extractedInfoName) {
		if (extractedInfoName == null) {
			return;
		}
		resultFile = new File(sourceFile.getParentFile().getAbsolutePath()
				+ "\\" + extractedInfoName);
	}

	public void extractInformation() {
		if (resultFile == null || outputFile == null) {
			return;
		}

		BitOutputStream theBitOutputStream = null;
		try {
			theBitOutputStream = new BitOutputStream(new FileOutputStream(
					resultFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		BufferedImage sourceImage = null;
		WritableRaster raster = null;// for get pixels
		// getting colors
		try {
			sourceImage = ImageIO.read(outputFile);
			raster = sourceImage.getRaster();
			boolean isEverythingWriteln = false;
			for (int i = 0; i < raster.getWidth() && !isEverythingWriteln; i++) {
				for (int j = 0; j < raster.getHeight() && !isEverythingWriteln; j++) {
					int[] pixel = raster.getPixel(i, j,
							new int[raster.getWidth() * raster.getHeight()]);
					int[] secretBits = getSecretBits(pixel);
					theBitOutputStream.writeBits(secretBits);
				}
			}
			theBitOutputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Get secret bits from pixel
	 * 
	 * @param pixel
	 * @return secretBits
	 */
	private int[] getSecretBits(int[] pixel) {
		int[] secretBits = new int[BITS_TO_READ];
		int offsetBits = 0;
		for (int i = 0; i < NUMBER_OF_COLOR_CHANNELS; i++) {
			int color = pixel[i];
			for (int j = 0; j < BITS_TO_USE; j++) {
				secretBits[offsetBits] = getBit(color, j);
				offsetBits += 1;
			}
		}
		return secretBits;
	}

	/**
	 * Get bit from integer value at offset
	 * 
	 * @param b int value
	 * @param index offset
	 * @return bit
	 */
	private int getBit(int b, int index) {
		int bit = 0;

		int mask = 1;
		mask <<= index;

		bit = b & mask;
		if (bit > 0) {
			bit = 1;
		}
		return bit;
	}

	public BufferedImage getModifiedImage() {
		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return myPicture;
	}

	public BufferedImage getInitialImage() {
		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(sourceFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return myPicture;
	}

	public String getTextToHide() {
		char[] b = new char[(int) secretFile.length()];
		try {
			new BufferedReader(new FileReader(secretFile)).read(b);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return String.valueOf(b, 0, (int) secretFile.length());
	}

	public String getExtractedText() {
		char[] b = new char[(int) resultFile.length()];
		try {
			new BufferedReader(new FileReader(resultFile)).read(b);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return String.valueOf(b, 0, (int) resultFile.length());
	}
}
