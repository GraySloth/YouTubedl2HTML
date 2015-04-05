package YouTubedl2HTML;

import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextArea txtMain;
	private JButton btnGet;
	private JComboBox cbResolution;
	private JComboBox cbFiletype;
	private JComboBox cbEquation;
	private JCheckBox chckbxBest;
	private JScrollPane scrollPane_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("YouTube-DL HTML Generator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 602);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnGet = new JButton("Get");
		btnGet.addActionListener(new GetHandler());

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 11, 414, 455);
		contentPane.add(scrollPane_1);

		txtMain = new JTextArea();
		scrollPane_1.setViewportView(txtMain);
		btnGet.setBounds(282, 500, 115, 38);
		contentPane.add(btnGet);

		cbResolution = new JComboBox();
		cbResolution.setFont(new Font("Tahoma", Font.PLAIN, 15));
		cbResolution.setModel(new DefaultComboBoxModel(new String[] { "1080",
				"720", "360" }));
		cbResolution.setSelectedIndex(1);
		cbResolution.setBounds(204, 492, 60, 50);
		contentPane.add(cbResolution);

		// cbFiletype = new JComboBox();
		// cbFiletype.setFont(new Font("Tahoma", Font.PLAIN, 15));
		// cbFiletype.setModel(new DefaultComboBoxModel(new String[] { "mp4 ",
		// "m4a ", "3gp ", "flv ", "webm" }));
		// cbFiletype.setBounds(183, 501, 62, 31);
		// contentPane.add(cbFiletype);

		chckbxBest = new JCheckBox("Best");
		chckbxBest.setSelected(true);
		chckbxBest.setBounds(10, 508, 54, 23);
		contentPane.add(chckbxBest);

		cbEquation = new JComboBox();
		cbEquation.setModel(new DefaultComboBoxModel(new String[] { "=", "<=",
				">=" }));
		cbEquation.setSelectedIndex(1);
		cbEquation.setBounds(134, 494, 50, 50);
		contentPane.add(cbEquation);

		JLabel lblHeight = new JLabel("Height:");
		lblHeight.setBounds(74, 503, 50, 32);
		contentPane.add(lblHeight);
	}

	private String Date() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat(
				"E yyyy.MM.dd 'at' hh:mm:ss a zzz");

		return ft.format(dNow);
	}

	private static BufferedReader getOutput(Process p) {
		return new BufferedReader(new InputStreamReader(p.getInputStream()));
	}

	private static BufferedReader getError(Process p) {
		return new BufferedReader(new InputStreamReader(p.getErrorStream()));
	}

	class GetHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			String[] text = txtMain.getText().split("\n");

			ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
			ArrayList<String[]> errorMatrix = new ArrayList<String[]>();
			String htmlText;

			for (int i = 0; i < text.length; i++) {
				matrix.add(new ArrayList<String>());
				matrix.get(i).add(text[i].trim());
			}

			for (int i = 0; i < matrix.size(); i++) {
				Runtime rt = Runtime.getRuntime();
				try {
					Process pr = rt
							.exec("youtube-dl.exe -g -e --get-format -f \""
									+ getBest() + "[height"
									+ cbEquation.getSelectedItem() + ""
									+ cbResolution.getSelectedItem()
									+ "]/mp4\" " + matrix.get(i).toArray()[0]);
					BufferedReader output = getOutput(pr);
					BufferedReader error = getError(pr);
					String ligne = "";

					while ((ligne = output.readLine()) != null) {
						matrix.get(i).add(ligne);
					}

					if ((ligne = error.readLine()) != null) {
						String[] errorArray = new String[2];
						errorArray[1] = ligne;
						while ((ligne = error.readLine()) != null) {
							System.out.println(ligne);
						}
						errorArray[0] = matrix.get(i).get(0);
						errorMatrix.add(errorArray);
						matrix.remove(i);
						i--;
					}

				} catch (IOException e1) {
					int reply = JOptionPane.showConfirmDialog(null,
							"Would you like to download it?",
							"Missing YouTube-dl", JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION) {
						try {
							URL website = new URL(
									"https://yt-dl.org/latest/youtube-dl.exe");
							ReadableByteChannel rbc = Channels
									.newChannel(website.openStream());
							FileOutputStream fos = new FileOutputStream(
									"youtube-dl.exe");
							fos.getChannel().transferFrom(rbc, 0,
									Long.MAX_VALUE);
							JOptionPane.showMessageDialog(null, "Please relaunch program");
							System.exit(0);
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(null, "Could not download, shutting down.");
							System.exit(0);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Shutting down.");
						System.exit(0);
					}
					e1.printStackTrace();
				}
			}
			
			if (matrix.size() > 0) {

				htmlText = "<!DOCTYPE html>"
						+ "\n<html>"
						+ "\n<head>"
						+ "\n<title>YouTube-DL Generated Links</title>"
						+ "\n<meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\">"
						+ "\n<style>"
						+ "\nbody      { font-family:Georgia,Ubuntu,Times,Sans; text-align:justify }"
						+ "\ntable     { border-collapse:collapse; margin-left:auto; margin-right:auto }"
						+ "\n.green    { color:#006600; }"
						+ "\n.gray     { color:#808080; }"
						+ "\n.red      { color:#FF0000; }"
						+ "\n.pink     { color:#FF00C0; }"
						+ "\n.purple   { color:#C000FF; }"
						+ "\n.ruby     { color:#C00000; }"
						+ "\n.lightblue{ color:#4480FF; }"
						+ "\n.center   { text-align:center; }"
						+ "\n.fullwidth{ width:100%; }"
						+ "\n.centerdiv{ margin:auto; }"
						+ "\n.pad20    { padding:20px }"
						+ "\n</style>"
						+ "\n</head>"
						+ "\n<body>"
						+ "\n<br/>"
						+ "\n<div class=\"center\">"
						+ "\n<h2 class=\"green\"> YouTube-DL has generated "
						+ matrix.size()
						+ "\n downloadable video links.</h2>"
						+ "\nInvoke a download manager, such as <b>DownThemAll</b>, on this page to download these videos."
						+ "\n<br/>"
						+ "\n<br/><hr size=\"1\" width=\"80%\"/><br/>"
						+ "\n<span class=\"gray\">"
						+ "\nIf you do not have a download manger, you can click on the links below and download the videos one by one.<br/> "
						+ "\nBut batch download becomes far easier if you have a download manager like DownThemAll.<br/>"
						+ "\n</span>" + "</div>" + "<p/>" + "<div id=\"links\">"
						+ "\n<table border=\"1\" cellpadding=\"5px\">"
						+ "\n<tr><th>S.No</th><th>Title</th><th>Quality</th></tr>";

				for (int i = 0; i < matrix.size(); i++) {

					htmlText += "\n<tr><td>" + (i + 1) + "</td><td><a href=\""
							+ matrix.get(i).get(2).toString() + "&; codecs&title="
							+ (i + 1) + " - "
							+ matrix.get(i).get(1).toString().replace("&", "and")
							+ "\">" + matrix.get(i).get(1).toString()
							+ "</a></td><td><span class='"
							+ getColor(matrix.get(i).get(3).toString()) + "'>" // dynamic
																				// color
																				// res
							+ matrix.get(i).get(3).toString() + "</span></td></tr>"; // fix
																						// res
				}

				htmlText += "</table>" + "</div>"
						+ "\n<br/><div class='center gray'>Link Generation Time: "
						+ Date() + "<br/>Link Expiry Time: " + "~6 Hours"
						+ "\n</div><br/><hr size=\"1\" width=\"80%\"/><br/><br/>"
						+ "\n</body>" + "</html>";

				writeToFile("ytdl.html", htmlText);
			}

			if (errorMatrix.size() > 0) {
				htmlText = "<!DOCTYPE html>"
						+ "\n<html>"
						+ "\n<head>"
						+ "\n<title>BYTubeD Generated Links</title>"
						+ "\n<meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\">"
						+ "\n<style>"
						+ "\nbody      { font-family:Georgia,Ubuntu,Times,Sans; text-align:justify }"
						+ "\ntable     { border-collapse:collapse; margin-left:auto; margin-right:auto }"
						+ "\n.green    { color:#006600; }"
						+ "\n.gray     { color:#808080; }"
						+ "\n.red      { color:#FF0000; }"
						+ "\n.pink     { color:#FF00C0; }"
						+ "\n.purple   { color:#C000FF; }"
						+ "\n.ruby     { color:#C00000; }"
						+ "\n.lightblue{ color:#4480FF; }"
						+ "\n.center   { text-align:center; }"
						+ "\n.fullwidth{ width:100%; }"
						+ "\n.centerdiv{ margin:auto; }"
						+ "\n.pad20    { padding:20px }"
						+ "\n</style>"
						+ "\n</head>"
						+ "\n<body>"
						+ "\n<div class=\"fullwidth center gray\">"
						+ "\n<b>BYTubeD Invocation Timestamp:</b> "
						+ Date()
						+ "<br/>"
						// +
						// "\n<b>Source page:</b> <a href=\"Download page link\">Download page</a>"
						+ "\n</div>"
						+ "\n<br/><h2 class=\"red center\">Failed to generate download links for the following videos.</h2>"
						+ "\n<div id=\"failed_links\" class=\"pad20\">"
						+ "\n<table border=\"1\" cellpadding=\"5px\" style=\"border-collapse:collapse;margin-left:auto;margin-right:auto\">"
						+ "\n<tr><th>S.No</th><th>Title</th><th>Reason for failure</th></tr>";

				for (int j = 0; j < errorMatrix.size(); j++) {
					htmlText += "\n<tr><td>" + (j + 1) + "</td><td>"
							+ errorMatrix.get(j)[0] + "</td><td>"
							+ errorMatrix.get(j)[1] + "</td></tr>";

				}

				htmlText += "\n</table>" + "\n</div>" + "\n</body>"
						+ "\n</html>";

				writeToFile("ytdl-Error.html", htmlText);

			}
		}

		private void writeToFile(String file, String text) {
			File f = new File(file);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write(text);
				bw.close();
				File htmlFile = new File(file);
				Desktop.getDesktop().browse(htmlFile.toURI());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		private String getBest() {
			if (chckbxBest.isSelected()) {
				return "best";
			}
			return "";
		}

		private String getColor(String res) {
			int hight = Integer.parseInt(extractNumber(res));

			switch (hight) {
			case 1080:
				return "ruby";
			case 720:
				return "purple";
			case 360:
				return "green";
			default:
				return "black";
			}
		}

		private String extractNumber(String res) {

			String[] strs = res.split("x");
			String str = "0";

			if (strs.length > 1) {
				str = strs[1];
			}

			if (str == null || str.isEmpty())
				return "";

			StringBuilder sb = new StringBuilder();
			boolean found = false;
			for (char c : str.toCharArray()) {
				if (Character.isDigit(c)) {
					sb.append(c);
					found = true;
				} else if (found) {
					// If we already found a digit before and this char is not a
					// digit, stop looping
					break;
				}
			}

			return sb.toString();
		}
	}
}
