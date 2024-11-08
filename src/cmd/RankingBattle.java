package cmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class RankingBattle 
{
	static final String BATTLE_CFG_HEADER = 
	"referee-id,map-destruct,time-id,map-id,bgm-id,com-transf-switch,battle-num\n";
	static RandomAccessFile battleConfigData, oppConfigData;
	public static void readBattleConfigFile() throws FileNotFoundException, IOException
	{
		battleConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[3]+"/07_ub_ranking_prm.dat","r");
		long fileSize = battleConfigData.length();
		if (fileSize!=2816) //prevent EOFException
		{
			battleConfigData.close(); return; 
		}	
		String input=BATTLE_CFG_HEADER;
		int val; FileWriter writer;
		for (int i=0; i<2772; i+=4)
		{
			if (i%28==0) 
			{
				System.out.println("Reading mission "+((i/28)+1)+"'s battle parameters...");
				if (i!=0)
				{	
					input+="\n"; //end of row
					input = input.substring(0, input.length()-2)+"\n";
				}
			}
			val = LittleEndian.getInt(battleConfigData.readInt());
			input+=val+",";
		}
		input = input.substring(0, input.length()-1); //remove last comma
		writer = new FileWriter(new File(Main.RES_PATH+Main.MODE_SELECT[3]+"/rank-battle-cfg.csv"));
		writer.write(input); writer.close();
		
		battleConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[3]+"/08_ub_ranking_challenge_prm.dat","r");
		fileSize = battleConfigData.length();
		if (fileSize!=1088) //prevent EOFException
		{
			battleConfigData.close(); return; 
		}
		input=BATTLE_CFG_HEADER;
		for (int i=0; i<1036; i+=4)
		{
			if (i%28==0) 
			{
				System.out.println("Reading challenge "+((i/28)+1)+"'s battle parameters...");
				if (i!=0)
				{	
					input+="\n"; //end of row
					input = input.substring(0, input.length()-2)+"\n";
				}
			}
			val = LittleEndian.getInt(battleConfigData.readInt());
			input+=val+",";
		}
		input = input.substring(0, input.length()-1); //remove last comma
		writer = new FileWriter(new File(Main.RES_PATH+Main.MODE_SELECT[3]+"/chall-battle-cfg.csv"));
		writer.write(input); 
		writer.close(); battleConfigData.close();
	}
	public static void readOpponentConfigFile() throws FileNotFoundException, IOException
	{
		oppConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[3]+"/09_ub_ranking_enemy_prm.dat","r");
		long fileSize = oppConfigData.length();
		if (fileSize!=4416) //prevent EOFException
		{
			oppConfigData.close(); return;
		}
		String input=Mission100.OPP_CFG_HEADER;
		int val; FileWriter writer;
		for (int i=0; i<4356; i+=4)
		{
			if (i%44==0)
			{
				System.out.println("Reading mission "+((i/44)+1)+"'s opponent parameters...");
				if (i!=0)
				{	
					input+="\n"; //end of row
					input = input.substring(0, input.length()-2)+"\n";
				}
			}
			val = LittleEndian.getInt(oppConfigData.readInt());
			input+=val+",";
		}
		input = input.substring(0, input.length()-1); //remove last comma
		writer = new FileWriter(new File(Main.RES_PATH+Main.MODE_SELECT[3]+"/rank-opp-cfg.csv"));
		writer.write(input); writer.close();
		
		oppConfigData = new RandomAccessFile(Main.RES_PATH+Main.MODE_SELECT[3]+"/10_ub_ranking_challenge_enemy_prm.dat","r");
		fileSize = oppConfigData.length();
		if (fileSize!=1664) //prevent EOFException
		{
			oppConfigData.close(); return;
		}
		input=Mission100.OPP_CFG_HEADER;
		for (int i=0; i<1628; i+=4)
		{
			if (i%44==0)
			{
				System.out.println("Reading challenge "+((i/44)+1)+"'s opponent parameters...");
				if (i!=0)
				{	
					input+="\n"; //end of row
					input = input.substring(0, input.length()-2)+"\n";
				}
			}
			val = LittleEndian.getInt(oppConfigData.readInt());
			input+=val+",";
		}
		input = input.substring(0, input.length()-1); //remove last comma
		writer = new FileWriter(new File(Main.RES_PATH+Main.MODE_SELECT[3]+"/chall-opp-cfg.csv"));
		writer.write(input); 
		writer.close(); oppConfigData.close();
	}
	public static void writeBattleConfigFile(int missionID) throws FileNotFoundException, IOException
	{
		String root = Main.RES_PATH+Main.MODE_SELECT[3]+"/";
		int copyOfMissionID=missionID; //this is to reset the mission ID for the second iteration
		String[] csvNames = {"rank-battle-cfg.csv","chall-battle-cfg.csv"};
		String[] fileNames = {"07_ub_ranking_prm.dat","08_ub_ranking_challenge_prm.dat"};
		String[] missionType = {"mission","challenge"};
		int[] fileSizes = {2816,1088}, missionLimits={99,37};

		for (int i=0; i<2; i++)
		{
			missionID=copyOfMissionID;
			File selectedCSV = new File(root+csvNames[i]);
			Scanner sc = new Scanner(selectedCSV);
			sc.nextLine(); //skip header
			battleConfigData = new RandomAccessFile(root+fileNames[i],"rw");
			long fileSize = battleConfigData.length();
			if (fileSize!=fileSizes[i]) //prevent EOFException
			{
				battleConfigData.close(); sc.close(); return; 
			}
			int lineCnt=0, missionAddr=(missionID-1)*28, pos;
			battleConfigData.seek(missionAddr); pos=missionAddr;
			
			while (sc.hasNextLine())
			{
				String input = sc.nextLine();
				String[] inputArr = input.split(",");
				lineCnt++;
				if (missionID>missionLimits[i]) break;
				if (!Main.isSingleMission) missionID=lineCnt; //this will assure the 2nd if condition is ALWAYS TRUE
				if (lineCnt==missionID)
				{
					System.out.println("Overwriting "+missionType[i]+" "+missionID+"'s battle parameters...");
					for (String inputStr: inputArr)
					{
						int newInt = Integer.parseInt(inputStr);
						if (newInt<0) newInt=0; //prevent underflow
						System.out.println(battleConfigData.getFilePointer());
						int currInt = LittleEndian.getInt(battleConfigData.readInt()); //current position automatically increases by 4
						battleConfigData.seek(pos); //go back 4 bytes
						if (currInt!=newInt) battleConfigData.writeInt(LittleEndian.getInt(newInt));
						pos+=4;
					}
				}
			}
			battleConfigData.close(); sc.close();
		}
	}
	public static void writeOpponentConfigFile(int missionID) throws FileNotFoundException, IOException
	{
		String root = Main.RES_PATH+Main.MODE_SELECT[3]+"/";
		int copyOfMissionID=missionID; //this is to reset the mission ID for the second iteration
		String[] csvNames = {"rank-opp-cfg.csv","chall-opp-cfg.csv"};
		String[] fileNames = {"09_ub_ranking_enemy_prm.dat","10_ub_ranking_challenge_enemy_prm.dat"};
		String[] missionType = {"mission","challenge"};
		int[] fileSizes = {4416,1664}, missionLimits={99,37};
		
		for (int i=0; i<2; i++)
		{
			missionID=copyOfMissionID;
			File selectedCSV = new File(root+csvNames[i]);
			Scanner sc = new Scanner(selectedCSV);
			sc.nextLine(); //skip header
			oppConfigData = new RandomAccessFile(root+fileNames[i],"rw");
			long fileSize = oppConfigData.length();
			if (fileSize!=fileSizes[i]) //prevent EOFException
			{
				System.out.println(fileSize+"||"+fileSizes[i]);
				oppConfigData.close(); sc.close(); return; 
			}
			int lineCnt=0, missionAddr=(missionID-1)*44, pos;
			oppConfigData.seek(missionAddr); pos=missionAddr;
			
			while (sc.hasNextLine())
			{
				String input = sc.nextLine();
				String[] inputArr = input.split(",");
				lineCnt++;
				if (missionID>missionLimits[i]) break;
				if (!Main.isSingleMission) missionID=lineCnt; //this will assure the 2nd if condition is ALWAYS TRUE
				if (lineCnt==missionID)
				{
					System.out.println("Overwriting "+missionType[i]+" "+missionID+"'s opponent parameters...");
					for (String inputStr: inputArr)
					{
						int newInt = Integer.parseInt(inputStr);
						if (newInt<0) newInt=0; //prevent underflow
						System.out.println(oppConfigData.getFilePointer());
						int currInt = LittleEndian.getInt(oppConfigData.readInt()); //current position automatically increases by 4
						oppConfigData.seek(pos); //go back 4 bytes
						if (currInt!=newInt) oppConfigData.writeInt(LittleEndian.getInt(newInt));
						pos+=4;
					}
				}
			}
			oppConfigData.close(); sc.close();
		}
	}
}