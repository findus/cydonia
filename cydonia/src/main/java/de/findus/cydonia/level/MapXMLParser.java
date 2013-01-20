/**
 * 
 */
package de.findus.cydonia.level;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;

/**
 * @author Findus
 *
 */
public class MapXMLParser {

	private AssetManager assetManager;
	
	
	/**
	 * 
	 */
	public MapXMLParser(AssetManager assetManager) {
		this.assetManager = assetManager;
	}
	
	public Map loadMapFromFile(String filename) throws ParserConfigurationException, SAXException, IOException {
		return loadMap(new InputSource(ClassLoader.class.getResourceAsStream(filename)));
	}

	public Map loadMapFromXML(String xml) throws ParserConfigurationException, SAXException, IOException {
		return loadMap(new InputSource(new StringReader(xml)));
	}
	
	public Map loadMap(InputSource is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(is);

		String name = document.getElementsByTagName("map").item(0).getAttributes().getNamedItem("name").getNodeValue();

		Map map = new Map(name);
		map.setTargetAreas(parseTargetAreas(document.getElementsByTagName("targetarea")));
		map.setSpawnPoints(parseSpawnPoints(document.getElementsByTagName("spawnpoint")));
		map.setFlubes(parseFlubes(document.getElementsByTagName("flube")));
		return map;
	}

	private List<TargetArea> parseTargetAreas(NodeList ndlist) {
		LinkedList<TargetArea> list = new LinkedList<TargetArea>();	
		for(int i=0; i<ndlist.getLength(); i++ ) {
			Node n = ndlist.item(i);
			NamedNodeMap attr = n.getAttributes();
			int id = Integer.parseInt(attr.getNamedItem("id").getNodeValue());
			int width = Integer.parseInt(attr.getNamedItem("width").getNodeValue());
			int height = Integer.parseInt(attr.getNamedItem("height").getNodeValue());
			int depth = Integer.parseInt(attr.getNamedItem("depth").getNodeValue());
			float posx = Float.parseFloat(attr.getNamedItem("posx").getNodeValue());
			float posy = Float.parseFloat(attr.getNamedItem("posy").getNodeValue());
			float posz = Float.parseFloat(attr.getNamedItem("posz").getNodeValue());
			TargetArea ta = new TargetArea(id, new Vector3f(posx, posy, posz), width, height, depth, assetManager);
			list.add(ta);				
		}
		return list;
	}
	
	private List<SpawnPoint> parseSpawnPoints(NodeList ndlist) {
		LinkedList<SpawnPoint> list = new LinkedList<SpawnPoint>();	
		for(int i=0; i<ndlist.getLength(); i++ ) {
			Node n = ndlist.item(i);
			NamedNodeMap attr = n.getAttributes();
			int id = Integer.parseInt(attr.getNamedItem("id").getNodeValue());
			int team = Integer.parseInt(attr.getNamedItem("team").getNodeValue());
			float posx = Float.parseFloat(attr.getNamedItem("posx").getNodeValue());
			float posy = Float.parseFloat(attr.getNamedItem("posy").getNodeValue());
			float posz = Float.parseFloat(attr.getNamedItem("posz").getNodeValue());
			SpawnPoint sp = new SpawnPoint(id, new Vector3f(posx, posy, posz), team);
			list.add(sp);				
		}
		return list;
	}
	
	private List<Flube> parseFlubes(NodeList ndlist) {
		LinkedList<Flube> list = new LinkedList<Flube>();	
		for(int i=0; i<ndlist.getLength(); i++ ) {
			Node n = ndlist.item(i);
			NamedNodeMap attr = n.getAttributes();
			long id = Long.parseLong(attr.getNamedItem("id").getNodeValue());
			int type = Integer.parseInt(attr.getNamedItem("type").getNodeValue());
			float posx = Float.parseFloat(attr.getNamedItem("posx").getNodeValue());
			float posy = Float.parseFloat(attr.getNamedItem("posy").getNodeValue());
			float posz = Float.parseFloat(attr.getNamedItem("posz").getNodeValue());
			Flube f = new Flube(id, new Vector3f(posx, posy, posz), type, assetManager);
			list.add(f);				
		}
		return list;
	}

}
