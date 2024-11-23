package cmd;
//Ultimate Battle Editor v1.1 - Course Battle
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;

public class CourseBattle 
{	
	static final String OPP_CFG_HEADER =
	"chara-id,costume-num,com-diff-lvl,item-id-1,item-id-2,item-id-3,item-id-4,item-id-5,item-id-6,item-id-7,item-id-8\n";
	static RandomAccessFile battleConfigData, oppConfigData;
	public static void readBattleConfigFile() throws IOException
	{
		battleConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[4]+"/07_ub_circuit_prm.dat","r");
		long fileSize = battleConfigData.length();
		if (fileSize!=320) //prevent EOFException
		{
			battleConfigData.close(); return; 
		}
		String input=Survival.BATTLE_CFG_HEADER;
		for (int i=1; i<=8; i++) 
		{
			input+="opp-num-"+i+",";
			if (i==8) input = input.substring(0, input.length()-1); //remove last comma
		}
		input+="\n";
		for (int i=0; i<280; i+=4)
		{
			if (i%56==0) 
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
		FileWriter writer = new FileWriter(new File(Main.RES_PATH+Main.MODE_SELECT[4]+"/circ-battle-cfg.csv"));
		writer.write(input); 
		writer.close(); battleConfigData.close();
	}
	public static void readOpponentConfigFile() throws IOException
	{
		oppConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[4]+"/08_ub_circuit_enemy_prm.dat","r");
		long fileSize = oppConfigData.length();
		if (fileSize!=1792) //prevent EOFException
		{
			oppConfigData.close(); return;
		}
		String input=OPP_CFG_HEADER;
		for (int i=0; i<1760; i+=4)
		{
			if (i!=0)
			{
				if (i%44==0) 
				{	
					input+="\n"; //end of row
					input = input.substring(0, input.length()-2)+"\n";
				}
			}
			if (i%352==0)
			{
				input = input.substring(0, input.length()-1); //remove last comma
				FileWriter writer = new FileWriter(new File(Main.RES_PATH+Main.MODE_SELECT[4]+"/circ-opp-cfg-"+((i/352)+1)+".csv"));
				writer.write(input); writer.close();
				input=OPP_CFG_HEADER; //reset row
				Main.fileCnt++; Main.bar.setValue(Main.fileCnt);
			}
			int val = LittleEndian.getInt(oppConfigData.readInt());
			input+=val+",";
		}
		oppConfigData.close();
	}
	public static void writeBattleConfigFile(int missionID) throws IOException
	{
		String root = Main.RES_PATH+Main.MODE_SELECT[4]+"/";
		int lineCnt=0, missionAddr=(missionID-1)*56, pos;
		File selectedCSV = new File(root+"circ-battle-cfg.csv");
		Scanner sc = new Scanner(selectedCSV);
		
		battleConfigData = new RandomAccessFile(root+"07_ub_circuit_prm.dat","rw");
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
			if (!Main.isSingleMission) 
			{
				missionID=lineCnt; //this will assure the 2nd if condition is ALWAYS TRUE
			}
			else Main.fileTotal=1;
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
		String root = Main.RES_PATH+Main.MODE_SELECT[4]+"/";
		File folder = new File(root), selectedCSV=null;
		Scanner sc=null;
		String[] csvFilePaths = folder.list((dir, name)->(name.startsWith("circ-opp-cfg") && name.endsWith(".csv")));
		Arrays.sort(csvFilePaths);
		
		int missionAddr=0, pos;
		if (Main.isSingleMission)
		{
			Main.fileTotal=1;
			int key = Arrays.binarySearch(csvFilePaths, "circ-opp-cfg-"+missionID+".csv");
			missionAddr=(missionID-1)*44;
			selectedCSV = new File(root+csvFilePaths[key]);
			sc = new Scanner(selectedCSV);
		}
		
		oppConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[4]+"/08_ub_circuit_enemy_prm.dat","rw");
		long fileSize = oppConfigData.length();
		if (fileSize!=1792) //prevent EOFException
		{
			oppConfigData.close(); sc.close(); return; 
		}
		
		int max = Main.MODE_MAX_MISSIONS[4];
		if (Main.isSingleMission) max=1;
		for (int i=0; i<max; i++)
		{
			if (!Main.isSingleMission) missionAddr=i*44;
			battleConfigData.seek(missionAddr); pos=missionAddr;
			if (!Main.isSingleMission) 
			{
				selectedCSV = new File(root+csvFilePaths[i]);
				sc = new Scanner(selectedCSV);
			}
			sc.nextLine(); //skip header

			Main.fileCnt++; Main.bar.setValue(Main.fileCnt);

			while (sc.hasNextLine())
			{
				String input = sc.nextLine();
				String[] inputArr = input.split(",");
				for (String inputStr: inputArr)
				{
					int newInt = Integer.parseInt(inputStr);
					if (newInt<0) newInt=0; //prevent underflow
					int currInt = LittleEndian.getInt(oppConfigData.readInt()); //current position automatically increases by 4
					oppConfigData.seek(pos); //go back 4 bytes
					if (currInt!=newInt) oppConfigData.writeInt(LittleEndian.getInt(newInt));
					pos+=4;
				}
			}
		}
	}
}