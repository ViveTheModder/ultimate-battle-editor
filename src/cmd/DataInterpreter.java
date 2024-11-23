package cmd;
//Ultimate Battle Editor v1.1 - Data Interpreter
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class DataInterpreter 
{
	private static final String OUT_PATH = "./out/";
	private static final String REF_PATH = "./ref/";
	private static final String[] REF_CSV_NAMES = 
	{"bgm.csv","characters.csv","conditions.csv","dp.csv","items.csv","maps.csv","referees.csv","times.csv"};
	private static final String[] REF_KEYWORDS =
	{"bgm","chara","condition","dp","item","map","referee","time"};
	private static final int REF_CSV_CNT = 8;
	private static final int MAX_ROWS = 440; 
	private static String[][] refCsvContents = new String[REF_CSV_CNT][MAX_ROWS];
	private static String getStringFromAnyID(int csvIndex, int ID) throws IOException
	{
		setCsvContents(csvIndex);
		if (ID==998) return "Random";
		if (ID==999) return null;
		String text = refCsvContents[csvIndex][ID];
		if (text==null) return "UNKNOWN (ID: "+ID+")";
		return refCsvContents[csvIndex][ID];
	}
	public static void interpretData(int modeIndex) throws IOException
	{
		File[] fileArray = new File(Main.RES_PATH+Main.MODE_SELECT[modeIndex]+'/').listFiles((dir, name) -> (name.endsWith(".csv")));
		Main.fileTotal = fileArray.length;
		Main.bar.setMaximum(Main.fileTotal);
		for (File f: fileArray)
		{
			int inputCnt=0;
			Scanner sc = new Scanner(f);
			sc.useDelimiter(",");
			String header = sc.nextLine();
			String[] columns = header.split(",");
			String output = header.replaceAll("-id", "")+"\n";
			while (sc.hasNextLine())
			{
				String line = sc.nextLine();
				String[] inputArr = line.split(",");
				for (String input: inputArr)
				{
					if (columns[inputCnt].contains("-id"))
					{
						int refIndex = Arrays.binarySearch(REF_KEYWORDS, columns[inputCnt].replace("-id", "").split("-")[0]);
						int inputNum = Integer.parseInt(input);
						input = getStringFromAnyID(refIndex,inputNum);
					}
					output+=input;
					output+=","; inputCnt++;
					if (inputCnt==columns.length) 
					{
						output+="\n";
						output = output.substring(0, output.length()-2)+"\n";
						inputCnt=0;
					}
				}
			}
			output = output.substring(0, output.length()-1); //remove last comma
			FileWriter writer = new FileWriter(OUT_PATH+f.getName());
			writer.write(output);
			writer.close(); sc.close();
			Main.fileCnt++; Main.bar.setValue(Main.fileCnt);
		}
	}
	private static void setCsvContents(int csvIndex) throws IOException
	{
		if (refCsvContents[csvIndex][0]!=null) return; //skip already-initialized CSV contents
		File csv = new File(REF_PATH+REF_CSV_NAMES[csvIndex]);
		
		Scanner sc = new Scanner(csv);
		while (sc.hasNextLine())
		{
			String input = sc.nextLine();
			String[] inputArray = input.split(",");
			int nameIndex = Integer.parseInt(inputArray[0]);
			refCsvContents[csvIndex][nameIndex] = inputArray[1];
		}
		sc.close();
	}
}