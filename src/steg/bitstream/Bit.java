package steg.bitstream;

public class Bit {

	private int bit = 0;

	public Bit(int bit) {
		setBit(bit);
	}

	public int getValue() {
		return bit;
	}

	public void setBit(int bit) {
		if (bit > 0) {
			this.bit = 1;
		} else {
			this.bit = 0;
		}
	}

	public String toString() {
		return String.valueOf(bit);
	}
}
