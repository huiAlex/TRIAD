package edu.nd.dronology.core.collisionavoidance;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class CollisionAvoidanceUtil {

    private static class DronePairIterator implements Iterator<DronePair> {

        Iterator<int[]> indices;
        List<DroneSnapshot> snapshots;

        DronePairIterator(List<DroneSnapshot> snapshots, Iterator<int[]> indices) {
            this.snapshots = snapshots;
            this.indices = indices;
        }

		@Override
		public boolean hasNext() {
			return indices.hasNext();
		}

		@Override
		public DronePair next() {
            int[] nextIndices = indices.next();
            int indexA = nextIndices[0];
            int indexB = nextIndices[1];
            return new DronePair(snapshots.get(indexA), snapshots.get(indexB));
		}

    }

    private static Iterator<DronePair> pairs(List<DroneSnapshot> snapshots) {
        if (snapshots.size() >= 2) {
            Iterator<int[]> indices = CombinatoricsUtils.combinationsIterator(snapshots.size(), 2);
            return new DronePairIterator(snapshots, indices);
        } else {
            return Collections.emptyIterator();
        }
    }

    public static Iterable<DronePair> findPairs(final List<DroneSnapshot> snapshots) {
        return new Iterable<DronePair>(){
        
            @Override
            public Iterator<DronePair> iterator() {
                return pairs(snapshots);
            }
            
        };
    }

}