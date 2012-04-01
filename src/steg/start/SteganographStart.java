package steg.start;

import steg.mvc.SteganographCtrl;
import steg.mvc.SteganographModel;
import steg.mvc.SteganographView;

public class SteganographStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SteganographModel steganographModel = new SteganographModel();
		SteganographView steganographView = new SteganographView(
				steganographModel);
		SteganographCtrl steganographCtrl = new SteganographCtrl(
				steganographView, steganographModel);
		steganographView.setVisible(true);
	}
}
