package steg.bitstream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class for reading file by bits.
 * 
 * @author psix
 */
public class BitInputStream {
	private InputStream in;
	private int buffer;
	private int nextBit = 8;

	/**
	 * @param in
	 */
	public BitInputStream(InputStream in) {
		this.in = in;
	}

	synchronized public int readBit() throws IOException {
		if (in == null)
			throw new IOException("Already closed");

		if (nextBit == 8) {
			buffer = in.read();

			if (buffer == -1)
				throw new EOFException();

			nextBit = 0;
		}

		int bit = buffer & (1 << nextBit);
		nextBit++;

		bit = bit == 0 ? 0 : 1;

		return bit;
	}

	synchronized public int[] readBit(int size) throws IOException {
		int[] bits = new int[size];
		for (int i = 0; i < size; i++) {
			if (in == null)
				throw new IOException("Already closed");

			if (nextBit == 8) {
				buffer = in.read();

				if (buffer == -1)
					throw new EOFException();

				nextBit = 0;
			}
			int bit = buffer & (1 << nextBit);
			nextBit++;

			bit = bit == 0 ? 0 : 1;

			bits[i] = bit;
		}
		return bits;
	}

	public void close() throws IOException {
		in.close();
		in = null;
	}
}