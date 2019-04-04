package analysis;
import java.io.*;
import java.util.Scanner;
public class WinCounter {
	
	public static void main(String[] args) throws FileNotFoundException{
		File file = new File("/Users/tudor/Desktop/ECSE_424/pentago-swap/logs/outcomes.txt");
		BufferedReader br = null;
		String line = "";
		int TGwins0 = 0;
		int AIwins0 = 0;
		int TGwins1 = 0;
		int AIwins1 = 0;
		int draws = 0;
		br = new BufferedReader(new FileReader(file));
		try {
			while((line = br.readLine()) != null) {
				String[] winner = line.split(",");
				if(winner[3].equals("GAMEOVER DRAW")){//TIE
					draws++;
					continue;
				}
				else {
					if(Integer.parseInt(winner[0]) % 2 == 0) {//AI first
						if(Integer.parseInt(winner[3]) == 0){//AI won
							AIwins0++;
						}
						else {//TG won
							TGwins1++;
						}
					}
					else {//TG first
						if(Integer.parseInt(winner[3]) == 0){//TG won
							TGwins0++;
						}
						else {
							AIwins1++;
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("-------TG WINS-------");
		System.out.println("First: "+ TGwins0);
		System.out.println("Second: "+ TGwins1);
		System.out.println("-------AI WINS-------");
		System.out.println("First: "+ AIwins0);
		System.out.println("Second: "+ AIwins1);
		System.out.println("--------DRAWS--------");
		System.out.println("Draws: "+ draws);

		
		

	}
}
