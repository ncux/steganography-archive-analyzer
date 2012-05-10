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
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.tools.bzip2.CBZip2OutputStream;

import steg.bitstream.Bit;
import steg.bitstream.BitInputStream;
import steg.bitstream.BitOutputStream;
import steg.exception.WarningException;

public class SteganographModel {

	private int bitsToUse = 0;

	final int NUMBER_OF_COLOR_CHANNELS = 3;

	// Hide
	private File sourceFile = null;

	private File outputFile = null;

	private File secretFile = null;

	private BitInputStream bitStream = null;

	// Extract
	private File resultFile = null;

	public void setInitialImage(File selectedFile) {
		sourceFile = selectedFile;
	}

	public void setFileToHide(File selectedFile) {
		secretFile = selectedFile;
	}

	public void createFileWithSecret(String secretFileName) {
		if (secretFileName == null) {
			return;
		}
		outputFile = new File(sourceFile.getParentFile().getAbsolutePath()
				+ "\\" + secretFileName + ".bmp");
	}

	public void hideInformation() throws WarningException {
		if (sourceFile == null || secretFile == null || outputFile == null) {
			return;
		}

		try {
			bitStream = new BitInputStream(new FileInputStream(secretFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		BufferedImage sourceImage = null;
		WritableRaster raster = null;// for get pixels
		validataLength();
		// getting colors
		try {
			sourceImage = ImageIO.read(sourceFile);
			raster = sourceImage.getRaster();
			boolean isFinished = false;
			for (int i = 0; i < raster.getWidth() && !isFinished; i++) {
				for (int j = 0; j < raster.getHeight() && !isFinished; j++) {
					int[] pixel = raster.getPixel(i, j,
							new int[raster.getWidth() * raster.getHeight()]);
					Bit[] bitsToSave = getBitsToSave();
					if (bitsToSave == null) {
						isFinished = true;
						break;
						// bitsToSave = new Bit[getNumberOfBitsToRead()];
						// for (int k = 0; k < bitsToSave.length; k++) {
						// bitsToSave[k] = new Bit(0);
						// }
					}
					int[] newPixel = hideBits(pixel, bitsToSave);
					raster.setPixel(i, j, newPixel);
				}
			}
			outputFile.createNewFile();
			ImageIO.write(sourceImage, "bmp", outputFile);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (bitStream != null) {
					bitStream.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private void validataLength() throws WarningException {
		long maxSizeToSave = sourceFile.length() * bitsToUse / 8;
		long realSizeToSave = secretFile.length();

		if (realSizeToSave > maxSizeToSave) {
			throw new WarningException(
					"Secret data can't be saved into current image"
							+ "\nReal size: " + realSizeToSave + "\nMaxSize: "
							+ maxSizeToSave);
		}
	}

	private Bit[] getBitsToSave() throws IOException {
		Bit[] bitsToSave = null;
		try {
			bitsToSave = bitStream.readBits(getNumberOfBitsToRead());
		} catch (EOFException eof) {
			// 1. write everything
			// bitStream.close();
			// bitStream = new BitInputStream(new
			// FileInputStream(secretFile));
			// bitsToSave = bitStream.readBits(getNumberOfBitsToRead());
			// 2. stop
			// bitsToSave = null;
		}
		return bitsToSave;
	}

	/**
	 * Set int values into pixel's colors
	 * 
	 * @param pixel
	 * @param bitsToSave
	 * @return changed pixel
	 */
	private int[] hideBits(int[] pixel, Bit[] bitsToSave) {
		int[] resultedPixel = new int[NUMBER_OF_COLOR_CHANNELS];
		if (bitsToSave == null) {
			return pixel;
		}
		// offset for bits to save
		int bitsOffset = 0;
		for (int i = 0; i < NUMBER_OF_COLOR_CHANNELS; i++) {
			byte byteValue = (byte) pixel[i];

			// Clear last bits BITS_TO_USE
			int mask = 0xFF;// 0xFF - BITS_TO_USE;
			if (bitsToUse == 1) {
				mask = 0xFE;
			} else if (bitsToUse == 2) {
				mask = 0xFC;
			} else if (bitsToUse == 3) {
				mask = 0xF8;
			} else if (bitsToUse == 4) {
				mask = 0xF0;
			}

			resultedPixel[i] = byteValue & mask;
			if (resultedPixel[i] < 0) {
				resultedPixel[i] = 256 + resultedPixel[i];
			}

			// result[i] += readedTextToSave[secretOffset] >> 3;
			// Set bits
			for (int j = 0; j < bitsToUse; j++) {
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
	private int setBit(int b, Bit bit, int index) {
		int mask = 1;
		mask <<= index;

		if (bit.getValue() == 1) {
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
					Bit[] secretBits = getSecretBits(pixel);
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
	private Bit[] getSecretBits(int[] pixel) {
		Bit[] secretBits = new Bit[getNumberOfBitsToRead()];
		int offsetBits = 0;
		for (int i = 0; i < NUMBER_OF_COLOR_CHANNELS; i++) {
			int color = pixel[i];
			for (int j = 0; j < bitsToUse; j++) {
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
	private Bit getBit(int b, int index) {
		int bit = 0;

		int mask = 1;
		mask <<= index;

		bit = b & mask;
		if (bit > 0) {
			bit = 1;
		}
		return new Bit(bit);
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

	public void setBitsToUse(Integer bitsToUseSpinnerValue) {
		bitsToUse = bitsToUseSpinnerValue;
	}

	private int getNumberOfBitsToRead() {
		return bitsToUse * NUMBER_OF_COLOR_CHANNELS;
	}

	public void gzip() throws WarningException {
		if (outputFile == null) {
			throw new WarningException("Output file is null");
		}

		try {
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
					outputFile.getName() + ".gz"));

			// Open the input file
			FileInputStream in = new FileInputStream(outputFile);

			// Transfer bytes from the input file to the GZIP output stream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();

			// Complete the GZIP file
			out.finish();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void bzip2() throws WarningException {
		if (outputFile == null) {
			throw new WarningException("Output file is null");
		}

		try {
			CBZip2OutputStream out = new CBZip2OutputStream(
					new FileOutputStream(outputFile.getName() + ".bz2"));

			// Open the input file
			FileInputStream in = new FileInputStream(outputFile);

			// Transfer bytes from the input file to the GZIP output stream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();

			// Complete the GZIP file
			// out.finish();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void zip() throws WarningException {
		if (outputFile == null) {
			throw new WarningException("Output file is null");
		}

		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					outputFile.getName() + ".zip"));

			// Open the input file
			FileInputStream in = new FileInputStream(outputFile);

			ZipEntry entry = new ZipEntry(outputFile.getName());
			out.putNextEntry(entry);
			// Transfer bytes from the input file to the GZIP output stream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();

			// Complete the GZIP file
			out.finish();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
