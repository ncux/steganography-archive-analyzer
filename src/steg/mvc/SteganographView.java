package steg.mvc;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;

public class SteganographView extends JFrame {

	private static final long serialVersionUID = 5843614603184849086L;

	private JLabel initialImageLabel = null;

	private JLabel modifiedImageLabel = null;

	private JLabel resultNameLabel = null;

	private JLabel extractedFileNameLabel = null;

	private JTextField resultNameTextField = null;

	private JTextField extractedFileNameTextField = null;

	private JTextArea secretText = null;

	private JButton openInitialImageButton = null;

	private JButton openFileToHideButton = null;

	private JButton hideButton = null;

	private JButton extractButton = null;

	private SteganographModel model = null;

	private JTextArea extractedText = null;

	private JSpinner bitsToUseSpinner = null;

	private JButton gzipButton = null;

	private JButton bzip2Button = null;

	private JButton zipButton = null;

	public SteganographView(SteganographModel model) {
		// super();
		this.model = model;
		initView();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initView() {
		setSize(920, 640);
		getContentPane().setLayout(null);

		bitsToUseSpinner = new JSpinner();
		bitsToUseSpinner.setBounds(30, 0, 200, 30);
		SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 4, 1);
		bitsToUseSpinner.setModel(spinnerModel);
		add(bitsToUseSpinner, null);

		openInitialImageButton = new JButton();
		openInitialImageButton.setBounds(30, 30, 200, 30);
		openInitialImageButton.setText("Open Initial Picture");
		add(openInitialImageButton, null);

		openFileToHideButton = new JButton();
		openFileToHideButton.setBounds(235, 30, 200, 30);
		openFileToHideButton.setText("Open File to Hide");
		add(openFileToHideButton, null);

		resultNameLabel = new JLabel();
		resultNameLabel.setBounds(30, 290, 200, 30);
		resultNameLabel.setText("Result File:");
		add(resultNameLabel, null);

		resultNameTextField = new JTextField();
		resultNameTextField.setBounds(100, 290, 130, 30);
		resultNameTextField.setText("ImageWithSecret");
		add(resultNameTextField, null);

		hideButton = new JButton();
		hideButton.setBounds(30, 325, 200, 30);
		hideButton.setText("Hide text into image");
		add(hideButton, null);

		secretText = new JTextArea();
		secretText.setBounds(235, 65, 200, 200);
		secretText.setBorder(new LineBorder(new Color(0)));
		secretText.setLineWrap(true);
		secretText.setText("Here will be secret Text");
		JScrollPane secretTextPane = new JScrollPane(secretText);
		secretTextPane.setBounds(235, 65, 200, 200);
		secretTextPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(secretTextPane);

		initialImageLabel = new JLabel();
		initialImageLabel.setBounds(30, 65, 200, 200);
		initialImageLabel.setBorder(new LineBorder(new Color(0)));
		initialImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		initialImageLabel.setText("Here will be initial image");
		add(initialImageLabel);

		modifiedImageLabel = new JLabel();
		modifiedImageLabel.setBounds(30, 365, 200, 200);
		modifiedImageLabel.setBorder(new LineBorder(new Color(0)));
		modifiedImageLabel.setText("Here will be modified image");
		add(modifiedImageLabel);

		extractedFileNameLabel = new JLabel();
		extractedFileNameLabel.setBounds(235, 290, 200, 30);
		extractedFileNameLabel.setText("Result File:");
		add(extractedFileNameLabel, null);

		extractedFileNameTextField = new JTextField();
		extractedFileNameTextField.setBounds(305, 290, 130, 30);
		extractedFileNameTextField.setText("Extracted Text");
		add(extractedFileNameTextField, null);

		extractButton = new JButton();
		extractButton.setBounds(235, 325, 200, 30);
		extractButton.setText("Extract text from image");
		add(extractButton, null);

		extractedText = new JTextArea();
		extractedText.setBounds(235, 365, 200, 200);
		extractedText.setBorder(new LineBorder(new Color(0)));
		extractedText.setLineWrap(true);
		extractedText.setText("Here will be extracted text");
		JScrollPane extractedTextPane = new JScrollPane(extractedText);
		extractedTextPane.setBounds(235, 365, 200, 200);
		extractedTextPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(extractedTextPane);

		gzipButton = new JButton();
		gzipButton.setBounds(445, 30, 200, 30);
		gzipButton.setText("GZIP");
		add(gzipButton, null);

		bzip2Button = new JButton();
		bzip2Button.setBounds(445, 65, 200, 30);
		bzip2Button.setText("BZIP2");
		add(bzip2Button, null);

		zipButton = new JButton();
		zipButton.setBounds(445, 100, 200, 30);
		zipButton.setText("ZIP");
		add(zipButton, null);

		setTitle("Steganograph");
	}

	public void addOpenInitialImageListener(ActionListener cal,
			String actionCommand) {
		openInitialImageButton.addActionListener(cal);
		openInitialImageButton.setActionCommand(actionCommand);
	}

	public void addOpenFileToHideListener(ActionListener cal,
			String actionCommand) {
		openFileToHideButton.addActionListener(cal);
		openFileToHideButton.setActionCommand(actionCommand);
	}

	public void addHideListener(ActionListener cal, String actionCommand) {
		hideButton.addActionListener(cal);
		hideButton.setActionCommand(actionCommand);
	}

	public void addExtractListener(ActionListener cal, String actionCommand) {
		extractButton.addActionListener(cal);
		extractButton.setActionCommand(actionCommand);
	}

	public void addGzipListener(ActionListener cal, String actionCommand) {
		gzipButton.addActionListener(cal);
		gzipButton.setActionCommand(actionCommand);
	}

	public void addBzip2Listener(ActionListener cal, String actionCommand) {
		bzip2Button.addActionListener(cal);
		bzip2Button.setActionCommand(actionCommand);
	}

	public void addZipListener(ActionListener cal, String actionCommand) {
		zipButton.addActionListener(cal);
		zipButton.setActionCommand(actionCommand);
	}

	public Integer getBitsToUseSpinnerValue() {
		return (Integer) bitsToUseSpinner.getModel().getValue();
	}

	public String getSecretFileName() {
		return resultNameTextField.getText();
	}

	public String getExtractedInfoName() {
		return extractedFileNameTextField.getText();
	}

	public void setInitialImage(BufferedImage myPicture) {
		if (myPicture == null) {
			return;
		}
		initialImageLabel.setText(null);
		initialImageLabel.setIcon(new ImageIcon(myPicture));
	}

	public void setModifiedImage(BufferedImage myPicture) {
		if (myPicture == null) {
			return;
		}
		modifiedImageLabel.setText(null);
		modifiedImageLabel.setIcon(new ImageIcon(myPicture));
	}

	public void setTextToHide(String text) {
		secretText.setText(text);
	}

	public void setExtractedText(String text) {
		extractedText.setText(text);
	}
}
