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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.awt.FlowLayout;
import java.awt.SystemColor;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextArea txtMain;
	private JButton btnGet;
	private JComboBox cbResolution;
	private JScrollPane spMain;
	private JPanel tabSimple;
	private JPanel tabAdvanced;
	private JComboBox cbFileType;
	private JCheckBox chckbxBestVideo;
	private JCheckBox chckbxBestAudio;
	private JTabbedPane paneOptions;
	private JCheckBox chckbxNumberFileNames;
	private JScrollPane paneInfo;
	private JTextArea txtInfo;

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

		paneOptions = new JTabbedPane(JTabbedPane.TOP);
		paneOptions.setBounds(0, 468, 149, 96);
		contentPane.add(paneOptions);

		tabSimple = new JPanel();
		paneOptions.addTab("Simple", null, tabSimple, null);
		tabSimple.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		cbResolution = new JComboBox();
		tabSimple.add(cbResolution);
		cbResolution.setFont(new Font("Tahoma", Font.PLAIN, 15));
		cbResolution.setModel(new DefaultComboBoxModel(new String[] { "720",
				"360" }));

		cbFileType = new JComboBox();
		cbFileType.setFont(new Font("Tahoma", Font.PLAIN, 15));
		cbFileType.setModel(new DefaultComboBoxModel(new String[] { "mp4",
				"flv", "webm", "3gp", "m4a" }));
		// cbFileType.setToolTipText("Leave blank for no preference.");
		tabSimple.add(cbFileType);

		chckbxNumberFileNames = new JCheckBox("Number file names");
		chckbxNumberFileNames.setSelected(true);
		tabSimple.add(chckbxNumberFileNames);

		tabAdvanced = new JPanel();
		paneOptions.addTab("Advanced", null, tabAdvanced, null);
		tabAdvanced.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		chckbxBestAudio = new JCheckBox("Best Audio");
		chckbxBestAudio.addActionListener(new bestHandler());
		chckbxBestAudio.setSelected(true);
		tabAdvanced.add(chckbxBestAudio);

		chckbxBestVideo = new JCheckBox("Best Video");
		chckbxBestVideo.addActionListener(new bestHandler());
		chckbxBestVideo.setSelected(true);
		tabAdvanced.add(chckbxBestVideo);

		btnGet = new JButton("Get");
		btnGet.setBounds(159, 501, 116, 41);
		contentPane.add(btnGet);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 5, 414, 457);
		contentPane.add(tabbedPane);

		spMain = new JScrollPane();
		tabbedPane.addTab("Input", null, spMain, null);

		txtMain = new JTextArea();
		spMain.setViewportView(txtMain);

		paneInfo = new JScrollPane();
		tabbedPane.addTab("Info", null, paneInfo, null);

		txtInfo = new JTextArea();
		txtInfo.setBackground(SystemColor.control);
		txtInfo.setWrapStyleWord(true);
		txtInfo.setLineWrap(true);
		txtInfo.setText("A java gui that uses youtube-dl to replace the some of the basic functionality of the defuct BYTubeD.\r\n\r\nCurrently a quick and dirty alpha to prove the concept. \r\nPlace in folder with youtube-dl.exe where it will keep it's files. \r\nOpen and place links in text area. \r\nChoose resolution and file type, it will get the best file up to the chosen resolution of that file type. \r\nPress get button. \r\nIf you don't have youtube-dl.exe it will prompt you to download it.\r\nIt creates a html file with the links and open them up in your default browser.\r\nA separate html is created for any errors.\r\n\r\nHaving the advanced tab open when you press the get button will search for the best audio and video file if options are checked. If both checked will return two files BASH files, one with only video and one with only audio. If you aren't sure what that means, or how to use those flies, you probably don't want to be using this option.");
		txtInfo.setEditable(false);
		paneInfo.setViewportView(txtInfo);
		btnGet.addActionListener(new GetHandler());
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

	class bestHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == chckbxBestAudio
					&& !chckbxBestVideo.isSelected()) {
				chckbxBestVideo.setSelected(true);
			} else if (e.getSource() == chckbxBestVideo
					&& !chckbxBestAudio.isSelected()) {
				chckbxBestAudio.setSelected(true);
			}
		}
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
				String statment = "youtube-dl.exe --get-filename --get-url --get-format -f \"";
				if (paneOptions.getSelectedIndex() == 0) {
					statment += cbFileType.getSelectedItem() + "/[height"
							+ "<=" + cbResolution.getSelectedItem() + "]";

				} else if (paneOptions.getSelectedIndex() == 1) {
					statment += getBest();
					// System.out.println(statment);
				}
				try {
					Process pr = rt.exec(statment
							+ "\" -o \"%(title)s.%(ext)s\" "
							+ matrix.get(i).toArray()[0]
							+ " --restrict-filenames");
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
					try {
						ErrorWindow dialog = new ErrorWindow();
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					} catch (Exception e2) {
						e2.printStackTrace();
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
						+ "\n</span>"
						+ "</div>"
						+ "<p/>"
						+ "<div id=\"links\">"
						+ "\n<table border=\"1\" cellpadding=\"5px\">"
						+ "\n<tr><th>S.No</th><th>Title</th><th>Quality</th></tr>";

				for (int i = 0; i < matrix.size(); i++) {
					if (paneOptions.getSelectedIndex() == 1
							&& chckbxBestAudio.isSelected() == chckbxBestVideo
									.isSelected()) {

						htmlText += "\n<tr>"
								+ "\n<td>"
								+ (i + 1)
								+ "</td>"
								+ "\n<td>"
								+ "\n<p><a href=\""
								+ matrix.get(i).get(1).toString()
								+ "\">"
								+ matrix.get(i).get(3).toString()
										.replace("_", " ")
								+ "</a></p>\n"
								+ "\n<p><a href=\""
								+ matrix.get(i).get(2).toString()
								+ "\">"
								+ matrix.get(i)
										.get(3)
										.toString()
										.replace("_", " ")
										.replaceFirst(
												"\\.(mp4|m4a|flv|3gp|webm)$",
												"") + ".m4a</a></p>"
								+ "\n</td>" + "\n<td><span class='"
								+ getColor(matrix.get(i).get(4).toString())
								+ "'>" + matrix.get(i).get(4).toString()
								+ "</span></td>" + "\n</tr>";

					} else {
						htmlText += "\n<tr><td>"
								+ (i + 1)
								+ "</td><td><a href=\""
								+ matrix.get(i).get(1).toString()
								+ "&; codecs&title="
								+ getNumbering(i)
								+ matrix.get(i)
										.get(2)
										.toString()
										.replace("_", " ")
										.replaceFirst(
												"\\.(mp4|m4a|flv|3gp|webm)$",
												"")
								+ "\">"
								+ matrix.get(i).get(2).toString()
										.replace("_", " ")
								+ "</a></td><td><span class='"
								+ getColor(matrix.get(i).get(3).toString())
								+ "'>" + matrix.get(i).get(3).toString()
								+ "</span></td></tr>";
					}

				}

				htmlText += "</table>"
						+ "</div>"
						+ "\n<br/><div class='center gray'>Link Generation Time: "
						+ Date()
						+ "<br/>Link Expiry Time: "
						+ "~6 Hours"
						+ "\n</div><br/><hr size=\"1\" width=\"80%\"/><br/><br/>"
						+ "\n</body>" + "</html>";

				writeToFile("ytdl.html", htmlText);
			}

			if (errorMatrix.size() > 0) {
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
						+ "\n<div class=\"fullwidth center gray\">"
						+ "\n<b>YouTube-DL Invocation Timestamp:</b> "
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

		private String getNumbering(int i) {
			if (chckbxNumberFileNames.isSelected()) {
				return (i + 1) + " - ";
			}
			return "";
		}

		private String getBest() {
			boolean audio = chckbxBestAudio.isSelected();
			boolean video = chckbxBestVideo.isSelected();

			if (audio == video) {
				return "bestvideo+bestaudio";
			} else if (video) {
				return "bestvideo";
			}

			return "bestaudio";
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

		private String getColor(String res) {
			String height = regexFind(res, "x\\d*")[0];

			switch (height) {
			case "x1080":
				return "ruby";
			case "x720":
				return "purple";
			case "x360":
				return "green";
			case "x240":
				return "lightblue";
			default:
				return "black";
			}
		}

		private String[] regexFind(String string, String regex) {
			Pattern MY_PATTERN = Pattern.compile(regex);
			Matcher m = MY_PATTERN.matcher(string);
			ArrayList<String> matches = new ArrayList<String>();
			if (m.find()) {
				do {
					matches.add(m.group(0));
				} while (m.find());
			} else {
				matches.add("");
			}

			return matches.toArray(new String[matches.size()]);
		}

	}
}
