package org.testng.code;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SampleApplication {

    public static void main(String[] args) {

        try
        {
            // Run and get the output.
            //need to change it as OS specific - below command for windows
            String outlist[] = runCommand("C:\\Users\\hkannan\\Desktop\\sofwares\\apache-maven-3.8.4-bin.tar\\apache-maven-3.8.4\\bin\\mvn.cmd test -f C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\sym\\java-testng\\pom.xml -DopenApiUrl=http://localhost:9010/v3/api-docs/");
            // Print the output to screen character by character.
            // Safe and not very inefficient.
            for (int i = 0; i < outlist.length; i++)
                System.out.println(outlist[i]);
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }


        static public String[] runCommand(String cmd)throws IOException
        {

            // The actual procedure for process execution:
            //runCommand(String cmd);
            // Create a list for storing output.
            ArrayList list = new ArrayList();
            // Execute a command and get its process handle
            Process proc = Runtime.getRuntime().exec(cmd);
            // Get the handle for the processes InputStream
            InputStream istr = proc.getInputStream();
            // Create a BufferedReader and specify it reads
            // from an input stream.

            BufferedReader br = new BufferedReader(new InputStreamReader(istr));
            String str; // Temporary String variable
            // Read to Temp Variable, Check for null then
            // add to (ArrayList)list
            while ((str = br.readLine()) != null)
                list.add(str);
            // Wait for process to terminate and catch any Exceptions.
            try {
                proc.waitFor();
            }
            catch (InterruptedException e) {
                System.err.println("Process was interrupted");
            }
            // Note: proc.exitValue() returns the exit value.
            // (Use if required)
            br.close(); // Done.
            // Convert the list to a string and return
            return (String[])list.toArray(new String[0]);
        }


}
