package steg.bitstream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for writing files by bits.
 * 
 * @author psix
 */
public class BitOutputStream {
	private OutputStream out;

	private int buffer;

	private int bitCount;

	public BitOutputStream(OutputStream out) {
		this.out = out;
	}

	synchronized public void writeBit(Bit bit) throws IOException {
		if (out == null)
			throw new IOException("Already closed");

		buffer |= bit.getValue() << bitCount;
		bitCount++;

		if (bitCount == 8) {
			flush();
		}
	}

	synchronized public void writeBits(Bit[] bits) throws IOException {
		for (int i = 0; i < bits.length; i++) {
			writeBit(bits[i]);
		}
	}

	private void flush() throws IOException {
		if (bitCount > 0) {
			out.write((byte) buffer);
			bitCount = 0;
			buffer = 0;
		}
	}

	public void close() throws IOException {
		flush();
		out.close();
		out = null;
	}
}