package com.cyberark.authenticator;

import com.cyberark.m3lclient.M3LClient;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.security.MessageDigest;
import java.io.BufferedInputStream;

public class InvestigatorServer extends BasicServer {
    private IInvestigator m_Investigator;

    public InvestigatorServer(int port, IInvestigator inv) throws IOException {
        super(port);
        m_Investigator = inv;
    }

    protected String doLogic(Socket connection, String message) {
        try {
            String[] messageData = message.split(";");
            if (messageData.length != 2) {
                //return ("Invalid");
                throw new Exception("The request needs to contain the PID");
            }

            String investigated = String.valueOf(m_Investigator.getPIDFromSocket(connection.getPort()));
            String received = messageData[1];

            if (investigated.equals(received)) {
                // Find the app of the process
                String processCommandLine = m_Investigator.getCommandLineFromPID(investigated);

                String jarPath = getJarFromCommandLine(processCommandLine);

                String hash = calculateFileHash(jarPath);

                M3LClient m3lclient = new M3LClient();
                if (m3lclient.checkHash(hash)) {
                    System.out.println("Application is authorized");
                    return System.getProperty("password");
                } else {
                    String msg = "Application is unauthorized";
                    System.out.println(msg);
                    return msg;
                }
            } else {
                throw new Exception("The given PID is false");
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    protected String getJarFromCommandLine(String commandLine) throws Exception {
        String jarName = "";

        // Currently only Java is supported
        if (!commandLine.toLowerCase().contains("java")) {
            System.out.println("getJarFromCommandLine: the calling process doesn't run Java");
            throw new Exception("the calling process doesn't run Java");
        } else {
            try {
                String[] values = commandLine.split(" ");
                int index = Arrays.asList(values).indexOf("-jar");
                jarName = values[index + 1];

                System.out.println(String.format("getJarFromCommandLine: The jar name is [%s]", jarName));
            } catch (Exception e) {
                System.out.println(String.format("getJarFromCommandLine: invalid command line [%s]. Make sure you run java and that the jar contains the full path", commandLine));
                throw new Exception("Invalid command line - make sure you run java and that the jar contains the full path");
            }

            try {
                //Make sure that the path is full
                if (!Character.isLetter(jarName.charAt(0)) || jarName.charAt(1) != ':' || jarName.charAt(2) != '\\') {
                    throw new Exception("Invalid command line - the jar file is not a full Windows path");
                } else {
                    System.out.println(String.format("getJarFromCommandLine: jar name is ok [%s]", jarName));
                    return (jarName);
                }
            } catch (Exception e) {
                System.out.println(String.format("getJarFromCommandLine: jar name [%s] is not a full path", jarName));
                throw new Exception("Invalid command line - the jar file is not a full path");
            }
        }
    }

    protected String calculateFileHash(String filePath) throws Exception {
        String hashString = "";

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            try (InputStream input = new FileInputStream(filePath)) {

                byte[] buffer = new byte[8192];
                int len = input.read(buffer);

                while (len != -1) {
                    sha1.update(buffer, 0, len);
                    len = input.read(buffer);
                }

                hashString = new HexBinaryAdapter().marshal(sha1.digest());
            }
            System.out.println(String.format("calculateFileHash: calculated hash [%s]", hashString));

            return (hashString);
        } catch (Exception e) {
            System.out.println(String.format("calculateFileHash: could not calculate hash on [%s]", filePath));
            throw new Exception("Could not calculate the hash of the jar");
        }
    }
}