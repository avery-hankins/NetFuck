import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.util.*;

public class Netfuck {
    static boolean output_compiled = false;
    static boolean print_errors = false;
    static boolean verbose = false;

    static int pointer = 0;
    static int programPointer = 0;
    static short[] cells = new short[30000];
    static Socket[] cellSockets = new Socket[30000];
    static DataInputStream[] cellIn = new DataInputStream[30000];
    static DataOutputStream[] cellOut = new DataOutputStream[30000];
    static ServerSocket sSocket;
    static Scanner scan = new Scanner(System.in);
    static int[] loops;
    static ArrayList<Character> buffer = new ArrayList<>();
    static ArrayList<Character> inputBuffer = new ArrayList<>();
    static SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

    static String address = "";
    static int port;


    public static void main(String[] args) {
        String programPath = null;

        for (String argument : args) {
            if (argument.equals("-s")) {
                output_compiled = true;
            }
            else if (argument.equals("-e")) {
                print_errors = false;
            }
            else if (argument.equals("-v")) {
                verbose = true;
            }
            else if (programPath == null) {
                programPath = argument;
            }
            else {
                System.out.println("Error, incorrect arguments");
                return;
            }
        }

        if (programPath == null) {
            System.out.println("Error, incorrect arguments");
            return;
        }

        try {
            BufferedReader codeReader = new BufferedReader(new FileReader(programPath));

            //pre-processing of code
            //manage insert chars {}
            String code = String.join("", codeReader.lines().toList().toArray(new String[0]));
            boolean compiled = false;
            while (code.contains("{")) {
                compiled = true;
                int startIndex = code.indexOf("{");
                int endIndex = code.indexOf("}");

                StringBuilder replacement = new StringBuilder();
                //iterate through each character
                for (int j = 1; j < endIndex - startIndex; j++) {
                    replacement.append("\n[-]\n");
                    int c = (int) code.charAt(startIndex + j);

                    for (int i = 0; i < c; i++) {
                        replacement.append("+");
                        if ((i + 1) % 10 == 0) {
                            replacement.append("\n");
                        } else if ((i + 1) % 5 == 0) {
                            replacement.append(" ");
                        }
                    }

                    replacement.append(">\n\n");
                }

                for (int i = 0; i < (endIndex - startIndex) - 1; i++) {
                    replacement.append("<");
                }
                replacement.append("\n\n");
                code = code.substring(0, startIndex) + replacement + code.substring(endIndex + 1);
            }
            if (compiled && output_compiled) {
                PrintWriter codeWriter = new PrintWriter(new FileWriter(programPath + "_comp"));
                codeWriter.print(code);
                codeWriter.close();
            }

            codeReader.close();

            String tempCode = code;

            loops = new int[code.length()];

            while (tempCode.contains("[")) {
                //find index of right-most [, and then nearest ] from that must signify loop
                String reverseTemp = new StringBuilder(tempCode).reverse().toString();

                int startIndex = reverseTemp.indexOf("[");
                startIndex = (code.length() - 1) - startIndex;

                int endIndex = tempCode.substring(startIndex).indexOf("]") + startIndex;

                if (endIndex == -1 || endIndex <= startIndex) {
                    System.out.println("Error reading program! Make sure all brackets are matching.");
                    return;
                }

                loops[startIndex] = endIndex;
                loops[endIndex] = startIndex;

                tempCode = tempCode.substring(0, startIndex) + "X" + tempCode.substring(startIndex + 1);
                tempCode = tempCode.substring(0, endIndex) + "X" + tempCode.substring(endIndex + 1);
            }

            //individual operation management
            while (programPointer != code.length()) {
                char op = code.charAt(programPointer);

                switch (op) {
                    case '>':
                        incptr();
                        break;
                    case '<':
                        decptr();
                        break;
                    case '+':
                        add();
                        break;
                    case '-':
                        sub();
                        break;
                    case '.':
                        out();
                        break;
                    case ',':
                        in();
                        break;
                    case '[':
                        loopstart();
                        break;
                    case ']':
                        loopend();
                        break;
                    case '`':
                        setport();
                        break;
                    case '?':
                        openport();
                        break;
                    case '%':
                        composeIP();
                        break;
                    case '~':
                        connect();
                        break;
                    case '=':
                        acceptConnection();
                        break;
                    case '!':
                        closeConnection();
                        break;
                    case '^':
                        addValue();
                        break;
                    case '_':
                        sendValues();
                        break;
                    case 'V':
                        receiveValue();
                        break;
                    case '#':
                        sleep();
                        break;
                }

                programPointer++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setport() {
        port = cells[pointer];
    }
    
    private static void openport() {
        try {
            sSocket = new ServerSocket(port);
            if (verbose) {
                System.out.println("\nAccepting connections on port " + sSocket.getLocalPort());
            }
        } catch (Exception e) {
            cells[pointer] = -1;
            if (print_errors) {
                e.printStackTrace();
            }
        }
    }

    private static void acceptConnection() {
        try {
            Socket socket = sSocket.accept();
            if (verbose) {
                System.out.println("Connection accepted!");
            }
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            cellSockets[pointer] = socket;
            cellIn[pointer] = input;
            cellOut[pointer] = output;
        } catch (Exception e) {
            cells[pointer] = -1;
            if (print_errors) {
                e.printStackTrace();
            }
        }
    }

    private static void composeIP() {
        address += String.valueOf((char) cells[pointer]);
    }

    private static void connect() {
        try {
            Socket socket;
            if (address.startsWith("https://")) {
                address = address.substring(8);
                socket = sslsocketfactory.createSocket(address, port);
            } else {
                socket = new Socket(address, port);
            }

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            if (verbose) {
                System.out.println("\nConnected to " + address + ", on port " + port);
            }

            cellSockets[pointer] = socket;
            cellIn[pointer] = input;
            cellOut[pointer] = output;
        } catch (Exception e) {
            cells[pointer] = -1;
            if (print_errors) {
                e.printStackTrace();
            }
        } finally {
            address = "";
        }
    }

    private static void closeConnection() {
        try {
            cellSockets[pointer].close();
            cellIn[pointer].close();
            cellOut[pointer].close();

            cellSockets[pointer] = null;
            cellIn[pointer] = null;
            cellOut[pointer] = null;
        } catch (Exception e) {
            cells[pointer] = -1;
            if (print_errors) {
                e.printStackTrace();
            }
        }
    }

    private static void addValue() {
        buffer.add((char) cells[pointer]);
    }

    private static void sendValues() {
        if (verbose) {
            System.out.print("Sending: ");
        }

        try {
            for (char c : buffer) {
                cellOut[pointer].write(c);
                if (verbose) {
                    System.out.print(c);
                }
            }
            if (verbose) {
                System.out.println();
            }
            cellOut[pointer].flush();
            buffer = new ArrayList<>();
        } catch (Exception e) {
            cells[pointer] = -1;
            buffer = new ArrayList<>();
            if (print_errors) {
                e.printStackTrace();
            }
        }
    }

    private static void receiveValue() {
        try {
            cells[pointer] = (short) cellIn[pointer].read();
        } catch (Exception e) {
            cells[pointer] = -1;
            if (print_errors) {
                e.printStackTrace();
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(cells[pointer]);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (print_errors) {
                e.printStackTrace();
            }
        }
    }

    private static void incptr() {
        pointer++;
        if (pointer == 30000) {
            pointer = 0;
        }
    }

    private static void decptr() {
        pointer--;
        if (pointer == -1) {
            pointer = 29999;
        }
    }

    private static void add() {
        cells[pointer]++;
    }

    public static void sub() {
        cells[pointer]--;
    }

    private static void out() {
        System.out.print((char) cells[pointer]);
    }

    private static void in() {
        char in;
        if (inputBuffer.isEmpty()) {
            String line = scan.nextLine();

            for (char c : line.toCharArray()) {
                inputBuffer.add(c);
            }

            in = line.charAt(0);
            inputBuffer.removeFirst();
        } else {
            in = inputBuffer.getFirst();
            inputBuffer.removeFirst();
        }

        cells[pointer] = (short) in;
        System.out.println((int) in);
    }

    private static void loopstart() {
        if (cells[pointer] == 0) {
            //skip to closest ]
            programPointer = loops[programPointer];
        }
    }

    private static void loopend() {
        if (cells[pointer] != 0) {
            //go back to closest [
            programPointer = loops[programPointer];
        }
    }
}
