package convertCoeGlyphToHex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ConvertCoeGlyphToHex {

	public static void main(String[] args) {
			ArrayList<Character> chars = new ArrayList<Character>();
			String fileName = "C:/Users/Rusty/Xilinx/VGA_top/CS_3710/glyph_buffer.coe";

			List<String> fileLines = readFile(fileName);
			
			//convert binary to hex.
			List<String> convertedTextList = convertBinToHex(fileLines);
			try {
				File fout = new File("hex_glyph_buffer.coe");
				FileOutputStream fos = new FileOutputStream(fout);
			 
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			 
				for (int i = 0; i < 1024; i++) {
					if (i < 16) {
						bw.write("000" + Integer.toString(i, 16) + ',');
					}
					else if (i <= 255) {
						bw.write("00" + Integer.toString(i, 16) + ',');
					}
					else {
						bw.write("0" + Integer.toString(i, 16) + ',');
					}
					//bw.write(Integer.toString(i, 16) + ',');
					bw.newLine();
				}
				for (int i = 0; i < convertedTextList.size(); i++) {
					bw.write(convertedTextList.get(i));
					System.out.println(convertedTextList.get(i));
					bw.newLine();
				}
			 
				bw.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
	}

	private static List<String> convertBinToHex(List<String> lines) {
		// TODO Auto-generated method stub
		List<String> binLines = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.charAt(line.length() - 1) == ',') {
				binLines.add(line);
			}
		}
		
		List<String> hexLines = new ArrayList<>();
		hexLines.add("memory_initialization_radix=16;");
		hexLines.add("memory_initialization_vector=");
		for (int i = 0; i < binLines.size(); i++) {
			String line = binLines.get(i);
			String firstHalf = line.substring(0, 8);
			String secondHalf = line.substring(8, 16);
			int binFirstHalf = Integer.parseInt(firstHalf, 2);
			int binSecondHalf = Integer.parseInt(secondHalf, 2);
			String hexStr = Integer.toString(binFirstHalf, 16) + Integer.toString(binSecondHalf, 16);
			
			hexLines.add(hexStr + ",");
		}
		
		return hexLines;
	}

	private static List<String> readFile(String fileName) {
		
		List<String> lines = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	lines.add(line);
		    }
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
}
