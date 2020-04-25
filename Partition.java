import java.io.*;
import java.util.Scanner;

class Partition {
    public static void main(String[] args) {
        int testcode = Integer.parseInt(args[0]);
        int algorithm = Integer.parseInt(args[1]);
        String inputFile = args[2];
        int[] numbers;

        if (testcode == 0) {
            numbers = new int[100];
            // Read numbers from input file
            try {  
                FileInputStream f = new FileInputStream(inputFile);       
                Scanner s = new Scanner(f); 
                int i = 0;
                while(s.hasNextLine()) {  
                    numbers[i] = Integer.parseInt(s.nextLine());
                    i++;
                }  
                s.close();  
            }
            catch(IOException e) {  
                e.printStackTrace();  
            }
            for (int number : numbers) {
                System.out.println(number);
            }
        }
    }  
}