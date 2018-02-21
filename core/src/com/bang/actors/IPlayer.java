package com.bang.actors;

import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPlayer extends Remote {

    //void setPlayerList(ArrayList<Player> pl); //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.

    //void setIpList(ArrayList<String> ips) throws RemoteException; //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.

    String getIp() throws RemoteException;

    int getLifes() throws RemoteException;

    int getPos() throws RemoteException;

    ArrayList<IPlayer> getPlayers() throws RemoteException; 

    ArrayList<Card> getCards() throws RemoteException; 

    Card getHandCard(int i) throws RemoteException;

    int getDistance() throws RemoteException;
    
    void setDeck(ArrayList<Card> deck) throws RemoteException;

    void shot(IPlayer target, int i) throws RemoteException;

    void decreaseLifes() throws RemoteException;

    void playCard(int index, int targetIndex) throws RemoteException;

    void playCard(int index) throws RemoteException;

    void indiani() throws RemoteException;

    void removeTableCard(int index) throws RemoteException;

    void removeHandCard(int index) throws RemoteException;

    void removePlayer( int index, String ip) throws RemoteException;

    void beer(IPlayer target, int i) throws RemoteException;

    void increaseLifes() throws RemoteException;

    void initPlayerList(ArrayList<String> ips) throws RemoteException;

}