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

    int getDistancs() throws RemoteException;

    void shot(IPlayer target) throws RemoteException;

    void decreaseLifes() throws RemoteException;

    void beer(String ip) throws RemoteException;

    void increaseLifes() throws RemoteException;

    void initPlayerList(ArrayList<String> ips) throws RemoteException;

}