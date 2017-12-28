package com.barrybecker4.common.util;

import java.io.InputStream;
import java.util.Scanner;

/**
 * The <tt>WeightedQuickUnionUF</tt> class represents a union-find data structure.
 * It supports the <em>union</em> and <em>find</em> operations, along with
 * methods for determining whether two objects are in the same component
 * and the total number of components.
 * <p>
 * This implementation uses weighted quick union by size (without path compression).
 * Initializing a data structure with <em>N</em> objects takes linear time.
 * Afterwards, <em>union</em>, <em>find</em>, and <em>connected</em> take
 * logarithmic time (in the worst case) and <em>count</em> takes constant
 * time.
 * </p>
 * <p>
 * 6/19 added ability to delete connections
 * </p>
 * For additional documentation, see <a href="http://algs4.cs.princeton.edu/15uf">Section 1.5</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * @author Barry Becker - minor modifications
 */
public class UnionFind {
    // parent[i] = parent of i
    private int[] parent;
    // size[i] = number of objects in subtree rooted at i
    private int[] size;
    // number of components
    private int count;

    /**
     * Initializes an empty union-find data structure with N isolated components 0 through N-1.
     *
     * @param n the number of objects
     * @throws IllegalArgumentException if N &lt; 0
     */
    public UnionFind(int n) {
        count = n;
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public static UnionFind create(InputStream in) {
        Scanner stdIn = new Scanner(in);
        int n = stdIn.nextInt();
        UnionFind uf = new UnionFind(n);
        while (stdIn.hasNext()) {
            int p = stdIn.nextInt();
            int q = stdIn.nextInt();
            if (!uf.connected(p, q)) {
                uf.union(p, q);
            }
        }
        return uf;
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between 1 and N)
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the component identifier for the component containing site <tt>p</tt>.
     *
     * @param p the integer representing one site
     * @return the component identifier for the component containing site <tt>p</tt>
     * @throws IndexOutOfBoundsException unless 0 &lt;= p &lt; N
     */
    public int find(int p) {
        validate(p);
        while (p != parent[p]) {
            p = parent[p];
        }
        return p;
    }

    /** validate that p is a valid index */
    private void validate(int p) {
        int N = parent.length;
        if (p < 0 || p >= N) {
            throw new IndexOutOfBoundsException("index " + p + " is not between 0 and " + N);
        }
    }

    /**
     * Are the two sites <tt>p</tt> and <tt>q</tt> in the same component?
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @return <tt>true</tt> if the two sites <tt>p</tt> and <tt>q</tt>
     * are in the same component, and <tt>false</tt> otherwise
     * @throws IndexOutOfBoundsException unless both 0 &lt;= p &lt; N and 0 &lt;= q &lt; N
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Merges the component containing site<tt>p</tt> with the component
     * containing site <tt>q</tt>.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws IndexOutOfBoundsException unless both 0 &lt;= p &lt; N and 0 &lt;= q &lt; N
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;

        // make smaller root point to larger one
        if (size[rootP] < size[rootQ]) {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        } else {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        }
        count--;
    }

    /**
     * Reads in a sequence of pairs of integers (between 0 and N-1) from standard input,
     * where each integer represents some object;
     * if the objects are in different components, merge the two components
     * and print the pair to standard output.
     */
    public static void main(String[] args) {
        UnionFind uf = UnionFind.create(System.in);
        System.out.println(uf.getCount() + " components");
    }

}

