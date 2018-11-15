package com.cyberark.authenticator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BasicServer
{
    private ServerSocket m_Socket;
    private Boolean m_Shutdown;

    public BasicServer(int port) throws IOException
    {
        m_Socket = new ServerSocket(port);
        m_Shutdown = false;
    }

    protected void send(Socket connection, String message) throws IOException
    {
        OutputStreamWriter output = new OutputStreamWriter(new BufferedOutputStream(connection.getOutputStream()));
        output.write(message);
        output.flush();
    }

    protected String receive(Socket connection) throws IOException
    {
        int character;
        InputStreamReader input = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));
        StringBuilder message = new StringBuilder();
        while ((character = input.read()) > 0)
        {
            message.append((char) character);
        }

        return (message.toString());
    }

    public void stop()
    {
        m_Shutdown = true;
    }

    public void run()
    {
        while (!m_Shutdown)
        {
            Socket connection = null;
            try
            {
                connection = m_Socket.accept();

                String message = this.receive(connection);

                String timeStamp = new java.util.Date().toString();
                String returnCode = "Message received at " + timeStamp + " Logic Result: " + this.doLogic(connection,message) + ((char) 0);
                this.send(connection, returnCode);
            }
            catch (IOException e)
            {
                System.err.println("Socket handling failed");
                e.printStackTrace();
            }
            catch (Exception e)
            {
                System.err.println("Error in processing the request");
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (connection != null)
                    {
                        connection.close();
                    }
                }
                catch(IOException e)
                {
                    System.err.println("Error in processing the request");
                    e.printStackTrace();
                }
            }
        }

        try
        {
            m_Socket.close();
        }
        catch (IOException e)
        {
            System.err.println("Failed to close socket");
            e.printStackTrace();
        }


    }

    protected String doLogic(Socket connection,String message) throws Exception
    {
        return ("None");
    }
}
