package edu.nd.dronology.monitoring.simplechecker.monitor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;

public class MonitorDialog {

	private static JFrame frmMonitor;
	private static JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MonitorDialog window = new MonitorDialog();
					window.frmMonitor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MonitorDialog() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMonitor = new JFrame();
		frmMonitor.setTitle("Simple-Monitor");
		frmMonitor.setBounds(100, 100, 705, 500);
		frmMonitor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(new TitledBorder(new EtchedBorder(), ""));
		frmMonitor.getContentPane().add(middlePanel);
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));
		// create the middle panel components

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		textArea.setEditable(false); // set textArea non-editable
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// Add Textarea in to middle panel
		middlePanel.add(scroll);

		textArea.setFont(new Font("Courier New", Font.BOLD, 20));
		textArea.setEditable(false);
		// textArea.getDocument().addDocumentListener(this);
	}

	public void addLine(String line) {
		SwingUtilities.invokeLater(() -> {
			textArea.append(line);
			textArea.append("\n");
			textArea.setSelectionEnd(textArea.getText().length());
		});

	}

}
