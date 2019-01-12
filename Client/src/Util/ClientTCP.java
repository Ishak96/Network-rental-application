package Util;

import java.io.BufferedReader ;
import java.io.IOException ;
import java.io.InputStream;
import java.io.InputStreamReader ;
import java.io.OutputStream;
import java.io.PrintWriter ;
import java.net.NoRouteToHostException;
import java.net.Socket ;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import log.LoggerUtility;

public class ClientTCP
{	
	private static ClientTCP instance = new ClientTCP(ServerParameters.getServ_addr(), ServerParameters.getPort());
	
	private static Logger logger = LoggerUtility.getLogger(ClientTCP.class);

	private Socket socket;
	private BufferedReader inputReader;
	private PrintWriter outputWriter;
		
	private boolean connectError;
	private boolean hostError;
	private boolean portError;
	
	
	private ClientTCP(String serv_addr, int port)
	{
		try {
            socket = new Socket (serv_addr, port);
            
            OutputStream outputS = socket.getOutputStream ();
            InputStream inputS = socket.getInputStream ();
            
            inputReader = new BufferedReader (new InputStreamReader(inputS));
            outputWriter = new PrintWriter (outputS,true);
            
            socket.setSoTimeout(ServerParameters.TIME_OUT);
            
            connectError = false;
            hostError = false;
            portError=false;
                                                            
        }catch(UnknownHostException e) {
        	hostError=true;
        }
        catch (NoRouteToHostException e){
        	connectError = true;
        }
		catch(IOException e) {
			portError = true;
		}
	}
	
	public static void setInstance(String serv_addr, int port) 
	{
		instance = new ClientTCP(serv_addr, port);
	}

	public static ClientTCP getInstance() 
	{
		return instance;
	}
	
	public boolean portFailed() 
	{
		return portError;
	}
	
	public boolean connexionFailed() 
	{
		return connectError;
	}

	public boolean unreachableHost() 
	{
		return hostError;
	}
		
	public void send(String data){
		outputWriter.println(data);
		logger.info("requête-client ---> [ "+data+" ]");
	}
	
	public String recieve() throws SocketTimeoutException,IOException{
		String input = inputReader.readLine();
		logger.info("réponse-serveur <--- [ "+input+" ]");
		if(input==null) {
			throw new IOException("SERVER_DOWN");
		}
		return input;
	}

	
	public String[] getData(String operation,String replay,String infos) throws SocketTimeoutException,IOException
	{	
		String[] result = null;
		send(operation);
		if(recieve().equals(replay)){
			send(infos);
			result = recieve().split(":");
		}
		else {
    		logger.error("réponse de serveur inattendue.");
		}
		return result;			
	}
	
	public void closeConnexion()
	{		
	    try {
			socket.close ();
			outputWriter.close ();
		    inputReader.close ();
		    System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
