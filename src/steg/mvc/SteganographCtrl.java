package steg.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import steg.common.SteganographConstants;

public class SteganographCtrl {

	private SteganographView view;

	private SteganographModel model;

	public SteganographCtrl(SteganographView steganographView,
			SteganographModel steganographModel) {
		view = steganographView;
		model = steganographModel;

		view.addOpenInitialImageListener(new SteganographViewListener(),
				SteganographConstants.OPEN_INITIAL_IMAGE);
		view.addOpenFileToHideListener(new SteganographViewListener(),
				SteganographConstants.OPEN_FILE_TO_HIDE);
		view.addHideListener(new SteganographViewListener(),
				SteganographConstants.HIDE);
		view.addExtractListener(new SteganographViewListener(),
				SteganographConstants.EXTRACT);
//		view.addBitsToUseSpinnerListener(new SteganographViewListener());
	}

	class SteganographViewListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			String straction = event.getActionCommand();
			if (straction.equals(SteganographConstants.OPEN_INITIAL_IMAGE)) {
				performOpenInitialImage();
			}
			if (straction.equals(SteganographConstants.OPEN_FILE_TO_HIDE)) {
				performOpenFileToHide();
			}
			if (straction.equals(SteganographConstants.HIDE)) {
				performHide();
			}
			if (straction.equals(SteganographConstants.EXTRACT)) {
				performExtract();
			}
		}

		private void performOpenInitialImage() {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"BMP Images", "bmp");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				model.setInitialImage(chooser.getSelectedFile());
				view.setInitialImage(model.getInitialImage());
			}

		}

		private void performOpenFileToHide() {
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				model.setFileToHide(chooser.getSelectedFile());
				view.setTextToHide(model.getTextToHide());
			}

		}

		private void performHide() {
			model.setBitsToUse(view.getBitsToUseSpinnerValue());
			model.createFileWithSecret(view.getSecretFileName());
			model.hideInformation();
			view.setModifiedImage(model.getModifiedImage());
		}

		private void performExtract() {
			model.setNameOfExtractedInfo(view.getExtractedInfoName());
			model.extractInformation(); 
			view.setExtractedText(model.getExtractedText());
		}
	}
}
