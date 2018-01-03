/**
 * Store all ad links into an ArrayList
 */
package domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Alberto Aranda García y Cristian Gómez Portes
 *
 */
public class ServerList {
	private ArrayList<String> serverList;
	
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
	public ArrayList<String> getServerList(){
		return serverList;
	}
	
	/**
	 * Set serverList
	 * @param serverList
	 */
	public void setServerList(ArrayList<String> serverList) {
		this.serverList = serverList;
	}
	
	/**
	 * Read file that contains all ad servers
	 * @param path
	 * @return
	 */
	private ArrayList<String> read_ad_servers_file(String path){
		Scanner s =  null;
		try {
			s = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNextLine()){
		    list.add(s.next());
		}
		s.close();
		
		return list;
	}
}
