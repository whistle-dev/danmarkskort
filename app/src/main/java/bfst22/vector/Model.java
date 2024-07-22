package bfst22.vector;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Model {
    private float minlat, minlon, maxlat, maxlon;
    private int id2 = 0;
    private Address address = null;
    public TrieTree trie = new TrieTree();
    private OSMNode osmnode = null;
    private ArrayList<OSMWay> highways = new ArrayList<OSMWay>();
    private Map<WayType, List<Drawable>> lines = new EnumMap<>(WayType.class);
    private ArrayList<Edge> edgeList = new ArrayList<>();
    private NodeMap id2node = new NodeMap();
    private EdgeWeightedDigraph graph;
    private KDTree kdTree = new KDTree();
    private KDTree roadTree = new KDTree();
    private String wayName = null;
    private int maxSpeed = 0;
    private boolean isHighway = false;

    {
        for (WayType type : WayType.values())
            lines.put(type, new ArrayList<>());
    }
    List<Runnable> observers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Model(String filename)
            throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        long time = -System.nanoTime();
        if (filename.endsWith(".zip")) {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(filename));
            zip.getNextEntry();
            loadOSM(zip);
        } else if (filename.endsWith(".osm")) {
            loadOSM(new FileInputStream(filename));
        } else if (filename.endsWith(".obj")) {
            try (ObjectInputStream input = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(filename)))) {
                minlat = input.readFloat();
                minlon = input.readFloat();
                maxlat = input.readFloat();
                maxlon = input.readFloat();
                lines = (Map<WayType, List<Drawable>>) input.readObject();
            }
        }
        time += System.nanoTime();
        System.out.println("Load time: " + (long) (time / 1e6) + " ms");
    }

    public void save(String basename) throws FileNotFoundException, IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(lines);
        }
    }

    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError {
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input));
        Map<Long, OSMWay> id2way = new HashMap<>();
        List<OSMNode> nodes = new ArrayList<>();
        List<OSMWay> rel = new ArrayList<>();
        long relID = 0;
        WayType type = WayType.UNKNOWN;
        String relationType = "";
        List<OSMWay> multipolygonWays = new ArrayList<>();
        double timeTwo = -System.nanoTime();

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    String name = reader.getLocalName();
                    switch (name) {
                        case "bounds":
                            maxlat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                            minlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            minlat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                            maxlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            break;
                        case "node":
                            long id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            float lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            float lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            osmnode = new OSMNode(id, id2, 0.56f * lon, -lat);
                            id2++;
                            id2node.add(osmnode);
                            break;
                        case "nd":
                            long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));

                            nodes.add(id2node.get(ref));
                            break;
                        case "way":
                            relID = Long.parseLong(reader.getAttributeValue(null, "id"));

                            type = WayType.UNKNOWN;
                            break;
                        case "tag":
                            String k = reader.getAttributeValue(null, "k");
                            String v = reader.getAttributeValue(null, "v");
                            if (k.equals("type"))
                                relationType = v;
                            switch (k) {
                                case "place":
                                    switch (v) {
                                        case "island":
                                        case "islet":
                                        case "peninsula":
                                            type = WayType.LAND;
                                            break;
                                        default:
                                            type = WayType.UNKNOWN;
                                            break;
                                    }
                                case "natural":
                                    switch (v) {
                                        case "coastline":
                                            type = WayType.COASTLINE;
                                            break;
                                        case "stone":
                                            type = WayType.STONE;
                                            break;
                                        case "lake":
                                        case "water":
                                            type = WayType.LAKE;
                                            break;
                                        case "wetland":
                                            type = WayType.FOREST;
                                        default:
                                            break;
                                    }
                                    break;
                                case "building":
                                case "farmyard":
                                    type = WayType.BUILDING;
                                    break;
                                case "aerodrome":
                                    type = WayType.CITY;
                                    break;
                                case "leisure":
                                    type = WayType.FOREST;
                                    break;
                                case "landuse":
                                    switch (v) {
                                        case "forest":
                                        case "meadow":
                                            type = WayType.FOREST;
                                            break;
                                        case "residential":
                                        case "industrial":
                                            type = WayType.CITY;
                                            break;
                                        case "quarry":
                                            type = WayType.STONE;
                                            break;
                                        default:
                                            type = WayType.UNKNOWN;
                                            break;
                                    }
                                    break;
                                case "highway":
                                    switch (v) {
                                        case "waterway":
                                            type = WayType.WATERWAY;
                                            break;
                                        case "primary":
                                        case "trunk":
                                        case "secondary":
                                        case "trunk_link":
                                        case "secondary_link":
                                            isHighway = true;
                                            type = WayType.HIGHWAY;
                                            break;
                                        case "road":
                                        case "unclassified":
                                        case "tertiary":
                                        case "tertiary_link":
                                            type = WayType.ROAD;
                                            isHighway = true;
                                            break;
                                        case "residential":
                                        case "service":
                                        case "living_street":
                                        case "pedestrian":
                                            type = WayType.CITYWAY;
                                            isHighway = true;
                                            break;
                                        case "path":
                                        case "track":
                                        case "cycleway":
                                            isHighway = false;
                                            type = WayType.PATH;
                                            break;
                                        case "motorway":
                                        case "motorway_link":
                                            isHighway = true;
                                            type = WayType.MOTORWAY;
                                            break;
                                        default:
                                            type = WayType.UNKNOWN;
                                            break;
                                    }

                                    break;
                                case "addr:city":
                                    if (address == null) {
                                        address = new Address(osmnode);
                                    }
                                    address.setCity(v.intern());
                                    if (address.isFull()) {
                                        addAddress();
                                    }
                                    break;
                                case "addr:postcode":
                                    if (address == null) {
                                        address = new Address(osmnode);
                                    }
                                    address.setPostcode(v.intern());
                                    if (address.isFull()) {
                                        addAddress();
                                    }
                                    break;
                                case "addr:housenumber":
                                    if (address == null) {
                                        address = new Address(osmnode);
                                    }
                                    address.setHousenumber(v.intern());
                                    if (address.isFull()) {
                                        addAddress();
                                    }
                                    break;
                                case "addr:street":
                                    if (address == null) {
                                        address = new Address(osmnode);
                                    }
                                    address.setStreet(v.intern());
                                    if (address.isFull()) {
                                        addAddress();
                                    }
                                    break;
                                case "name":
                                    wayName = v;
                                    break;
                                case "maxspeed":
                                    if (isHighway) {
                                        if (v.equals("signals")) {

                                        } else {
                                            Double d = Double.parseDouble(v);
                                            maxSpeed = (int) Math.round(d);
                                        }
                                    }
                                default:
                                    break;
                            }
                            break;
                        case "member":
                            OSMWay member = id2way.get(Long.parseLong(reader.getAttributeValue(null, "ref")));
                            if (member != null) {
                                multipolygonWays.add(member);
                            }
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            OSMWay elm = id2way.get(ref);
                            if (elm != null)
                                rel.add(elm);
                            break;
                        case "relation":
                            id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            if (id == 1305702) {
                                System.out.println("Done");
                            }
                            break;

                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "way":
                            PolyLine way;
                            if (isHighway) {
                                OSMWay highway = new OSMWay(nodes, wayName, maxSpeed);
                                highways.add(highway);
                                isHighway = false;
                                way = new PolyLine(nodes, type, wayName);
                            } else {
                                way = new PolyLine(nodes, type);
                                isHighway = false;
                            }
                            isHighway = false;
                            id2way.put(relID, new OSMWay(nodes, wayName, maxSpeed));
                            lines.get(type).add(way);
                            nodes.clear();
                            break;
                        case "relation":
                            if (relationType.equals("multipolygon")) {
                                MultiPolygon multiPolygon = new MultiPolygon(multipolygonWays, type);
                                lines.get(type).add(multiPolygon);
                            }
                            relationType = "";
                            multipolygonWays.clear();
                            rel.clear();
                            break;
                    }
                    break;
            }
        }
        timeTwo += System.nanoTime();
        System.out.println("Parsing Done in " + (long) (timeTwo / 1e6) + "ms.");
        timeTwo = -System.nanoTime();
        fillTrees();
        timeTwo += System.nanoTime();
        System.out.println("KDTree filled in: " + (long) (timeTwo / 1e6) + " ms");

        createGraph();
    }

    public void addObserver(Runnable observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (Runnable observer : observers) {
            observer.run();
        }
    }

    public Iterable<Drawable> iterable(WayType type) {
        return lines.get(type);
    }

    public void addAddress() {
        trie.insert(address.toString(), address.getCords());
        address = null;
    }

    public void fillTrees() {
        ArrayList<Drawable> main = new ArrayList<>();
        ArrayList<Drawable> roads = new ArrayList<>();

        for (WayType e : WayType.values()) {
            for (Drawable l : iterable(e)) {
                if (l.getType() == WayType.HIGHWAY || l.getType() == WayType.CITYWAY
                        || l.getType() == WayType.MOTORWAY) {
                    roads.add(l);
                } else {
                main.add(l);
                }
            }
        }

        roadTree.fillTree(roads, 0);
        kdTree.fillTree(main, 0);
    }

    public void createGraph() {
        /**
         * constructs Edges from ArrayList highways, and tracks how many vertices there
         * are
         */
        for (OSMWay o : highways) {
            if (o.getSpeedLimit() == 0) {
                o.setSpeedLimit(50);
            }

            for (int j = 0; j < o.getNodes().size() - 1; j++) {

                double distance = distanceCalc(o.getNodes().get(j).getID(), o.getNodes().get(j + 1).getID());
                Edge e = new Edge(o.getNodes().get(j).getID(), o.getNodes().get(j + 1).getID(),
                        o.getNodes().get(j).getID2(),
                        o.getNodes().get(j + 1).getID2(), distance, distance);
                e.addFromC(o.getNodes().get(j).getX(), o.getNodes().get(j).getY());
                e.addToC(o.getNodes().get(j + 1).getX(), o.getNodes().get(j + 1).getY());
                Edge f = new Edge(o.getNodes().get(j + 1).getID(), o.getNodes().get(j).getID(),
                        o.getNodes().get(j + 1).getID2(),
                        o.getNodes().get(j).getID2(), distance, distance);

                f.addFromC(o.getNodes().get(j + 1).getX(), o.getNodes().get(j + 1).getY());
                f.addToC(o.getNodes().get(j).getX(), o.getNodes().get(j).getY());
                edgeList.add(e);
                edgeList.add(f);

            }
        }
        graph = new EdgeWeightedDigraph(id2);

        /**
         * Adds edges to the graph.
         */

        for (Edge e : edgeList) {
            graph.addEdge(e);
        }
    }

    public EdgeWeightedDigraph getGraph() {
        return graph;
    }

    Double distanceCalc(long from, long to) {
        double R = 6371 * 1000;
        double lat1 = id2node.get(from).getX() * Math.PI / 180;
        double lat2 = id2node.get(to).getX() * Math.PI / 180;
        double deltaLat = (lat2 - lat1) * Math.PI / 180;
        double lon1 = id2node.get(from).getY();
        double lon2 = id2node.get(to).getY();
        double deltaLon = (lon2 - lon1) * Math.PI / 180;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1) *
                Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = R * c;

        return d;
    }

    public NodeMap getId2node() {
        return id2node;
    }

    public double getMinlon() {
        return minlon;
    }

    public double getMinlat() {
        return minlat;
    }

    public double getMaxlon() {
        return maxlon;
    }

    public double getMaxlat() {
        return maxlat;
    }

    public KDTree getRoadTree() {
        return roadTree;
    }

    public KDTree getKdTree() {
        return kdTree;
    }

}
