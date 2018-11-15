package com.cyberark.authenticator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Investigator implements IInvestigator
{
    private final String CMD_NETSTAT = "netstat -ano";
    private final String CMD_TASKLIST = "wmic path win32_process get processID, commandLine /format:csv";

    public int getPIDFromSocket(int port)
    {
        String[] output = cmdExec(String.format(CMD_NETSTAT, port)).split("\n");
        for (int i = 0; i < output.length; ++i)
        {
            if (output[i].matches("\\s+TCP\\s+127\\.0\\.0\\.1\\:" + port + "\\s+127\\.0\\.0\\.1\\:[1-9]{1,5}\\s+.*"))
            {
                String[] rowData = output[i].split(" ");
                int pid = Integer.parseInt(rowData[rowData.length - 1]);
                return pid;
            }
        }

        return -1;
    }

    public String getCommandLineFromPID(String pid)
    {
        String[] output = cmdExec(CMD_TASKLIST).split("\n");
        for (int i = 0; i < output.length; ++i)
        {
            //System.out.println(output[i]);

            try
            {
                String[] values = output[i].split(",");
                String commandLine = values[1];
                String currentPid = values[2];

                if (currentPid.equals(pid))
                {
                    System.out.println(String.format("getCommandLineFromPID: found PID %s [%s]", currentPid, commandLine));
                    return commandLine;
                }
            }
            catch(Exception e)
            {
                // System.out.println("getCommandLineFromPID: invalid line, skipping");
            }
        }

        return "";
    }

    private String cmdExec(String cmdLine)
    {
        String line;
        String output = "";
        try
        {
            Process p = Runtime.getRuntime().exec(cmdLine);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null)
            {
                output += (line + '\n');
            }
            input.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return output;
    }
}
