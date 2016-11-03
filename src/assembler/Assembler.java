package assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {
	//TODO: stick all these constants in their own file
	public static final String REG_1 = "$R1";
	public static final String REG_2 = "$R2";
	public static final String REG_3 = "$R3";
	public static final String REG_4 = "$R4";
	public static final String REG_5 = "$R5";
	public static final String REG_6 = "$R6";
	public static final String REG_7 = "$R7";
	public static final String REG_8 = "$R8";

	public static final String RESET_BIN 	= "000000";
	public static final String LOAD_BIN 	= "000001";
	public static final String STORE_BIN	= "000010";
	public static final String MOVE_BIN 	= "000011";
	public static final String NOT_BIN 		= "000100";
	public static final String AND_BIN 		= "000101";
	public static final String OR_BIN 		= "000110";
	public static final String SHIFTR_BIN 	= "000111";
	public static final String SHIFTL_BIN 	= "001000";
	public static final String ADD_BIN 		= "001001";
	public static final String SUB_BIN 		= "001010";
	public static final String TEST_BIN 	= "001011";
	public static final String JMPEQ_BIN 	= "001100";
	public static final String JMPLE_BIN 	= "001101";
	public static final String JMPGE_BIN 	= "001110";
	public static final String JMPL_BIN 	= "001111";
	public static final String JMPG_BIN 	= "010000";
	public static final String JMP_BIN 		= "010001";
	public static final String CALL_BIN		= "010010";

	public static final String RESET = "reset";
	public static final String LOAD = "load";
	public static final String STORE = "store";
	public static final String MOVE = "move";
	public static final String NOT = "not";
	public static final String AND = "and";
	public static final String OR = "or";
	public static final String SHIFTR = "shiftr";
	public static final String SHIFTL = "shiftl";
	public static final String ADD = "add";
	public static final String SUB = "sub";
	public static final String TEST = "test";
	public static final String JMPEQ = "jmpEq";
	public static final String JMPLE = "jmpLE";
	public static final String JMPGE = "jmpGE";
	public static final String JMPL = "jmpL";
	public static final String JMPG = "jmpG";
	public static final String JMP = "jmp";
	public static final String CALL = "call";


	public static void main(String[] args) {
		List<String> programLines = readFile("C:/Users/Rusty/workspace/CS_3710/src/assembler/sampleProgram");
		String programName = "sampleProgram";
		//System.out.println(programLines.toString());
		List<List<String>> programInstructions = parseProgramLines(programLines);
		//gets the constants and glyphs from file with a "," at the end of each line.
		List<String> constantsAndGlyphs = readFile("C:/Users/Rusty/workspace/CS_3710/src/assembler/constants_and_glyphs.txt");
		
		//probably don't need this until I write jump instructions and things like that. but it has been implemented.
		Map<String, Integer> functionLocations = calcFunctionLocations(programInstructions);
		
		List<String> binaryInstructionList = convertInstrToBinary(programInstructions);
		List<String> hexInstructionList = convertBinInstrToHex(binaryInstructionList);
		for (String s : binaryInstructionList) {
			//System.out.println(s);
		}
		for (String s : hexInstructionList) {
			//System.out.println(s);
		}
		
		List<String> outputList = new ArrayList<>(constantsAndGlyphs);
		outputList.add(0, "memory_initialization_vector=");
		outputList.add(0, "memory_initialization_radix=16;");
		for (String instr : hexInstructionList) {
			outputList.add(instr);
		}
		String lastInstr = outputList.get(outputList.size() -1);
		lastInstr = lastInstr.substring(0, lastInstr.length()-1);
		lastInstr = lastInstr + ";";
		outputList.set(outputList.size() - 1, lastInstr);
		
		saveToFile(programName + ".coe", outputList);		
	}
	
	private static void saveToFile(String programFileName, List<String> outputList) {
		try {
			File fout = new File(programFileName);
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
			for (int i = 0; i < outputList.size(); i++) {
				bw.write(outputList.get(i));
				System.out.println(outputList.get(i));
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> convertBinInstrToHex(
			List<String> binaryInstructionList) {
		List<String> hexLines = new ArrayList<>();
		for (int i = 0; i < binaryInstructionList.size(); i++) {
			String line = binaryInstructionList.get(i);
			String firstHalf = line.substring(0, 8);
			String secondHalf = line.substring(8, 16);
			int binFirstHalf = Integer.parseInt(firstHalf, 2);
			int binSecondHalf = Integer.parseInt(secondHalf, 2);
			String hexStr = Integer.toString(binFirstHalf, 16) + Integer.toString(binSecondHalf, 16);
			
			hexLines.add(hexStr + ",");
		}
		return hexLines;
	}

	private static List<String> convertInstrToBinary(List<List<String>> programInstructions) {
		List<String> machineCodeInstructionList = new ArrayList<>();
		int machineCodeInstrCounter = 0;

		for (int i = 0; i < programInstructions.size(); i++) {
			List<String> progInstruction = programInstructions.get(i);
			StringBuilder binaryLine = new StringBuilder();
			
			if (progInstruction.get(0).equals(RESET)) {
				binaryLine.append(RESET_BIN);
				binaryLine.append("0000000000");
			}
			else if (progInstruction.get(0).equals(LOAD)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction, binaryLine, LOAD_BIN);
			}
			else if (progInstruction.get(0).equals(STORE)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction, binaryLine, STORE_BIN);
			}
			else if (progInstruction.get(0).equals(MOVE)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, MOVE_BIN);
			}
			else if (progInstruction.get(0).equals(NOT)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, NOT_BIN);
			}
			else if (progInstruction.get(0).equals(AND)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, AND_BIN);
			}
			else if (progInstruction.get(0).equals(OR)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, OR_BIN);
			}
			else if (progInstruction.get(0).equals(SHIFTR)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, SHIFTR_BIN);
			}
			else if (progInstruction.get(0).equals(SHIFTL)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, SHIFTL_BIN);
			}
			else if (progInstruction.get(0).equals(ADD)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, ADD_BIN);
			}
			else if (progInstruction.get(0).equals(SUB)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, SUB_BIN);
			}
			else if (progInstruction.get(0).equals(TEST)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, TEST_BIN);
			}
			else if (progInstruction.get(0).equals(JMPEQ)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction,
						binaryLine, JMPEQ_BIN);
			}
			else if (progInstruction.get(0).equals(JMPLE)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction,
						binaryLine, JMPLE_BIN);
			}
			else if (progInstruction.get(0).equals(JMPGE)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction,
						binaryLine, JMPGE_BIN);
			}
			else if (progInstruction.get(0).equals(JMPL)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction,
						binaryLine, JMPL_BIN);
			}
			else if (progInstruction.get(0).equals(JMPG)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction,
						binaryLine, JMPG_BIN);
			}
			else if (progInstruction.get(0).equals(JMP)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction,
						binaryLine, JMP_BIN);
			}
			else if (progInstruction.get(0).equals(CALL)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction,
						binaryLine, CALL_BIN);
			}
			else {
				//TODO: keep adding instructions calculations here.
			}
			binaryLine.append(',');
			machineCodeInstructionList.add(binaryLine.toString());
		}
		return machineCodeInstructionList;
	}

	private static StringBuilder getBinaryLineAndConvertToMachineCode(List<String> machineCodeInstructionList, List<String> progInstruction,
																	  StringBuilder binaryLine, String instrBinaryCode) {
		String firstArg = calcArgBin(progInstruction.get(1));
		String secondArg = "";
		if (progInstruction.size() > 2) {
			secondArg = calcArgBin(progInstruction.get(2));
			binaryLine.append(secondArg);
		}
		else {
			binaryLine.append("00000");
		}
		binaryLine.append(firstArg);
		binaryLine.append(instrBinaryCode);
		machineCodeInstructionList.add(binaryLine.toString());

		binaryLine = new StringBuilder();
		StringBuilder zeros = new StringBuilder();

		if (progInstruction.size() > 2) {
			for (int j = 0; j < 16 - secondArg.length(); j++) {
                zeros.append("0");
            }
			binaryLine.append(zeros + secondArg);
		}
		else {
			for (int j = 0; j < 16 - firstArg.length(); j++) {
				zeros.append("0");
			}
			binaryLine.append(zeros + firstArg);
		}
		return binaryLine;
	}

	private static void convertToMachineCodeInstruction(List<String> progInstruction, StringBuilder binaryLine, String instrBinaryCode) {
		String firstArg = calcArgBin(progInstruction.get(1));
		if (progInstruction.size() > 2) {
			String secondArg = calcArgBin(progInstruction.get(2));
			binaryLine.append(secondArg);
		}
		//else make sure the instruction is long enough (16 bits)
		else {
			binaryLine.append("00000");
		}
		binaryLine.append(firstArg);
		binaryLine.append(instrBinaryCode);
	}

	private static String calcArgBin(String arg) {
		if (arg.startsWith("$")) {
			if (arg.equals(REG_1)) {
				return "00001";
			} else if (arg.equals(REG_2)) {
				return "00010";
			} else if (arg.equals(REG_3)) {
				return "00011";
			} else if (arg.equals(REG_4)) {
				return "00100";
			} else if (arg.equals(REG_5)) {
				return "00101";
			} else if (arg.equals(REG_6)) {
				return "00110";
			} else if (arg.equals(REG_7)) {
				return "00111";
			} else if (arg.equals(REG_8)) {
				return "01000";
			}
			//if it's not a predefined register (1-8), then it's an incorrect argument and an exception is thrown
			else {
				try {
					String message = String.format("%s is not a predefined register, choose from $R1 to $R8", arg);
					throw new IncorrectArgumentException(message);
				} catch (IncorrectArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		//if it's an address, starts with '0x'
		else if (arg.startsWith("0x")) {
			return convertHexAddressToBinary(arg.substring(2, arg.length() - 1));
		}
		//if it's anything else, it's probably just a number
		else {
			int integerArg = Integer.parseInt(arg);
			String binaryArg =Integer.toString(integerArg, 2);
			StringBuilder binaryArgSB = new StringBuilder();
			if (binaryArg.length() > 5) {
				try {
					throw new IncorrectArgumentException("immediate number is too big, must be less than 32");
				} catch (IncorrectArgumentException e) {
					e.printStackTrace();
				}
			}
			else if (binaryArg.length() < 5) {
				for (int i = 0; i < 5 - binaryArg.length(); i++) {
					binaryArgSB.append("0");
				}
				binaryArgSB.append(binaryArg);
				return binaryArgSB.toString();
			}
			return binaryArg;
		}
		return arg;
	}

	private static String convertHexAddressToBinary(String arg) {
		int hexInteger = Integer.parseInt(arg, 16);
		String binaryString = Integer.toString(hexInteger, 2);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 16 - binaryString.length(); i++) {
			sb.append("0");
		}
		sb.append(binaryString);
		return sb.toString();
	}

	private static String calcFirstArg(String arg) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Map<String, Integer> calcFunctionLocations(List<List<String>> programInstructions) {

		Map<String, Integer> functionLocations = new HashMap<>();
		for (int i = 0; i < programInstructions.size(); i++) {
			List<String> instruction = programInstructions.get(i);
			String instr = instruction.get(0);
			//if it starts with a . it is a function and we add it to the functionLocations map 
			if (instr.startsWith(".")) {
				functionLocations.put(instr, i);
			}
		}
		return functionLocations;
	}

	private static List<List<String>> parseProgramLines(
			List<String> programLines) {
		List<List<String>> listOfLists = new ArrayList<>();
		for (String instruction : programLines) {
			List<String> tokens = Arrays.asList(instruction.split("\\s+"));
			listOfLists.add(tokens);
		}
		return listOfLists;
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
