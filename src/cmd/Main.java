package cmd;
//Ultimate Battle Editor v1.0 by ViveTheModder
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

public class Main 
{
	static final Image ICON = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("img/icon.png"));
	static final int[] MODE_MAX_MISSIONS = {7,100,3,99,5};
	static final String HTML_TEXT = "<html><div style='font-family: Tahoma, Geneva, sans-serif; font-size: 14px; color: white;'>";
	static final String HTML_TEXT_BLACK = HTML_TEXT.replace("white", "black");
	static final String HTML_TEXT_GOLD = HTML_TEXT.replace("white", "#e4ca02");
	static final String RES_PATH = "./res/";
	static final String WINDOW_TITLE = "Ultimate Battle Editor";
	static final String[] ACTION_SELECT = {"Read DAT Files","Write DAT Files","Interpret CSV Files"};
	static final String[] MODE_SELECT = {"sim-dragon","mission-100","survival","ranking-battle","course-battle"};
    static boolean isSingleMission=true, isForWii=false;
	static int actionSelIndex=0, missionID=1, modeSelIndex=0;
	static JButton confBtn = new JButton(HTML_TEXT+"Confirm");
	static JFrame frame = new JFrame(WINDOW_TITLE);
	static JLabel label = new JLabel(HTML_TEXT+"Choose a gamemode:");
	static JPanel panel = new JPanel();
	static JRadioButton[] radioBtns;

	public static int getNumberOfDigits(int num)
	{
		int cnt=0;
		while (num!=0)
		{
			num/=10;
			cnt++;
		}
		return cnt;
	}
	public static void setErrorLog(Exception e)
	{
		frame.setVisible(false);
		File errorLog = new File("errors.log");
		try {
			FileWriter logWriter = new FileWriter(errorLog,true);
			logWriter.append(new SimpleDateFormat("dd-MM-yy-hh-mm-ss").format(new Date())+":\n"+e.getMessage()+"\n");
			logWriter.close();
			Desktop.getDesktop().open(errorLog);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public static void setModeSelect()
	{
		radioBtns = new JRadioButton[MODE_SELECT.length];
		ButtonGroup btnGrp = new ButtonGroup();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		confBtn.setHorizontalAlignment(SwingConstants.CENTER);
		confBtn.setBackground(new Color(53, 113, 157));
		confBtn.setBorder(BorderFactory.createLineBorder(new Color(0xcbcbcb),3));
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.add(label); panel.add(new JLabel(" "));
		for (int i=0; i<radioBtns.length; i++)
		{
			final int constant=i;
			radioBtns[i] = new JRadioButton(HTML_TEXT_GOLD+MODE_SELECT[i].replace('-', ' ').toUpperCase());
			if (i==4) 
				radioBtns[i].setToolTipText(HTML_TEXT_BLACK+"Disc Fusion's take on the gamemode from Budokai Tenkaichi 2.");
			else if (i==3)
				radioBtns[i].setToolTipText(HTML_TEXT_BLACK+"Disc Fusion's take on the gamemode from Budokai Tenkaichi.");
			else if (i==0) radioBtns[0].setSelected(true);
			
			radioBtns[i].setBackground(new Color(0x323B52));
			radioBtns[i].setHorizontalAlignment(SwingConstants.CENTER);
			radioBtns[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					modeSelIndex=constant;
				}
			});
			btnGrp.add(radioBtns[i]);
			panel.add(radioBtns[i]);
		}
		confBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i=0; i<radioBtns.length; i++) panel.remove(radioBtns[i]);
				setActionSelect();
			}
		});
		panel.add(new JLabel(" ")); panel.add(confBtn);
		panel.setBackground(new Color(0x323B52));
		frame.add(panel);
		frame.getContentPane().setBackground(new Color(0x323B52));
		frame.setIconImage(ICON);
		frame.setLayout(new GridBagLayout());	
		frame.setSize(512, 512);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public static void setActionSelect()
	{
		label.setText(HTML_TEXT+"Choose an action:");
		radioBtns = new JRadioButton[ACTION_SELECT.length];
		ButtonGroup btnGrp = new ButtonGroup();
		panel.add(label); panel.add(new JLabel(" "));
		for (int i=0; i<radioBtns.length; i++)
		{
			final int constant=i;
			radioBtns[i] = new JRadioButton(HTML_TEXT_GOLD+ACTION_SELECT[i]);
			if (i==2) radioBtns[i].setToolTipText(HTML_TEXT_BLACK
			+ "This action will return more readable CSV files (by replacing e.g. character/item IDs with<br>"
			+ "their names) if and only if there are any CSV files present in the res/"+MODE_SELECT[modeSelIndex]+" folder.");
			else if (i==1) radioBtns[i].setToolTipText(HTML_TEXT_BLACK
			+ "This action will check for changes made to the CSV files in the res/"+MODE_SELECT[modeSelIndex]+"<br>"
			+ "folder, and apply said change to the provided DAT files for "+MODE_SELECT[modeSelIndex].replace('-', ' ').toUpperCase()+'.');
			else if (i==0)
			{
				radioBtns[i].setSelected(true);
				radioBtns[i].setToolTipText(HTML_TEXT_BLACK
				+ "This action will read the DAT files for "+MODE_SELECT[modeSelIndex].replace('-', ' ').toUpperCase()
				+ "<br>and save their information in a series of CSV files.");
			}
			radioBtns[i].setBackground(new Color(0x323B52));
			radioBtns[i].setHorizontalAlignment(SwingConstants.CENTER);
			radioBtns[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					actionSelIndex=constant;
				}
			});
			btnGrp.add(radioBtns[i]);
			panel.add(radioBtns[i]);
		}
		
		JCheckBox wiiCheck = new JCheckBox(HTML_TEXT+"Wii Mode");
		wiiCheck.setToolTipText(HTML_TEXT_BLACK+"This option is meant for files whose integers are in Big Endian, "
		+ "not Little Endian<br>(which is the default byte order for the PS2 version of Budokai Tenkaichi 3).");
		wiiCheck.setBackground(new Color(0x323B52));
		wiiCheck.setHorizontalAlignment(SwingConstants.CENTER);
		
		confBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				if (wiiCheck.isSelected()) isForWii=true;
				try {
					performAction();
				} catch (IOException e1) {
					setErrorLog(e1);
					System.exit(1);
				}
			}
		});
		panel.add(wiiCheck);
		panel.add(new JLabel(" "));
		panel.add(confBtn);
	}
	public static void performAction() throws IOException
	{
		if (actionSelIndex==2) 
		{
			DataInterpreter.interpretData(modeSelIndex);
			JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
			"CSV files have been interpreted successfully!", WINDOW_TITLE, 1);
			System.exit(0);
		}
		else
		{
			if (actionSelIndex==1)
			{
				String input = JOptionPane.showInputDialog(null, HTML_TEXT_BLACK+
				"Enter a mission ID (or leave empty to overwrite them all):",WINDOW_TITLE, -1);
				if (input==null) System.exit(1);
				else if (input.matches("[0-9]+")) 
				{
					missionID=Integer.parseInt(input);
					if (missionID>MODE_MAX_MISSIONS[modeSelIndex]) missionID=MODE_MAX_MISSIONS[modeSelIndex]; //handle overflow
					if (missionID<=0) missionID=1; //handle underflow
				}
				else if (input.equals("")) isSingleMission=false;
			}
			switch (modeSelIndex)
			{
				case 0: 
				if (actionSelIndex==1)
				{
					SimDragon.writeBattleConfigFile(missionID);
					SimDragon.writeOpponentConfigFile(missionID);
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Sim Dragon's files have been overwritten successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				}
				else 
				{
					SimDragon.readBattleConfigFile();
					SimDragon.readOpponentConfigFile();
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Sim Dragon's files have been read successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				} break;
				case 1: 
				if (actionSelIndex==1) 
				{
					Mission100.writeBattleConfigFile(missionID);
					Mission100.writeOpponentConfigFile(missionID);
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Mission 100's files have been overwritten successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				}
				else
				{
					Mission100.readBattleConfigFile();
					Mission100.readOpponentConfigFile();
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Mission 100's files have been read successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				} break;
				case 2:
				if (actionSelIndex==1)
				{
					Survival.writeBattleConfigFile(missionID);
					Survival.writeOpponentConfigFile(missionID);
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Survival's files have been overwritten successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				}
				else
				{
					Survival.readBattleConfigFile();
					Survival.readOpponentConfigFile();
					frame.setVisible(false);
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Survival's files have been read successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				} break;
				case 3: 
				if (actionSelIndex==1)
				{	
					RankingBattle.writeBattleConfigFile(missionID);
					RankingBattle.writeOpponentConfigFile(missionID);
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Ranking Battle's files have been overwritten successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				}
				else
				{
					RankingBattle.readBattleConfigFile();
					RankingBattle.readOpponentConfigFile();
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Ranking Battle's files have been read successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				} break;
				case 4:
				if (actionSelIndex==1) 
				{
					CourseBattle.writeBattleConfigFile(missionID);
					CourseBattle.writeOpponentConfigFile(missionID);
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Course Battle's files have been overwritten successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				}
				else 
				{
					CourseBattle.readBattleConfigFile();
					CourseBattle.readOpponentConfigFile();
					JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+
					"Course Battle's files have been read successfully!", WINDOW_TITLE, 1);
					System.exit(0);
				} break;
			}
		}
	}
	public static void main(String[] args) 
	{
		setModeSelect();
	}
}