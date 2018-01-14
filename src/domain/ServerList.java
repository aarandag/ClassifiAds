/**
 * Store all ad links into an ArrayList
 */
package domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * @author Alberto Aranda García
 * @author Cristian Gómez Portes
 *
 */
public class ServerList {
	private HashSet<String> serverList;
	
	/**
	 * Constructor of the ServerList class
	 */
	public ServerList(){
		this.serverList = read_ad_servers_file("ad_servers.txt");
	}
	
	/**
	 * Get serverList
	 * @return
	 */
	public HashSet<String> getServerList(){
		return serverList;
	}
	
	/**
	 * Set serverList
	 * @param serverList
	 */
	public void setServerList(HashSet<String> serverList) {
		this.serverList = serverList;
	}
	
	/**
	 * Whether the serverList contains or not a given server
	 * @param server
	 * @return
	 */
	public boolean contains(String server) {
		return serverList.contains(server);
	}
	
	/**
	 * Read file that contains all ad servers
	 * @param path
	 * @return
	 */
	private HashSet<String> read_ad_servers_file(String path){
		Scanner s =  null;
		try {
			s = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<String> list = new HashSet<String>();
		while (s.hasNextLine()){
		    list.add(s.next());
		}
		s.close();
		
		return list;
	}
}
