package Util;

public class ServerParameters {
	
	public static String serv_addr="127.0.0.1";
	public static int port=5000;
	public static final int TIME_OUT = 10000;
	
	public static String getServ_addr() {
		return serv_addr;
	}
	public static void setServ_addr(String serv_addr) {
		ServerParameters.serv_addr = serv_addr;
	}
	public static int getPort() {
		return port;
	}
	public static void setPort(int port) {
		ServerParameters.port = port;
	}
}
