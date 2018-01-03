/**
 * Store the web page and the ad links which are related to it
 */
package domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Alberto Aranda García y Cristian Gómez Portes
 *
 */
public class Storage implements Serializable{
	private ArrayList<String> links;
	private String webpage;
	
	/**
	 * Constructor of the Storage class
	 * @param links
	 * @param webpage
	 */
	public Storage(ArrayList<String>links, String webpage) {
		this.links = links;
		this.webpage = webpage;
	}
	
	/**
	 * Get links
	 * @return
	 */
	public ArrayList<String> getLinks() {
		return links;
	}
	
	/**
	 * Set links
	 * @param links
	 */
	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}
	
	/**
	 * Get web page
	 * @return
	 */
	public String getWebpage() {
		return webpage;
	}
	
	/**
	 * Set web page
	 * @param webpage
	 */
	public void setWebpage(String webpage) {
		this.webpage = webpage;
	}
	
}
