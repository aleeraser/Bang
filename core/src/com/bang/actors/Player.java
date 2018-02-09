import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.rmi.registry.LocateRegistry;

public class Player extends UnicastRemoteObject implements IPlayer {
    private int lifes;
    private String ip;
    //private ArrayList<Card> handCards = new ArrayList<Card>();
    //private ArrayList<Card> tableCards = new ArrayList<Card>();
    //private CharacterPower character;
    private int view; //ditanza a cui può sparare
    private int distance; //incremento della distanza a cui viene visto
    private ArrayList<IPlayer> players = new ArrayList<IPlayer>();
    private ArrayList<String> ips = new ArrayList<String>(); //valutare se tenere la lista di ip o di player
    private int pos; //index del player nella lista; sarà una lista uguale per tutti, quindi ognuno deve sapere la propria posizione

    public Player() throws RemoteException {
        /*this.CharacterPower = genCharacter();
        this.lifes = CharacterPower.lifes; */
        this.ip = findIp();
        this.view = 0;
        this.distance = 0;
        this.pos = -1;

    }

    /*
    public void setPlayerList(ArrayList<Player> pl) { //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
        this.players = pl;
    }
    
    public void setIpList(ArrayList<String> ips) { //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
        this.ips = ips;
    }
    */
    public String getIp() {
        return this.ip;
    }

    public int getPos() {
        return this.pos;
    }

    public ArrayList<IPlayer> getPlayers() {
        return this.players;
    }

    public int getLifes() {
        return this.lifes;
    }

    public int getDistancs() {
        return this.distance;
    }

    public void shot(IPlayer target, int i) {
        //todo implementare controllo distanza tra i e j 

        try {
            target.decreaseLifes();
            System.out.println(target.getLifes());
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è");

            this.players.remove(i);
            if (i < this.pos) {
                this.pos--;
            }
            //TODO mandare messaggi per eliminarlo dalla lista;
            //e.printStackTrace();
        }

    }

    public void resetPos() {

    }

    public void beer(IPlayer target, int i) {

        try {
            System.out.println("nella shot");
            target.increaseLifes();
            System.out.println(target.getLifes());
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è");

            this.players.remove(i);
            if (i < this.pos) {
                this.pos--;
            }

            //TODO mandare messaggi per eliminarlo dalla lista;
            //e.printStackTrace();
        }

    }

    public void decreaseLifes() {
        this.lifes--;
        if (this.lifes <= 0) {
            System.out.println("SONO MORTO"); //todo chiamare routine per aggiornare le liste dei player
        }
    }

    public void increaseLifes() {
        if (this.lifes < 5) {
            this.lifes++;
        }
    }

    private static String findIp() {
        SocketException exception = null;

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    String ip = i.getHostAddress();
                    if (ip.matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+") && !(ip.substring(0, 3).matches("127"))) {
                        System.out.println(ip);
                        return (ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            exception = e;
        }

        return exception.toString();
    }

    private Boolean ping(String ip) {
        try {
            InetAddress inet = InetAddress.getByName(ip);
            System.out.println("Sending Ping Request to " + ip);
            if (inet.isReachable(5000)) {
                System.out.println(ip + " is reachable.");
                return true;
            } else {
                System.out.println(ip + " is NOT reachable.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            return false; //todo rilanciare l'eccezione a livello superiore
        }
    }

    public void initPlayerList(ArrayList<String> ips) {
        int unreachable = 0;
        for (int i = 0; i < ips.size(); i++) {
            try {
                if (this.ip.matches(ips.get(i))) {
                    this.pos = i - unreachable;
                    this.players.add((IPlayer) this);
                } else if (this.ping(ips.get(i))) {
                    System.out.println("inizio naming");
                    IPlayer player = (IPlayer) Naming.lookup("rmi://" + ips.get(i) + "/Player");
                    System.out.println("finita naming");
                    this.players.add(player);
                } else
                    unreachable++;
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                unreachable++;
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    // todo : quando si capisce che uno non c'e' bisogna anche aggiornare il campo pos di tutti
    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", findIp());

        try {
            LocateRegistry.createRegistry(1099);
            IPlayer server = new Player();
            Naming.rebind("//" + server.getIp() + "/Player", server);

            ArrayList<String> iplist = new ArrayList<String>();
            iplist.add("130.136.4.232");
            iplist.add("1.2.3.4");
            iplist.add("130.136.154.77");
            server.initPlayerList(iplist);
            System.out.println("---->" + server.getPos());
            for (int i = 0; i < server.getPlayers().size(); i++) {
                if (i != server.getPos()) {
                    server.shot((IPlayer) server.getPlayers().get(i), i);
                    server.getPlayers().get(i).getLifes();
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}