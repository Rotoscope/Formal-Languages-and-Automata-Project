package automaton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Automaton {

    static ArrayList<Transition> transition_table = new ArrayList();
    static String num_states;
    static int position = 0;
    static String[] alphabets, final_s;

    public static void main(String[] args) {
	final String initial_state = "0";
	String state = initial_state, symbol;
	String[] tokens;
	boolean[] final_states;
	int automaton_number = 1;

	//TODO: replace "file" with filename
	try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
	    String line = "";

	    do {
		//begin filling in variables
		if ((line = br.readLine()) == null) {
		    System.exit(1);
		}
		num_states = line;

		final_states = new boolean[Integer.parseInt(num_states)];
		if ((line = br.readLine()) == null) {
		    System.exit(1);
		}
		final_s = line.split(" ");
		for (String token : final_s) {
		    final_states[Integer.parseInt(token)] = true;
		}

		if ((line = br.readLine()) == null) {
		    System.exit(1);
		}
		alphabets = line.split(" ");

		//fill in transition table
		while ((line = br.readLine()) != null && line.startsWith("(")) {
		    tokens = line.split(" |\\(|\\)");

		    for (int i = 3; i < tokens.length; i++) {
			Transition t = new Transition();
			t.state = tokens[1];
			t.symbol = tokens[2];
			t.next_state = tokens[i];

			transition_table.add(t);
		    }
		}

		printInfo(automaton_number);

		while (line != null && !line.equals("@")) {
		    while (position < line.length() || line.length() == 0) {
			if(line.length() != 0) {
			    symbol = getNextSymbol(line);
			} else {
			    symbol = "";
			}

			//check if symbol is in the alphabet array
			if (isAlphabet(symbol)) {
			    //loop through transition table to get next state if exist; if not, 
			    //it is dead_end state then reject string and exit
			    state = getNextState(state, symbol);

			    if (state.equals(num_states)) {
				printResult(line, "REJECT");
				break;
			    } else if (position == line.length()) {
				if (final_states[Integer.parseInt(state)]) {
				    printResult(line, "ACCEPT");
				    break;
				} else {
				    printResult(line, "REJECT");
				    break;
				}
			    }
			} else {
			    if (position != line.length()) {
				printResult(line, "REJECT");
				break;
			    } else if (final_states[Integer.parseInt(state)]
				    && (isAlphabet(symbol) || symbol.isEmpty())) {
				printResult(line, "ACCEPT");
				break;
			    } else {
				printResult(line, "REJECT");
				break;
			    }
			}
		    }

		    position = 0;
		    state = initial_state;
		    line = br.readLine();
		}

		transition_table.clear();
		automaton_number++;
	    } while (line != null && line.equals("@"));
	} catch (Exception e) {
	    System.out.println(e);
	}
    }

    public static class Transition {

	public String state, next_state, symbol;
    }

    //gets current state and given symbol and searches transition table for next state
    public static String getNextState(String state, String symbol) {
	for (Transition t : transition_table) {
	    if (state.equals(t.state) && symbol.equals(t.symbol)) {
		//curr state and next st
		return t.next_state;
	    }
	}

	//dead_end state returned; state and given symbol not in table
	return num_states;
    }

    public static boolean isAlphabet(String symbol) {
	for (String alphabet : alphabets) {
	    if (symbol.equals(alphabet)) {
		return true;
	    }
	}

	return false;
    }

    public static String getNextSymbol(String line) {
	return String.valueOf(line.charAt(position++));
    }

    public static void printResult(String line, String result) {
	System.out.printf("%s\t %s\n", line, result);
    }

    public static void printInfo(int auto_num) {
	String out;

	System.out.printf("Finite State Automaton #%d\n", auto_num);
	System.out.printf("1) number of states: %s\n", num_states);

	if (final_s.length == 0) {
	    out = "none";
	} else {
	    out = final_s[0];
	    for (int i = 1; i < final_s.length; i++) {
		out += ", ";
		out += final_s[i];
	    }
	}
	System.out.printf("2) final states: %s\n", out);

	if (alphabets.length == 0) {
	    out = "none";
	} else {
	    out = alphabets[0];
	    for (int i = 1; i < alphabets.length; i++) {
		out += ", ";
		out += alphabets[i];
	    }
	}
	System.out.printf("3) alphabets: %s\n", out);

	System.out.println("4) transitions:");
	transition_table.stream().forEach((t) -> {
	    System.out.printf("\t%s %s %s\n", t.state, t.symbol, t.next_state);
	});

	System.out.println("5) strings:");
    }

}
