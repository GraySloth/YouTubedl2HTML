package YouTubedl2HTML;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;

import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.border.BevelBorder;

import java.awt.Font;

public class ErrorWindow extends JDialog {

	private final JPanel contentPanel = new JPanel();

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
		{
			JTextArea txtrThisProgramRequires = new JTextArea();
			txtrThisProgramRequires.setFont(new Font("Monospaced", Font.PLAIN, 15));
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
				JButton btnGoToWebsite = new JButton("Go to website");
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
				JButton btnDownload = new JButton("Download");
				btnDownload.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							URL website = new URL(
									"https://yt-dl.org/latest/youtube-dl.exe");
							ReadableByteChannel rbc = Channels
									.newChannel(website.openStream());
							FileOutputStream fos = new FileOutputStream(
									"youtube-dl.exe");
							fos.getChannel().transferFrom(rbc, 0,
									Long.MAX_VALUE);
							fos.close();
							JOptionPane.showMessageDialog(null,
									"Downlaod was successful.");
							dispose();
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(null,
									"Could not download.");
						}
					}
				});
				btnDownload.setActionCommand("Download");
				buttonPane.add(btnDownload);
				getRootPane().setDefaultButton(btnDownload);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
