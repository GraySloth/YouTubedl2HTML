package YouTubedl2HTML;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Document;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.awt.FlowLayout;
import java.awt.SystemColor;

import javax.swing.JProgressBar;

import java.awt.CardLayout;

import javax.swing.JToggleButton;
import java.awt.Panel;
import javax.swing.BoxLayout;

public class Main extends JFrame {

	public final static String HTML_PAGE_STYLE = "\t\t <style>\n"
			+ "\t\t\t body      { font-family:Georgia,Ubuntu,Times,Sans; text-align:justify }\n"
			+ "\t\t\t table     { border-collapse:collapse; margin-left:auto; margin-right:auto }\n"
			+ "\t\t\t .green    { color:#006600; }\n"
			+ "\t\t\t .gray     { color:#808080; }\n"
			+ "\t\t\t .red      { color:#FF0000; }\n"
			+ "\t\t\t .pink     { color:#FF00C0; }\n"
			+ "\t\t\t .purple   { color:#C000FF; }\n"
			+ "\t\t\t .ruby     { color:#C00000; }\n"
			+ "\t\t\t .lightblue{ color:#4480FF; }\n"
			+ "\t\t\t .center   { text-align:center; }\n"
			+ "\t\t\t .fullwidth{ width:100%; }\n"
			+ "\t\t\t .centerdiv{ margin:auto; }\n"
			+ "\t\t\t .pad20    { padding:20px }\n" + "\t\t </style>\n";
	private volatile boolean canceled = false;

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
	private JProgressBar progressBar;
	private JPanel mainPane;
	private JPanel progressPane;
	private JTextArea progressText;
	private JScrollPane spProgress;
	private Panel progressTool;
	private JButton btnCancel;
	private Panel panelCancel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
					frame.toFront();
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
		contentPane.setLayout(new CardLayout(0, 0));

		mainPane = new JPanel();
		contentPane.add(mainPane, "name_387393826283044");
		mainPane.setLayout(null);

		paneOptions = new JTabbedPane(JTabbedPane.TOP);
		paneOptions.setBounds(0, 458, 149, 96);
		mainPane.add(paneOptions);

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

		chckbxNumberFileNames = new JCheckBox("Numbered file names");
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
		btnGet.addActionListener(new GetHandler());
		btnGet.setBounds(159, 491, 116, 41);
		mainPane.add(btnGet);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 414, 457);
		mainPane.add(tabbedPane);

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
		txtInfo.setText("A java gui that uses youtube-dl to replace the some of the basic functionality of the defuct BYTubeD\n"
				+ "\n"
				+ "Not particularly complex or flexible, just what I need for my everyday browsing habits.\n"
				+ "Place in  folder with youtube-dl.exe where it will keep it's files. \n"
				+ "Open and place videoe it will keep it's files. \n"
				+ "Open and place links in text area. \n"
				+ "Choose resolution and file type, it will get the best file up to the chosen resolution of that file type. \n"
				+ "Press get button. \n"
				+ "If you don't have youtube-dl.exe it will prompt you to download it.\n"
				+ "It creates a html file with the links and open them up in your default browser.\n"
				+ "A separate html is created for any errors.\n"
				+ "\n"
				+ "Having the advanced tab open when you press the get button will search for the best audio and video file if options are checked. \n"
				+ "If both checked will return two files BASH files, one with only video and one with only audio. \n"
				+ "If you aren't sure what that means, or how to use those flies, you probably don't want to be using this option.");

		txtInfo.setEditable(false);
		paneInfo.setViewportView(txtInfo);

		progressPane = new JPanel();
		contentPane.add(progressPane, "name_387393847698746");
		progressPane.setLayout(new BorderLayout(0, 0));

		spProgress = new JScrollPane();
		progressPane.add(spProgress, BorderLayout.CENTER);

		progressText = new JTextArea();

		spProgress.setViewportView(progressText);
		progressText.setFont(new Font("Monospaced", Font.PLAIN, 16));
		progressText.setEditable(false);
		progressText.setLineWrap(true);
		progressText.setWrapStyleWord(true);

		progressTool = new Panel();
		progressPane.add(progressTool, BorderLayout.SOUTH);
		progressTool.setLayout(new BorderLayout(0, 0));

		progressBar = new JProgressBar();
		progressTool.add(progressBar, BorderLayout.SOUTH);
		progressBar.setStringPainted(true);

		panelCancel = new Panel();
		progressTool.add(panelCancel, BorderLayout.NORTH);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((CardLayout) contentPane.getLayout()).first(contentPane);
				canceled = true;
			}
		});
		panelCancel.add(btnCancel);

		SetFocus();
	}

	private void ontop(boolean state) {
		this.setAlwaysOnTop(state);
	}

	private void SetFocus() {
		new Thread() {
			public void run() {
				while (!txtMain.hasFocus()) {
					txtMain.requestFocus();
					try {
						sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					;
				}
			}
		}.start();
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
		final ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
		final ArrayList<String[]> errorMatrix = new ArrayList<String[]>();

		public void actionPerformed(ActionEvent e) {

			progressBar.setValue(0);
			progressBar.setBorder(BorderFactory
					.createTitledBorder("Fechting links: "));
			progressText.setText("");
			matrix.clear();
			errorMatrix.clear();

			CardLayout cl = (CardLayout) (contentPane.getLayout());
			cl.last(contentPane);

			String[] text = CleanInput(txtMain.getText().split("\n"));

			for (int i = 0; i < text.length; i++) {
				matrix.add(new ArrayList<String>());
				matrix.get(i).add(text[i].trim());
			}

			// setContentPane(loadingPane);
			progressBar.setMaximum(matrix.size());
			new Thread() {

				public void run() {

					for (int i = 0; i < matrix.size(); i++) {
						boolean isError = false;
						Runtime rt = Runtime.getRuntime();
						String statment = "youtube-dl.exe --get-filename --get-url --get-format -f \"";
						if (paneOptions.getSelectedIndex() == 0) {
							statment += cbFileType.getSelectedItem()
									+ "/[height" + "<="
									+ cbResolution.getSelectedItem() + "]";

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
								isError = true;
							}

						} catch (IOException e1) {
							try {
								ErrorWindow dialog = new ErrorWindow();
								dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
								dialog.setVisible(true);
								CardLayout cl = (CardLayout) (contentPane
										.getLayout());
								cl.first(contentPane);
							} catch (Exception e2) {
								e2.printStackTrace();
							}
							e1.printStackTrace();
						}

						if (isError) {
							progressText
									.append("Error with: ["
											+ errorMatrix.get(errorMatrix
													.size() - 1)[0] + "]\n\n");
						} else {
							int code = 2;
							if (paneOptions.getSelectedIndex() == 1
									&& chckbxBestAudio.isSelected() == chckbxBestVideo
											.isSelected()) {
								code = 3;
							}
							progressText.append("Request for ["
									+ matrix.get(i).get(code).replace("_", " ")
									+ "] has been successfully processed.\n\n");
						}
						Document d = progressText.getDocument();
						progressText.select(d.getLength(), d.getLength());
						progressBar.setValue(i + 1);
						progressBar.setBorder(BorderFactory
								.createTitledBorder("Fetching links: "
										+ (i + 1) + "/" + matrix.size()));
						LockSupport.parkNanos(TimeUnit.MILLISECONDS
								.toNanos(200));
						if (canceled) {
							canceled = false;
							Thread.currentThread().interrupt();
							return;
						}
					}
					printHTML();

				}
			}.start();
		}

		private String[] CleanInput(String[] text) {
			ArrayList<String> newText = new ArrayList<String>();
			for (String string : text) {
				if (string.trim().length() > 0
						&& !string.toLowerCase().contains("com/user/")
						&& !string.toLowerCase().contains("com/channel/")
						&& !string.toLowerCase().contains("com/playlist")) {
					newText.add(string);
				}
			}

			return newText.toArray(new String[newText.size()]);
		}

		private void printHTML() {
			String htmlText;
			if (matrix.size() > 0) {

				htmlText = "<!DOCTYPE html>\n"
						+ " <html>\n"
						+ "\t <head>\n"
						+ "\t\t <title>YouTube-DL Generated Links</title>\n"
						+ "\t\t <meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\">\n"
						+ HTML_PAGE_STYLE
						+ "\t </head>\n"
						+ "\t <body>\n"
						+ "\t\t <br/>\n"
						+ "\t\t <div class=\"center\">\n"
						+ "\t\t\t <h2 class=\"green\"> YouTube-DL has generated "
						+ matrix.size()
						+ " downloadable video links.</h2>\n"
						+ "\t\t\t Invoke a download manager, such as <b>DownThemAll</b>, on this page to download these videos.\n"
						+ "\t\t\t <br/>\n"
						+ "\t\t\t <br/><hr size=\"1\" width=\"80%\"/><br/>\n"
						+ "\t\t\t <span class=\"gray\">\n"
						+ "\t\t\t\t If you do not have a download manger, you can click on the links below and download the videos one by one.<br/> \n"
						+ "\t\t\t\t But batch download becomes far easier if you have a download manager like DownThemAll.<br/>\n"
						+ "\t\t\t </span>\n"
						+ "\t\t </div>\n"
						+ "\t\t <p/>\n"
						+ "\t\t <div id=\"links\">\n"
						+ "\t\t\t <table border=\"1\" cellpadding=\"5px\">\n"
						+ "\t\t\t\t <tr><th>S.No</th><th>Title</th><th>Quality</th></tr>\n";

				for (int i = 0; i < matrix.size(); i++) {
					String[] url = null;
					String name = null;
					String format = null;

					if (paneOptions.getSelectedIndex() == 1
							&& chckbxBestAudio.isSelected() == chckbxBestVideo
									.isSelected()) {
						url = new String[2];
						url[0] = matrix.get(i).get(1);
						url[1] = matrix.get(i).get(2);
						name = matrix.get(i).get(3);
						format = matrix.get(i).get(4);
					} else {
						url = new String[1];
						url[0] = matrix.get(i).get(1);
						name = matrix.get(i).get(2);
						format = matrix.get(i).get(3);
					}

					htmlText += "\t\t\t\t <tr><td>"
							+ (i + 1)
							+ "</td><td><a href=\""
							+ url[0]
							+ "&; codecs&title="
							+ getNumbering(i)
							+ name.replace("_", " ").replaceFirst(
									"\\.(mp4|m4a|flv|3gp|webm)$", "") + "\">"
							+ name.replace("_", " ") + "</a>";

					for (int j = 1; j < url.length; j++) {
						htmlText += "\t\t\t\t\t<p><a href=\""
								+ url[j].toString()
								+ "\">"
								+ name.replace("_", " ").replaceFirst(
										"\\.(mp4|m4a|flv|3gp|webm)$", "")
								+ ".m4a</a>";
					}

					htmlText += "</td><td><span class='" + getColor(format)
							+ "'>" + format + "</td></tr>\n";

				}

				htmlText += "\t\t\t </table>\n"
						+ "\t\t </div>\n"
						+ "\t\t <br/><div class='center gray'>"
						+ Date()
						+ "<br/>Link Expiry Time: ~6 Hours</div><br/><hr size=\"1\" width=\"80%\"/><br/><br/>\n"
						+ "\t </body>\n" + " </html>";

				writeToFile("ytdl.html", htmlText);
			}

			if (errorMatrix.size() > 0) {
				htmlText = " <!DOCTYPE html>\n"
						+ " <html>\n"
						+ "\t <head>\n"
						+ "\t\t <title>YouTube-DL Failed Links</title>\n"
						+ "\t\t <meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\">\n"
						+ HTML_PAGE_STYLE
						+ "\t </head>\n"
						+ "\t <body>\n"
						+ "\t\t <div class=\"fullwidth center gray\">\n"
						+ "\t\t\t <b>YouTube-DL Invocation Timestamp:</b>"
						+ Date()
						+ "<br/>\n"
						/* "\t\t\t <b>Source page:</b> <a href=\"PAGE LINK\">PAGE TITLE</a>\n" */
						+ "\t\t </div>\n"
						+ "\t\t <br/><h2 class=\"red center\">Failed to generate download links for the following videos.</h2>\n"
						+ "\t\t <div id=\"failed_links\" class=\"pad20\">\n"
						+ "\t\t\t <table border=\"1\" cellpadding=\"5px\" style=\"border-collapse:collapse;margin-left:auto;margin-right:auto\">\n"
						+ "\t\t\t\t <tr><th>S.No</th><th>Title</th><th>Reason for failure</th></tr>\n";

				for (int j = 0; j < errorMatrix.size(); j++) {
					htmlText += "\t\t\t<tr><td>" + (j + 1) + "</td><td>"
							+ errorMatrix.get(j)[0] + "</td><td>"
							+ errorMatrix.get(j)[1] + "</td></tr>\n";
				}

				htmlText += "\t\t</table>\n" + "\t</div>\n" + "</body>\n"
						+ "</html>";

				writeToFile("ytdl-Error.html", htmlText);

			}
			CardLayout cl = (CardLayout) (contentPane.getLayout());
			cl.first(contentPane);
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
