package bfst22.vector;

import java.util.ArrayList;
import java.util.Comparator;

public class NodeMap extends ArrayList<OSMNode> {
    private boolean sorted;

    public boolean add(OSMNode node) {
        sorted = false;
        return super.add(node);
    }

    public OSMNode get(long ref) {
        if (!sorted) {
            sort(Comparator.comparing(node -> node.getID()));
            sorted = true;
        }
        int lo = 0;
        int hi = size();
        while (hi - lo > 1) {
            int mi = (lo + hi) / 2;
            if (get(mi).getID() <= ref) {
                lo = mi;
            } else {
                hi = mi;
            }
        }
        OSMNode node = get(lo);
        return node.getID() == ref ? node : null;
    }
}
