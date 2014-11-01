package com.xandrev.jdorg.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.xandrev.jdorg.configuration.Configuration;
import com.xandrev.jdorg.configuration.Constants;
import com.xandrev.jdorg.i18n.TextLocalizerManager;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JTextField textField;
	private JTextField textField1;
	private JSpinner spinner;
	private final JList list;
	private final JList list1;
	private final MainFrame thisFrame;
        private Configuration cfg;

	/**
	 * Create the frame.
	 */
	public MainFrame() {
            
                cfg = Configuration.getInstance();
		thisFrame = this;
		
		JPanel contentPane;
                TextLocalizerManager i18n = TextLocalizerManager.getInstance(null);
		
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu(i18n.getLocalizerText("gui.preferences.file"));
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem(i18n.getLocalizerText("gui.preferences.exit"));
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu(i18n.getLocalizerText("gui.preferences.help"));
		menuBar.add(mnHelp);
                
                JMenu mnAudit = new JMenu(i18n.getLocalizerText("gui.preferences.audit"));
		menuBar.add(mnAudit);
		
		JMenuItem mntmAbout = new JMenuItem(i18n.getLocalizerText("gui.preferences.about"));
		mnHelp.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 442, 236);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("General", null, panel, null);
		panel.setLayout(null);
		
		JLabel label = new JLabel("Initial Folder");
		label.setBounds(22, 8, 59, 14);
		panel.add(label);
		
		textField = new JTextField();
		textField.setBounds(86, 5, 272, 20);
		textField.setColumns(10);
		panel.add(textField);
		
		JLabel label1 = new JLabel("Final folder");
		label1.setBounds(22, 39, 53, 14);
		panel.add(label1);
		
		textField1 = new JTextField();
		textField1.setBounds(86, 36, 272, 20);
		textField1.setColumns(10);
		panel.add(textField1);
		
		JLabel lblLanguage = new JLabel("Language");
		lblLanguage.setBounds(22, 73, 52, 14);
		panel.add(lblLanguage);
		
		spinner = new JSpinner();
		spinner.setBounds(86, 96, 113, 18);
		panel.add(spinner);
		
		JLabel lblFequency = new JLabel("Fequency");
		lblFequency.setBounds(22, 98, 53, 14);
		panel.add(lblFequency);
		
		JButton button = new JButton("Cancel");
		button.setBounds(360, 177, 67, 23);
		panel.add(button);
		
		JButton button1 = new JButton("Save");
		button1.setBounds(299, 177, 59, 23);
		panel.add(button1);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(86, 67, 272, 22);
		panel.add(comboBox);
		
		JPanel panel1 = new JPanel();
		tabbedPane.addTab("Extensions", null, panel1, null);
		panel1.setLayout(null);
		
		JButton button2 = new JButton("Add");
		button2.setBounds(220, 5, 75, 23);
		panel1.add(button2);
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog(null, "Please, type the extension you want to add" ,"Insert the new subtitle extension");
				boolean added = cfg.addSubtitleExtension(result);
				if(added){
					refreshPanel();
				}
			}

			private void refreshPanel() {
				// TODO Auto-generated method stub
				
			}
		});					
		
		list = new JList();
		list.setBounds(220, 35, 160, 137);
		panel1.add(list);
		
		JButton button3 = new JButton("Remove");
		button3.setBounds(86, 5, 84, 23);
		panel1.add(button3);
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int indexSelected = list.getSelectedIndex();
				if(indexSelected > -1){
					String extension = (String) list.getModel().getElementAt(indexSelected);
					cfg.removeSubtitleExtension(extension);
					thisFrame.refreshPanel();
				}
			}
		});
		
	    list1 = new JList();
		list1.setBounds(0, 35, 182, 137);
		panel1.add(list1);
		
		JButton button4 = new JButton("Add");
		button4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog(null, "Please, type the extension you want to add" ,"Insert the new video extension");
				boolean added = cfg.addVideoExtension(result);
				if(added){
					thisFrame.refreshPanel();
				}
			}

			
		});
		button4.setBounds(1, 5, 75, 23);
		panel1.add(button4);
		
		JButton button5 = new JButton("Remove");
		button5.setBounds(296, 5, 84, 23);
		button5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int indexSelected = list1.getSelectedIndex();
				if(indexSelected > -1){
					String extension = (String) list1.getModel().getElementAt(indexSelected);
					cfg.removeVideoExtension(extension);
					thisFrame.refreshPanel();
				}
			}
		});
		panel1.add(button5);
		
	}
	
	protected void refreshPanel() {
		loadProperties();
	}

	
	public void saveProperties(){
		Properties prop = cfg.getProperties();
		
		
		String initialDirectory = textField.getText(); 
		String finalDirectory = textField1.getText();
		int sleepTime = Integer.parseInt(spinner.getValue().toString());
		
		prop.setProperty(Constants.INITIAL_FOLDER,initialDirectory);
		prop.setProperty(Constants.FINAL_FOLDER,finalDirectory);
		prop.setProperty(Constants.SLEEP_TIME,String.valueOf(sleepTime));
		
		cfg.saveProperties(Constants.CONFIG_FILE_PATH);
	}
	
	public  void loadProperties(){
		Properties prop = cfg.getProperties();
		String initialDirectory = prop.getProperty(Constants.INITIAL_FOLDER);
		String finalDirectory = prop.getProperty(Constants.FINAL_FOLDER);
		String extensionsArray = prop.getProperty(Constants.VIDEO_EXTENSIONS);
		String subtitleArray = prop.getProperty(Constants.SUBTITLE_EXTENSIONS);
		String[] extensions = extensionsArray.split(",");
		String[] subExtensions = subtitleArray.split(",");
		
		int sleepTime = Integer.parseInt(prop.getProperty(Constants.SLEEP_TIME));
		
		DefaultListModel model = new DefaultListModel();
		for(int i=0;i<extensions.length;i++){
			model.add(i, extensions[i]);
		}
		list1.setModel(model);
		
		DefaultListModel model1 = new DefaultListModel();
		for(int i=0;i<subExtensions.length;i++){
			model1.add(i, subExtensions[i]);
		}
		list.setModel(model1);
		
		textField.setText(initialDirectory);
		textField1.setText(finalDirectory);
		spinner.setValue(sleepTime);
		
	}
}
