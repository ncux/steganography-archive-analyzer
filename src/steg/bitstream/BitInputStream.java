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

	synchronized public Bit readBit() throws IOException {
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

		return new Bit(bit);
	}

	synchronized public Bit[] readBits(int size) throws IOException {
		Bit[] bits = new Bit[size];
		for (int i = 0; i < size; i++) {
			bits[i] = readBit();
		}
		return bits;
	}

	public void close() throws IOException {
		in.close();
		in = null;
	}
}