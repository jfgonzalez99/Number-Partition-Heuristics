package src;
import java.io.*;
import java.util.Scanner;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;

class Partition {
    // Size of max heap for Karmark-Karp
    static int sizeHeap = 0;

    public static void main(String[] args) {
        int testcode = Integer.parseInt(args[0]);
        int algorithm = Integer.parseInt(args[1]);
        String inputFile = args[2];

        // List of numbers to be partitioned
        long[] numbers;

        // Binary max heap for Karmar-Karp
        long[] maxHeap;

        if (testcode == 0) {
            numbers = new long[100];
            maxHeap = new long[100];

            // Read numbers from input file
            try {
                FileInputStream f = new FileInputStream(inputFile);
                Scanner s = new Scanner(f);
                int i = 0;
                while (s.hasNextLine()) {
                    numbers[i] = Long.parseLong(s.nextLine());
                    insert(maxHeap, numbers[i]);
                    i++;
                }
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (testcode == 1) {
            int n = 10;
            numbers = new long[n];
            maxHeap = new long[n];

            for (int i = 0; i < n; i++) {
                numbers[i] = ThreadLocalRandom.current().nextLong(1,100);
                System.out.println(numbers[i]);
                insert(maxHeap, numbers[i]);
            }
            System.out.println();
            printHeap(maxHeap);

            System.out.println();
            extractMax(maxHeap);

            System.out.println();
            printHeap(maxHeap);
        }
    }

    /**
     * @param n
     * @return Index of the parent of index n in a binary heap
     */
    public static int parent(int n) {
        return (int) Math.floor(n / 2);
    }

    /**
     * @param p
     * @return Index of the left child of parent with index p in a binary heap
     */
    public static int leftChild(int p) {
        return 2 * p;
    }

    /**
     * @param p
     * @return Index of the right child of parent with index p in a binary heap
     */
    public static int rightChild(int p) {
        return 2 * p + 1;
    }

    /**
     * Inserts x into the binary max heap H
     * 
     * @param H
     * @param x
     */
    public static void insert(long[] H, long x) {
        H[sizeHeap] = x;
        int n = sizeHeap;
        int p = parent(n);
        while (n != 0 && H[p] < H[n]) {
            // Swap parent with current
            long parentVal = H[p];
            long currentVal = H[n];
            H[p] = currentVal;
            H[n] = parentVal;

            n = p;
            p = parent(n);
        }
        sizeHeap++;
    }

    /**
     * Remove largest element from max heap and rearrange tree to maintain heap 
     * structure
     * 
     * @param H
     * @return
     */
    public static long extractMax(long[] H) {
        long max = H[0];
        H[0] = H[sizeHeap-1];
        H[sizeHeap-1] = 0;
        sizeHeap--;
        // Rearrange tree to be a max heap
        maxHeapify(H, 0); 
        return max;
    }

    /**
     * Rearranges the tree rooted at root to be a max heap
     * 
     * @param H
     * @param root
     */
    public static void maxHeapify(long[] H, int root) {
        int l = leftChild(root);
        int r = rightChild(root);
        int largest;

        // Check if left child is largest
        if (l < sizeHeap && H[l] > H[root]) {
            largest = l;
        }
        else {
            largest = root;
        }
        // Check if right child is largest
        if (r < sizeHeap && H[r] > H[largest]) {
            largest = r;
        }
        
        // Swap and max heapify if root is not largest
        if (largest != root) {
            long rootVal = H[root];
            long largestVal = H[largest];
            H[root] = largestVal;
            H[largest] = rootVal;
            maxHeapify(H, largest);
        }
    }

    /**
     * Prints binary heap H
     * 
     * @param H
     */
    public static void printHeap(long[] H) {
        int height = (int) Math.floor(Math.log(sizeHeap) / Math.log(2)) + 1;
        int i = 0;
        int j = 0;
        int elements = 1;
        while (i < height) {
            elements += 2 * i;
            while (j < elements) {
                if (j < sizeHeap) {
                    System.out.print(Long.toString(H[j]) + " ");
                }
                j++;
            }
            System.out.println();
            i++;
        }
    }
}
