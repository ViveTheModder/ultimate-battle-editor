package cmd;
//Ultimate Battle Editor v1.1 - GUI
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Main 
{
	private static final Image ICON = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("img/icon.png"));
	static final int[] MODE_MAX_MISSIONS = {7,100,3,99,5};
	private static final int BG_RGB = 0x323B52;
	private static final String HTML_TEXT = "<html><div style='font-family: Tahoma, Geneva, sans-serif; font-size: 14px; color: white;'>";
	private static final String HTML_TEXT_BLACK = HTML_TEXT.replace("white", "black");
	private static final String HTML_TEXT_GOLD = HTML_TEXT.replace("white", "#e4ca02");
	static final String RES_PATH = "./res/";
	private static final String WINDOW_TITLE = "Ultimate Battle Editor";
	private static final String[] ACTION_SELECT = {"Read DAT Files","Write DAT Files","Interpret CSV Files"};
	static final String[] MODE_SELECT = {"sim-dragon","mission-100","survival","ranking-battle","course-battle"};
    static boolean isSingleMission=true, isForWii=false;
    private static int actionSelIndex=0, missionID=1, modeSelIndex=0;
	static int fileCnt=0, fileTotal=1;
	private static JButton confBtn = new JButton(HTML_TEXT+"Confirm");
	static JDialog loading;
	private static JFrame frame = new JFrame(WINDOW_TITLE);
	private static JLabel label = new JLabel(HTML_TEXT+"Choose a gamemode:");
	private static JPanel panel = new JPanel();
	static JProgressBar bar;
	private static JRadioButton[] radioBtns;

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
		loading.setVisible(false);
		File errorLog = new File("errors.log");
		try {
			FileWriter logWriter = new FileWriter(errorLog,true);
			logWriter.append(new SimpleDateFormat("dd-MM-yy-hh-mm-ss").format(new Date())+":\n"+e.getMessage()+"\n");
			logWriter.close();
			Desktop.getDesktop().open(errorLog);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(1);
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
			
			radioBtns[i].setBackground(new Color(BG_RGB));
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
		panel.setBackground(new Color(BG_RGB));
		frame.add(panel);
		frame.getContentPane().setBackground(new Color(BG_RGB));
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
			radioBtns[i].setBackground(new Color(BG_RGB));
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
		wiiCheck.setBackground(new Color(BG_RGB));
		wiiCheck.setHorizontalAlignment(SwingConstants.CENTER);
		
		confBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false); frame.dispose();
				if (wiiCheck.isSelected()) isForWii=true;
				setProgress();
			}
		});
		panel.add(wiiCheck);
		panel.add(new JLabel(" "));
		panel.add(confBtn);
	}
	public static void setProgress()
	{
		//change progress bar settings (must be done before declaring)
	    UIManager.put("ProgressBar.background", Color.WHITE);
	    UIManager.put("ProgressBar.foreground", Color.GREEN);
	    UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
	    UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
		//add -ing at end of action verb (felt better than making another array)
	    String[] labelTextArr = ACTION_SELECT[actionSelIndex].split(" ");
	    if (labelTextArr[0].endsWith("e")) //get rid of e at end of action verb if it exists
	    	labelTextArr[0] = labelTextArr[0].substring(0, labelTextArr[0].length()-1);
	    String labelText = labelTextArr[0]+"ing "+labelTextArr[1]+" "+labelTextArr[2]+"...";
		loading = new JDialog();
		JPanel panel = new JPanel();
		JLabel label = new JLabel(HTML_TEXT_GOLD+labelText);
		bar = new JProgressBar();
		bar.setValue(0);
		bar.setStringPainted(true);
		bar.setBorderPainted(true);
		bar.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
		bar.setFont(new Font("Tahoma", Font.BOLD, 14));
		bar.setMinimumSize(new Dimension(128,32));
		bar.setMaximumSize(new Dimension(128,32));
		bar.setPreferredSize(new Dimension(128,32));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.setBackground(new Color(BG_RGB));
		panel.setLayout(new GridLayout(0,1));
		panel.add(label);
		panel.add(bar);
		loading.add(panel);
		loading.getContentPane().setBackground(new Color(BG_RGB));
		loading.setTitle(WINDOW_TITLE);
		loading.setLayout(new GridBagLayout());
		loading.setSize(256,256);
		loading.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		loading.setIconImage(ICON);
		loading.setLocationRelativeTo(null);
		loading.setVisible(true);
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception {
				performAction();
				return null;
			}
		};
		worker.execute();
	}
	public static void performAction()
	{
		String[] actions = {"read","overwritten","interpreted"};
		try {
			if (actionSelIndex==2) DataInterpreter.interpretData(modeSelIndex);
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
						loading.setVisible(false); //no progress bar is needed if only one file is being worked on
					}
					else if (input.equals("")) isSingleMission=false;
				}
				switch (modeSelIndex)
				{
					case 0:
					Main.fileTotal = 2*Main.MODE_MAX_MISSIONS[0];
					Main.bar.setMaximum(Main.fileTotal);
					if (actionSelIndex==1)
					{
						SimDragon.writeBattleConfigFile(missionID);
						SimDragon.writeOpponentConfigFile(missionID);
					}
					else 
					{
						SimDragon.readBattleConfigFile();
						SimDragon.readOpponentConfigFile();
					} break;
					case 1:
					Main.fileTotal = 2*Main.MODE_MAX_MISSIONS[1];
					Main.bar.setMaximum(Main.fileTotal);
					if (actionSelIndex==1) 
					{
						Mission100.writeBattleConfigFile(missionID);
						Mission100.writeOpponentConfigFile(missionID);
					}
					else
					{
						Mission100.readBattleConfigFile();
						Mission100.readOpponentConfigFile();
					} break;
					case 2:
					Main.fileTotal = 2*Main.MODE_MAX_MISSIONS[2];
					Main.bar.setMaximum(Main.fileTotal);	
					if (actionSelIndex==1)
					{
						Survival.writeBattleConfigFile(missionID);
						Survival.writeOpponentConfigFile(missionID);
					}
					else
					{
						Survival.readBattleConfigFile();
						Survival.readOpponentConfigFile();
					} break;
					case 3:
					Main.fileTotal = 2*Main.MODE_MAX_MISSIONS[3]+74;
					Main.bar.setMaximum(Main.fileTotal);
					if (actionSelIndex==1)
					{	
						RankingBattle.writeBattleConfigFile(missionID);
						RankingBattle.writeOpponentConfigFile(missionID);
					}
					else
					{
						RankingBattle.readBattleConfigFile();
						RankingBattle.readOpponentConfigFile();
					} break;
					case 4:
					Main.fileTotal = 2*Main.MODE_MAX_MISSIONS[4];
					Main.bar.setMaximum(Main.fileTotal);
					if (actionSelIndex==1) 
					{
						CourseBattle.writeBattleConfigFile(missionID);
						CourseBattle.writeOpponentConfigFile(missionID);
					}
					else 
					{
						CourseBattle.readBattleConfigFile();
						CourseBattle.readOpponentConfigFile();
					} break;
				}
			}
		} catch (IOException e) {
			setErrorLog(e);
		}
		loading.setVisible(false); loading.dispose();
		//generate message contents after action is performed
		String fileType="DAT";
		if (actionSelIndex==2) fileType="CSV";
		String msg = fileType+" files for "+MODE_SELECT[modeSelIndex].replace('-', ' ').toUpperCase()+" have been "+actions[actionSelIndex]+" successfully!";
		JOptionPane.showMessageDialog(null, HTML_TEXT_BLACK+msg, WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
	public static void main(String[] args) 
	{
		setModeSelect();
	}
}