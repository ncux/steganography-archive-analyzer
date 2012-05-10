package steg.mvc;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import steg.common.SteganographConstants;
import steg.exception.WarningException;

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
		view.addGzipListener(new SteganographViewListener(),
				SteganographConstants.GZIP);
		view.addBzip2Listener(new SteganographViewListener(),
				SteganographConstants.BZIP2);
		view.addZipListener(new SteganographViewListener(),
				SteganographConstants.ZIP);
		// view.addBitsToUseSpinnerListener(new SteganographViewListener());
	}

	class SteganographViewListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			try {
				String straction = event.getActionCommand();
				if (straction.equals(SteganographConstants.OPEN_INITIAL_IMAGE)) {
					performOpenInitialImage();
				} else if (straction
						.equals(SteganographConstants.OPEN_FILE_TO_HIDE)) {
					performOpenFileToHide();
				} else if (straction.equals(SteganographConstants.HIDE)) {
					performHide();
				} else if (straction.equals(SteganographConstants.EXTRACT)) {
					performExtract();
				} else if (straction.equals(SteganographConstants.GZIP)) {
					performGzip();
				} else if (straction.equals(SteganographConstants.BZIP2)) {
					performBzip2();
				} else if (straction.equals(SteganographConstants.ZIP)) {
					performZip();
				}
			} catch (WarningException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Warning!",
						MessageType.WARNING.ordinal());

			}
		}

		private void performZip() throws WarningException {
			model.zip();
		}

		private void performBzip2() throws WarningException {
			model.bzip2();
		}

		private void performGzip() throws WarningException {
			model.gzip();
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

		private void performHide() throws WarningException {
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
