package Functions;

import kNN.Point;

import java.util.Random;

/**
 * Created by Zell on 12/4/15.
 */
public class Selection {

    public Point[] select(Point[] G, int k){
        Point[] selections = new Point[k];
        for(int i = 0; i < k; i++){
            selections[i] = quickselect(G, i+1);
        }
        return selections;
    }

    public Point quickselect(Point[] G, int k) {
        return quickselect(G, 0, G.length - 1, k - 1);
    }

    private Point quickselect(Point[] G, int first, int last, int k) {
        if (first <= last) {
            int pivot = partition(G, first, last);
            if (pivot == k) {
                return G[k];
            }
            if (pivot > k) {
                return quickselect(G, first, pivot - 1, k);
            }
            return quickselect(G, pivot + 1, last, k);
        }
        return new Point();
    }

    private int partition(Point[] G, int first, int last) {
        int pivot = first + new Random().nextInt(last - first + 1);
        swap(G, last, pivot);
        for (int i = first; i < last; i++) {
            if (G[i].getDistance() < G[last].getDistance()) {
                swap(G, i, first);
                first++;
            }
        }
        swap(G, first, last);
        return first;
    }

    private void swap(Point[] G, int x, int y) {
        Point tmp = G[x];
        G[x] = G[y];
        G[y] = tmp;
    }
}
