package YouTubedl2HTML;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;

import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import javax.swing.border.BevelBorder;

import java.awt.Font;

import javax.swing.JProgressBar;

public class ErrorWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7745875822441214872L;
	private final JPanel contentPanel = new JPanel();
	private JProgressBar progressBar;
	private JPanel progressPanel;
	private JButton cancelButton;
	private JButton btnGoToWebsite;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ErrorWindow dialog = new ErrorWindow();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ErrorWindow() {
		setTitle("Error: Missing YouTube-dl");
		setBounds(100, 100, 450, 209);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

		final CancelHandler Cancel = new CancelHandler();

		{
			JTextArea txtrThisProgramRequires = new JTextArea();
			txtrThisProgramRequires.setFont(new Font("Monospaced", Font.PLAIN,
					15));
			txtrThisProgramRequires.setEditable(false);
			txtrThisProgramRequires.setBackground(SystemColor.control);
			txtrThisProgramRequires.setWrapStyleWord(true);
			txtrThisProgramRequires.setLineWrap(true);
			txtrThisProgramRequires
					.setText("This program requires youtube-dl.exe in the same folder to function.\r\nYou can visit the youtube-dl website by pressing the go to website button,\r\nOr you can press the download button and it will be downloaded automatically.");
			contentPanel.add(txtrThisProgramRequires);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnGoToWebsite = new JButton("Go to website");
				btnGoToWebsite.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							Desktop.getDesktop().browse(
									new URI("http://youtube-dl.org/"));
						} catch (IOException | URISyntaxException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						dispose();
					}
				});
				buttonPane.add(btnGoToWebsite);
			}
			{
				final JButton btnDownload = new JButton("Download");
				final AtomicBoolean running = new AtomicBoolean(false);

				btnDownload.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						running.set(!running.get());
						btnDownload.setText(running.get() ? "Pause"
								: "Continue");

						if (!progressPanel.isVisible()) {
							progressPanel.setVisible(true);
							contentPanel.setVisible(false);
							new Thread() {

								public void run() {
									Download file = null;
									try {
										file = new Download(
												new URL(
														"https://yt-dl.org/latest/youtube-dl.exe"));
										// http://www.wswd.net/testdownloadfiles/5MB.zip
										// https://yt-dl.org/latest/youtube-dl.exe
									} catch (MalformedURLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									cancelButton.removeActionListener(Cancel);
									CancelHandler CancelDownload = new CancelHandler(
											file);
									cancelButton
											.addActionListener(new CancelHandler(
													file));

									while (file.getStatus() < 2) {
										progressBar.setValue((int) file
												.getProgress());
										LockSupport
												.parkNanos(TimeUnit.MILLISECONDS
														.toNanos(200));
										if (btnDownload.getText() == "Continue"
												&& file.getStatus() == 0) {
											file.pause();
										} else if (btnDownload.getText() == "Pause"
												&& file.getStatus() == 1) {
											file.resume();
										}
									}
									cancelButton
											.removeActionListener(CancelDownload);
									cancelButton.addActionListener(Cancel);
									running.set(false);
									if (file.getStatus() == 2) {
										cancelButton.setText("OK");
										btnGoToWebsite.setVisible(false);
										btnDownload.setVisible(false);
										progressBar.setValue(progressBar
												.getMaximum());
										progressBar.setBorder(BorderFactory
												.createTitledBorder("Download successful."));
									} else {
										btnDownload.setText("Download");
										progressPanel.setVisible(false);
										contentPanel.setVisible(true);
									}
									if (file.getStatus() == 4) {
										JOptionPane.showMessageDialog(null,
												"An error occured.");
									}
								}
							}.start();
						}
					}
				});
				btnDownload.setActionCommand("Download");
				buttonPane.add(btnDownload);
				getRootPane().setDefaultButton(btnDownload);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(Cancel);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			progressPanel = new JPanel();
			getContentPane().add(progressPanel, BorderLayout.NORTH);
			{
				progressBar = new JProgressBar();
				progressBar.setStringPainted(true);
				progressBar.setBorder(BorderFactory
						.createTitledBorder("Download file"));
				progressPanel.add(progressBar);
			}
			progressPanel.setVisible(false);
		}
	}

	class CancelHandler implements ActionListener {
		Download file;

		public CancelHandler() {
			file = null;
		}

		public CancelHandler(Download file) {
			this.file = file;
		}

		public void actionPerformed(ActionEvent e) {
			if (file != null) {
				file.cancel();
			} else {
				dispose();
			}
		}
	}

}
