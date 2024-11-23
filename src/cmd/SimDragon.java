package cmd;
//Ultimate Battle Editor v1.1 - Sim Dragon
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class SimDragon 
{
	static final String BATTLE_CFG_HEADER = 
	"referee-id,map-destruct,time-id,map-id,bgm-id,com-transf-switch,battle-num\n";
	static RandomAccessFile battleConfigData, oppConfigData;
	public static void readBattleConfigFile() throws IOException
	{
		battleConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[0]+"/24_sim_param.dat","r");
		long fileSize = battleConfigData.length();
		if (fileSize!=256) //prevent EOFException
		{
			battleConfigData.close(); return; 
		}
		String input=BATTLE_CFG_HEADER;
		for (int i=0; i<196; i+=4)
		{
			if (i%28==0) 
			{
				if (i!=0)
				{	
					input+="\n"; //end of row
					input = input.substring(0, input.length()-2)+"\n";
				}
				Main.fileCnt++; Main.bar.setValue(Main.fileCnt);
			}
			int val = LittleEndian.getInt(battleConfigData.readInt());
			input+=val+",";
		}
		input = input.substring(0, input.length()-1); //remove last comma
		FileWriter writer = new FileWriter(new File(Main.RES_PATH+Main.MODE_SELECT[0]+"/sim-battle-cfg.csv"));
		writer.write(input); 
		writer.close(); battleConfigData.close();
	}
	public static void readOpponentConfigFile() throws IOException
	{
		oppConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[0]+"/25_sim_opponent_param.dat","r");
		long fileSize = oppConfigData.length();
		if (fileSize!=320) //prevent EOFException
		{
			oppConfigData.close(); return;
		}
		String input=Mission100.OPP_CFG_HEADER;
		for (int i=0; i<308; i+=4)
		{
			if (i%44==0)
			{
				if (i!=0)
				{	
					input+="\n"; //end of row
					input = input.substring(0, input.length()-2)+"\n";
				}
				Main.fileCnt++; Main.bar.setValue(Main.fileCnt);
			}
			int val = LittleEndian.getInt(oppConfigData.readInt());
			input+=val+",";
		}
		input = input.substring(0, input.length()-1); //remove last comma
		FileWriter writer = new FileWriter(new File(Main.RES_PATH+Main.MODE_SELECT[0]+"/sim-opp-cfg.csv"));
		writer.write(input); writer.close();
		oppConfigData.close();
	}
	public static void writeBattleConfigFile(int missionID) throws IOException
	{
		String root = Main.RES_PATH+Main.MODE_SELECT[0]+"/";
		int lineCnt=0, missionAddr=(missionID-1)*28, pos;
		File selectedCSV = new File(root+"sim-battle-cfg.csv");
		Scanner sc = new Scanner(selectedCSV);
		
		battleConfigData = new RandomAccessFile(root+"24_sim_param.dat","rw");
		long fileSize = battleConfigData.length();
		if (fileSize!=256) //prevent EOFException
		{
			battleConfigData.close(); sc.close(); return; 
		}
		sc.nextLine(); //skip header
		battleConfigData.seek(missionAddr); pos=missionAddr;
		
		while (sc.hasNextLine())
		{
			String input = sc.nextLine();
			String[] inputArr = input.split(",");
			lineCnt++;
			if (!Main.isSingleMission) missionID=lineCnt; //this will assure the 2nd if condition is ALWAYS TRUE
			if (lineCnt==missionID)
			{
				for (String i: inputArr)
				{
					int newInt = Integer.parseInt(i);
					if (newInt<0) newInt=0; //prevent underflow
					int currInt = LittleEndian.getInt(battleConfigData.readInt()); //current position automatically increases by 4
					battleConfigData.seek(pos); //go back 4 bytes
					if (currInt!=newInt) battleConfigData.writeInt(LittleEndian.getInt(newInt));
					pos+=4;
				}
				Main.fileCnt++; Main.bar.setValue(Main.fileCnt);
			}
		}
		sc.close();
	}
	public static void writeOpponentConfigFile(int missionID) throws IOException
	{
		String root = Main.RES_PATH+Main.MODE_SELECT[0]+"/";
		int lineCnt=0, missionAddr=(missionID-1)*44, pos;		
		File selectedCSV = new File(root+"sim-opp-cfg.csv");
		Scanner sc = new Scanner(selectedCSV);
		
		battleConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[0]+"/25_sim_opponent_param.dat","rw");
		long fileSize = battleConfigData.length();
		if (fileSize!=320) //prevent EOFException
		{
			battleConfigData.close(); sc.close(); return; 
		}
		sc.nextLine(); //skip header
		battleConfigData.seek(missionAddr); pos=missionAddr;
		
		while (sc.hasNextLine())
		{
			String input = sc.nextLine();
			String[] inputArr = input.split(",");
			lineCnt++;
			if (!Main.isSingleMission) missionID=lineCnt; //this will assure the 2nd if condition is ALWAYS TRUE
			if (lineCnt==missionID)
			{
				for (String i: inputArr)
				{
					int newInt = Integer.parseInt(i);
					if (newInt<0) newInt=0; //prevent underflow
					int currInt = LittleEndian.getInt(battleConfigData.readInt()); //current position automatically increases by 4
					battleConfigData.seek(pos); //go back 4 bytes
					if (currInt!=newInt) battleConfigData.writeInt(LittleEndian.getInt(newInt));
					pos+=4;
				}
				Main.fileCnt++; Main.bar.setValue(Main.fileCnt);
			}
		}
		sc.close();
	}
}