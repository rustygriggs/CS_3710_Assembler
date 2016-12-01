package assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class Assembler {
	//TODO: stick all these constants in their own file
	private static final String REG_1 = "$R1";
	private static final String REG_2 = "$R2";
	private static final String REG_3 = "$R3";
	private static final String REG_4 = "$R4";
	private static final String REG_5 = "$R5";
	private static final String REG_6 = "$R6";
	private static final String REG_7 = "$R7";
	private static final String REG_8 = "$R8";
	private static final String R_STRING = "$Rstring";
	private static final String R_SCREEN = "$Rscreen";

	private static final String RESET_BIN 	= "000000";
	private static final String LOAD_BIN 	= "000001";
	private static final String STORE_BIN	= "000010";
	private static final String MOVE_BIN 	= "000011";
	private static final String NOT_BIN 	= "000100";
	private static final String AND_BIN 	= "000101";
	private static final String OR_BIN 		= "000110";
	private static final String SHIFTR_BIN 	= "000111";
	private static final String SHIFTL_BIN 	= "001000";
	private static final String ADD_BIN 	= "001001";
	private static final String SUB_BIN 	= "001010";
	private static final String TEST_BIN 	= "001011";
	private static final String JMPEQ_BIN 	= "001100";
	private static final String JMPLE_BIN 	= "001101";
	private static final String JMPGE_BIN 	= "001110";
	private static final String JMPL_BIN 	= "001111";
	private static final String JMPG_BIN 	= "010000";
	private static final String JMP_BIN 	= "010001";
	private static final String CALL_BIN	= "010010";
	public static final String JMPNE_BIN	= "010011";
	private static final String STORE_R_BIN = "011000";
	private static final String JMPF_BIN 	= "011001";
	private static final String FUNCTION_BIN 	= "0000000000010010";

	private static final String RESET = "reset";
	private static final String LOAD = "load";
	private static final String STORE = "store";
	private static final String MOVE = "move";
	private static final String NOT = "not";
	private static final String AND = "and";
	private static final String OR = "or";
	private static final String SHIFTR = "shiftr";
	private static final String SHIFTL = "shiftl";
	private static final String ADD = "add";
	private static final String SUB = "sub";
	private static final String TEST = "test";
	private static final String JMPEQ = "jmpEq";
	private static final String JMPNE = "jmpNE";
	private static final String JMPLE = "jmpLE";
	private static final String JMPGE = "jmpGE";
	private static final String JMPL = "jmpL";
	private static final String JMPG = "jmpG";
	private static final String JMP = "jmp";
	private static final String CALL = "call";
	private static final String PRINT = "print";
	private static final String STORE_R = "storeR";
	private static final String JMPF = "jmpF";

	private static final int PROGRAM_OFFSET = 9216;

	static int _endOfProgram = 0;
	static List<Character> stringCharacters = new ArrayList<>();
	static Map<String, Integer> _functionLocations;
	static int _screenPosition = 1536;
	static boolean firstPrint = true;


	public static void main(String[] args) {

        String programName = "userInputTest";
        List<String> programLines = readFile("C:/Users/Rusty/workspace/CS_3710/bin/assembler/" + programName);
		//System.out.println(programLines.toString());
		List<List<String>> programInstructions = parseProgramLines(programLines);
		//gets the constants and glyphs from file with a "," at the end of each line.
		List<String> constantsAndGlyphs = readFile("C:/Users/Rusty/workspace/CS_3710/src/assembler/constants_and_glyphs.txt");
		
		//probably don't need this until I write jump instructions and things like that. but it has been implemented.
		_functionLocations = calcFunctionLocations(programInstructions);
		
		List<String> binaryInstructionList = convertInstrToBinary(programInstructions);
        for (String s : binaryInstructionList) {
            //System.out.println(s);
        }
		List<String> hexInstructionList = convertBinInstrToHex(binaryInstructionList);

		List<String> outputList = new ArrayList<>(constantsAndGlyphs);
		for (int i = 1536; i <= 9215; i++) {
			outputList.add(i, "0000,");
		}

		outputList.add(0, "memory_initialization_vector=");
		outputList.add(0, "memory_initialization_radix=16;");
		outputList.addAll(hexInstructionList);
		//convert characters to strings and add them to the end of the program
		List<String> finalStringCharStrings = getCharacterStringsForEndOfFile();
		outputList.addAll(finalStringCharStrings);

		String lastInstr = outputList.get(outputList.size() -1);
		lastInstr = lastInstr.substring(0, lastInstr.length()-1);
		lastInstr = lastInstr + ";";
		outputList.set(outputList.size() - 1, lastInstr);
		
		saveToFile(programName + ".coe", outputList);		
	}

	private static List<String> getCharacterStringsForEndOfFile() {
		List<String> stringCharStrings = new ArrayList<>();
		for (int i = 0; i < stringCharacters.size(); i++ ) {
			int charInt = stringCharacters.get(i);
			stringCharStrings.add(Integer.toHexString(charInt));
		}
		List<String> finalStringCharStrings = new ArrayList<>();
		for (int i = 0; i < stringCharStrings.size(); i++) {
			String s = stringCharStrings.get(i);
			if (s.length() == 1) {
				s = "000" + s;
			}
			else if (s.length() == 2) {
				s = "00" + s;
			}
			else if (s.length() == 3) {
				s = "0" + s;
			}
			finalStringCharStrings.add(s + ',');
		}
		return finalStringCharStrings;
	}

	private static void saveToFile(String programFileName, List<String> outputList) {
		try {
			File fout = new File(programFileName);
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			int index = -2;
			for (String anOutputList : outputList) {

				bw.write(anOutputList);
				System.out.println(Integer.toHexString(index) + " " + anOutputList);
				index++;
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> convertBinInstrToHex(List<String> binaryInstructionList) {
		List<String> hexLines = new ArrayList<>();
		for (int i = 0; i < binaryInstructionList.size(); i++) {
			String line = binaryInstructionList.get(i);
			line = line.substring(0, 16);
			int binary = Integer.parseInt(line, 2);
			String hexStr = Integer.toString(binary, 16);

			if (hexStr.length() == 1) {
				hexStr = "000" + hexStr;
			}
			else if (hexStr.length() == 2) {
				hexStr = "00" + hexStr;
			}
			else if (hexStr.length() == 3) {
				hexStr = "0" + hexStr;
			}

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
			//check if it's a function and assign it an arbitrary value
			if (progInstruction.get(0).endsWith(":") && progInstruction.size() == 1) {
				binaryLine.append(FUNCTION_BIN);
			}
			else if (progInstruction.get(0).equals(RESET)) {
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
			else if (progInstruction.get(0).equals(JMPNE)) {
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, progInstruction,
						binaryLine, JMPNE_BIN);
			}
			else if (progInstruction.get(0).equals(STORE_R)) {
				convertToMachineCodeInstruction(progInstruction, binaryLine, STORE_R_BIN);
			}
			else if (progInstruction.get(0).equals(JMPF)) {
				binaryLine.append("0000000000");
				binaryLine.append(JMPF_BIN);
//				convertToMachineCodeInstruction(progInstruction, binaryLine, JMPF_BIN);
			}
			else if (progInstruction.get(0).equals(PRINT)) {
				if (firstPrint) {
					//loads the screen position into the screen pos register into the
					List<String> loadRscreenInstruction = new ArrayList<>();
					loadRscreenInstruction.add("load");
					loadRscreenInstruction.add(R_SCREEN);
					String hexScreenStart = Integer.toHexString(_screenPosition);
					hexScreenStart = "0x" + hexScreenStart;
					loadRscreenInstruction.add(hexScreenStart);
					binaryLine = new StringBuilder();
					binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, loadRscreenInstruction,
							binaryLine, LOAD_BIN);
					binaryLine.append(',');
					machineCodeInstructionList.add(binaryLine.toString());

					//loads the string characters position into the $Rstring register
					List<String> loadRStringInstruction = new ArrayList<>();
					loadRStringInstruction.add("load");
					loadRStringInstruction.add(R_STRING);
					String hexEndOfProgram = Integer.toHexString(_endOfProgram + 9216);
					hexEndOfProgram = "0x" + hexEndOfProgram;
					loadRStringInstruction.add(hexEndOfProgram);
					binaryLine = new StringBuilder();
					binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, loadRStringInstruction,
							binaryLine, LOAD_BIN);
					binaryLine.append(',');
					machineCodeInstructionList.add(binaryLine.toString());
					firstPrint = false;
				}
				StringBuilder sb = new StringBuilder();
				for (int j = 1; j < progInstruction.size(); j++) {
					if (j == progInstruction.size() - 1) {
						sb.append(progInstruction.get(j));
					}
					else {
						sb.append(progInstruction.get(j) + " ");
					}
				}
				String stringToBeAdded = sb.toString();
				for (int j = 0; j < stringToBeAdded.length(); j++) {
					if (stringToBeAdded.charAt(j) != '"') {
						stringCharacters.add(stringToBeAdded.charAt(j));
					}
				}
				stringCharacters.add('`');
				List<String> callPrintInstruction = new ArrayList<>();
				callPrintInstruction.add("call");
				callPrintInstruction.add("Print:");
				binaryLine = new StringBuilder();
				binaryLine = getBinaryLineAndConvertToMachineCode(machineCodeInstructionList, callPrintInstruction,
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
		String functionLocation;
		if (firstArg.endsWith(":")) { //this argument is actually a function/label.
			int functionLocationInt = _functionLocations.get(firstArg);
			functionLocation = Integer.toBinaryString(functionLocationInt);
			firstArg = functionLocation;
		}

		String secondArg = "";
		if (progInstruction.size() > 2) {
			secondArg = calcArgBin(progInstruction.get(2));
		}
		binaryLine.append("00000");

		if (progInstruction.size() > 2) {
			binaryLine.append(firstArg);
		}
		else {
			binaryLine.append("00000");
		}
//		binaryLine.append(firstArg);
		binaryLine.append(instrBinaryCode);
		machineCodeInstructionList.add(binaryLine.toString() + ",");

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
			else if (arg.equals(R_STRING)) {
				return "10100"; //register 20
//				String binaryString = Integer.toString(_endOfProgram, 2);
//				return binaryString;
			}
			else if (arg.equals(R_SCREEN)) {
				return "10101"; //register 21
//				return Integer.toBinaryString(_screenPosition);
//				return "10010000000000"; //TODO: this is just for now...will need to be a variable that can be incremented and stuff
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
			return convertHexAddressToBinary(arg.substring(2, arg.length()));
		}
		//if it's a function, it ends with ":" and just needs to go to the next line
		else if (arg.endsWith(":")) {
			//TODO: do I need to do anything here?
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
		int decimalInteger = Integer.parseInt(arg, 16);
		String binaryString = Integer.toString(decimalInteger, 2);
		return binaryString;
	}

	private static String calcFirstArg(String arg) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Map<String, Integer> calcFunctionLocations(List<List<String>> programInstructions) {
//TODO: actually check functions
		Map<String, Integer> functionLocations = new HashMap<>();
		int functionLocationCounter = 0;
		for (int i = 0; i < programInstructions.size(); i++) {
			List<String> instruction = programInstructions.get(i);
			String instr = instruction.get(0);

			//I need to add up the instructions that take two lines and add them to the actual counter
			if (instr.equals(STORE) || instr.equals(LOAD) || instr.equals(MOVE) || instr.equals(JMPLE) ||
					instr.equals(JMPL) || instr.equals(JMP) || instr.equals(JMPEQ) || instr.equals(JMPGE) ||
					instr.equals(JMPG) || instr.equals(JMPNE) || instr.equals(CALL)) {
				functionLocationCounter++;
			}
			if (instr.equals(PRINT)) {
				functionLocationCounter++;
			}

			//if it ends with a : it is a function and we add it to the functionLocations map
			if (instr.endsWith(":") && instruction.size() == 1) {
				functionLocations.put(instr, i + functionLocationCounter + PROGRAM_OFFSET + 4); //the 4 is for the extra lines from print
			}
		}

		_endOfProgram = functionLocationCounter + programInstructions.size() + 2; //the 4 is for the 4 lines produced by the first print function
		return functionLocations;
	}

	private static List<List<String>> parseProgramLines(
			List<String> programLines) {
		List<List<String>> listOfLists = new ArrayList<>();
		for (String instruction : programLines) {
			List<String> tokens = new ArrayList<>(Arrays.asList(instruction.split("\\s+")));
			boolean isPartOfComment = false;
			for (Iterator<String> iterator = tokens.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				if (string.startsWith("#")) {
					// Remove the current element from the iterator and the list.
					isPartOfComment = true;
				}
				if (isPartOfComment) {
					iterator.remove();
				}
			}
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
