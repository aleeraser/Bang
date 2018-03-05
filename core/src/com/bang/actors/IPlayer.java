package com.bang.actors;

import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPlayer extends Remote {

    //void setPlayerList(ArrayList<Player> pl); //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.

    void setIpList(ArrayList<String> ips) throws RemoteException; //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.

    String getIp() throws RemoteException;

    int getLifes(int[] callerClock) throws RemoteException;

    int getPos(int[] callerClock) throws RemoteException;

    Clock getClock(int[] callerClock) throws RemoteException;

    ArrayList<IPlayer> getPlayers(int[] callerClock) throws RemoteException; 

    ArrayList<IPlayer> getPlayers() throws RemoteException; 

    void refreshPList() throws RemoteException;

    ArrayList<Card> getCards(int[] callerClock) throws RemoteException; 

    Card getHandCard(int i,int[] callerClock) throws RemoteException;

    int getDistance(int[] callerClock) throws RemoteException;
    
    void setDeck(Deck deck,int[] callerClock) throws RemoteException;

    void decreaseLifes(int[] callerClock) throws RemoteException;

    void indiani(int[] callerClock) throws RemoteException;

    void removeTableCard(int index,int[] callerClock) throws RemoteException;

    void removeHandCard(int index,int[] callerClock) throws RemoteException;

    void removePlayer( int index, String ip,int[] callerClock) throws RemoteException;

    void increaseLifes(int[] callerClock) throws RemoteException;

    void initPlayerList(ArrayList<String> ips) throws RemoteException;

}