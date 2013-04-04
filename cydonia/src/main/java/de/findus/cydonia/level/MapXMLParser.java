/**
 * 
 */
package de.findus.cydonia.level;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;

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
	
	/**
	 * Parses a Map from XML.
	 * 
	 * @param is the XML InputSource
	 * @return the Map object
	 * @throws JDOMException
	 * @throws IOException
	 */
	public Map loadMap(InputSource is) throws JDOMException, IOException {
		SAXBuilder sxbuild = new SAXBuilder();
		Document doc = sxbuild.build(is);
		Element root = doc.getRootElement();
		if(!root.getName().equals("map")) {
			System.out.println("No map format");
		}

		String name = root.getAttributeValue("name");
		float bottomHeight = Float.parseFloat(root.getAttributeValue("bottomHeight"));

		Map map = new Map(name);
		map.setBottomHeight(bottomHeight);
		map.setFlags(parseFlags(root));
		map.setSpawnPoints(parseSpawnPoints(root));
		map.setFlubes(parseFlubes(root));
		return map;
	}
	
	/**
	 * Writes a Map to XML.
	 * 
	 * @param level the Map object
	 * @return the XML representation of the Map
	 * @throws IOException
	 */
	public String writeMap(Map level) throws IOException {
		Element root = new Element("map");
		if(level != null) {
			root.setAttribute("name", level.getName());
			root.setAttribute("bottomHeight", String.valueOf(level.getBottomHeight()));

			root.addContent(writeFlags(level.getFlags()));
			root.addContent(writeSpawnPoints(level.getSpawnPoints()));
			root.addContent(writeFlubes(level.getFlubes()));
		}
		
		Document doc = new Document(root);
		StringWriter buffer = new StringWriter();
		XMLOutputter outputter = new XMLOutputter();
		outputter.output(doc, buffer);
		return buffer.toString();
	}

	private Collection<Element> writeFlags(List<Flag> list) {
		Collection<Element> col = new LinkedList<Element>();
		for(Flag f : list) {
			Element e = new Element("flag");
			e.setAttribute("id", String.valueOf(f.getId()));
			e.setAttribute("posx", String.valueOf(f.getOrigin().getX()));
			e.setAttribute("posy", String.valueOf(f.getOrigin().getY()));
			e.setAttribute("posz", String.valueOf(f.getOrigin().getZ()));
			col.add(e);
		}
		return col;
	}
	
	private Collection<Element> writeSpawnPoints(List<SpawnPoint> list) {
		Collection<Element> col = new LinkedList<Element>();
		for(SpawnPoint sp : list) {
			Element e = new Element("spawnpoint");
			e.setAttribute("id", String.valueOf(sp.getId()));
			e.setAttribute("posx", String.valueOf(sp.getPosition().getX()));
			e.setAttribute("posy", String.valueOf(sp.getPosition().getY()));
			e.setAttribute("posz", String.valueOf(sp.getPosition().getZ()));
			e.setAttribute("team", String.valueOf(sp.getTeam()));
			col.add(e);
		}
		return col;
	}
	
	private Collection<Element> writeFlubes(List<Flube> list) {
		Collection<Element> col = new LinkedList<Element>();
		for(Flube f : list) {
			Element e = new Element("flube");
			e.setAttribute("id", String.valueOf(f.getId()));
			e.setAttribute("type", String.valueOf(f.getType()));
			e.setAttribute("posx", String.valueOf(f.getOrigin().getX()));
			e.setAttribute("posy", String.valueOf(f.getOrigin().getY()));
			e.setAttribute("posz", String.valueOf(f.getOrigin().getZ()));
			col.add(e);
		}
		return col;
	}

	private List<Flag> parseFlags(Element root) {
		LinkedList<Flag> list = new LinkedList<Flag>();	
		for(Element e : root.getChildren("flag")) {
			int id = Integer.parseInt(e.getAttributeValue("id"));
			int team = Integer.parseInt(e.getAttributeValue("team"));
			float posx = Float.parseFloat(e.getAttributeValue("posx"));
			float posy = Float.parseFloat(e.getAttributeValue("posy"));
			float posz = Float.parseFloat(e.getAttributeValue("posz"));
			Flag f = FlagFactory.getInstance().createFlag(id, new Vector3f(posx, posy, posz), team);
			list.add(f);				
		}
		return list;
	}
	
	private List<SpawnPoint> parseSpawnPoints(Element root) {
		LinkedList<SpawnPoint> list = new LinkedList<SpawnPoint>();	
		for(Element e : root.getChildren("spawnpoint")) {
			int id = Integer.parseInt(e.getAttributeValue("id"));
			int team = Integer.parseInt(e.getAttributeValue("team"));
			float posx = Float.parseFloat(e.getAttributeValue("posx"));
			float posy = Float.parseFloat(e.getAttributeValue("posy"));
			float posz = Float.parseFloat(e.getAttributeValue("posz"));
			SpawnPoint sp = new SpawnPoint(id, new Vector3f(posx, posy, posz), team, assetManager);
			list.add(sp);				
		}
		return list;
	}
	
	private List<Flube> parseFlubes(Element root) {
		LinkedList<Flube> list = new LinkedList<Flube>();	
		for(Element e : root.getChildren("flube")) {
			long id = Long.parseLong(e.getAttributeValue("id"));
			int type = Integer.parseInt(e.getAttributeValue("type"));
			float posx = Float.parseFloat(e.getAttributeValue("posx"));
			float posy = Float.parseFloat(e.getAttributeValue("posy"));
			float posz = Float.parseFloat(e.getAttributeValue("posz"));
			Flube f = new Flube(id, new Vector3f(posx, posy, posz), type, assetManager);
			list.add(f);				
		}
		return list;
	}

}
