package com.cyberark.authenticator;

public interface IInvestigator
{
    public int getPIDFromSocket(int port);
    public String getCommandLineFromPID(String pid);
}
