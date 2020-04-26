package src;
import java.io.*;
import java.util.Scanner;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

class Partition {
    // Size of max heap for Karmark-Karp
    static int size = 0;
    static int max_iter = 25000;

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

            // Read numbers from input file
            try {
                FileInputStream f = new FileInputStream(inputFile);
                Scanner s = new Scanner(f);
                int i = 0;
                while (s.hasNextLine()) {
                    numbers[i] = Long.parseLong(s.nextLine());
                    i++;
                }
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            int n = 5;
            numbers = new long[n];

            for (int i = 0; i < n; i++) {
                numbers[i] = ThreadLocalRandom.current().nextLong(1,10);
                System.out.println(numbers[i]);
            }

            System.out.println();
        }

        // Karmarkar-Karp
        if (algorithm == 0) {
            System.out.println(KarmarkarKarp(numbers));
        }
        // Repeated Random
        if (algorithm == 1) {
            System.out.println(RepeatedRandom(numbers));
        }
        // Hill Climbing
        if (algorithm == 2) {
            System.out.println(HillClimbing(numbers));
        }
        // Simulated Annealing
        if (algorithm == 3) {
            System.out.println();
        }
        // Prepartitioned Repeated Random
        if (algorithm == 11) {
            System.out.println();
        }
        // Prepartitioned Hill Climbing
        if (algorithm == 12) {
            System.out.println();
        }
        // Prepartitioned Simulated Annealing
        if (algorithm == 13) {
            System.out.println();
        }
    }

    /**
     * Implementation of Karmarkar-Karp heuristic for the number partition 
     * problem
     * 
     * @param A : list of positive 64-bit integers
     * @return An upper bound for the residue
     */
    public static long KarmarkarKarp(long[] A) {
        int n = A.length;
        long[] maxHeap = new long[n];
        size = 0;
        for (long num : A) {
            insert(maxHeap, num);
        }

        long max1;
        long max2;
        for (int i = 0; i < n-2; i++) {
            max1 = extractMax(maxHeap);
            max2 = extractMax(maxHeap);
            insert(maxHeap, max1 - max2);
        }

        max1 = extractMax(maxHeap);
        max2 = extractMax(maxHeap);
        return max1 - max2;
    }

    /**
     * Repeatedly generate random solutions and return the one with the 
     * smallest residue
     * 
     * @param A : list of numbers
     * @return r : residue of solution
     */
    public static long RepeatedRandom(long[] A) {
        int n = A.length;
        int[] S = randSolution(n);
        long r = residue(A, S, n);

        for (int i = 0; i < max_iter; i++) {
            int[] S2 = randSolution(n);
            long r2 = residue(A, S2, n);
            if (r2 < r) {
                S = S2;
                r = r2;
            }
        }

        for (int s : S) {
            System.out.print(Integer.toString(s) + " ");
        }
        System.out.println();

        return r;
    }

    /**
     * Start with a random solution and try to improve it through moves to 
     * better neighbors
     * 
     * @param A
     * @return r : residue of solution
     */
    public static long HillClimbing(long[] A) {
        int n = A.length;
        int[] S = randSolution(n);
        long r = residue(A, S, n);

        for (int i = 0; i < max_iter; i++) {
            int[] S2 = randNeighbor(S, n);
            long r2 = residue(A, S2, n);
            if (r2 < r) {
                S = S2;
                r = r2;
            }
        }

        for (int s : S) {
            System.out.print(Integer.toString(s) + " ");
        }
        System.out.println();

        return r;
    }

    /**
     * Given a sequence of positive integers A and solution S returns residue
     * 
     * @param A
     * @param S
     * @param n : length of A
     * @return r : residue
     */
    public static long residue(long[] A, int[] S, int n) {
        int r = 0;
        for (int i = 0; i < n; i++) {
            r += A[i] * S[i];
        }
        return Math.abs(r);
    }

    /**
     * Returns a random solution of length n
     * 
     * @param n
     * @return S : solution
     */
    public static int[] randSolution(int n) {
        Random rand = new Random();
        int[] S = new int[n];
        for (int i = 0; i < n; i++) {
            int s = rand.nextInt(2);
            if (s == 0) {
                S[i] = -1;
            }
            else {
                S[i] = 1;
            }
        }
        return S;
    }

    /**
     * Returns a random neigbor of a given solution S of length n
     * 
     * @param S
     * @param n
     * @return N : neighbor solution to S
     */
    public static int[] randNeighbor(int[] S, int n) {
        Random rand = new Random();
        int[] N = S.clone();
        // Choose random element to change
        int i = rand.nextInt(n);
        N[i] = -1 * S[i];
        // With probability 1/2 change another distinct random element
        int p = rand.nextInt(2);
        if (p == 1) {
            int j = i;
            while (j == i) {
                j = rand.nextInt(n);
            }
            N[j] = -1 * S[j];
        }
        return N;
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
     * @param size : size of H
     * @param x
     */
    public static void insert(long[] H, long x) {
        H[size] = x;
        int n = size;
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
        size++;
    }

    /**
     * Remove largest element from max heap and rearrange tree to maintain heap 
     * structure
     * 
     * @param H
     * @param size : size of H
     * @return max
     */
    public static long extractMax(long[] H) {
        long max = H[0];
        H[0] = H[size-1];
        H[size-1] = 0;
        size--;
        // Rearrange tree to be a max heap
        maxHeapify(H, 0); 
        return max;
    }

    /**
     * Rearranges the tree rooted at root to be a max heap
     * 
     * @param H
     * @param size : size of H
     * @param root
     */
    public static void maxHeapify(long[] H, int root) {
        int l = leftChild(root);
        int r = rightChild(root);
        int largest;

        // Check if left child is largest
        if (l < size && H[l] > H[root]) {
            largest = l;
        }
        else {
            largest = root;
        }
        // Check if right child is largest
        if (r < size && H[r] > H[largest]) {
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
     * @param size : size of H
     */
    public static void printHeap(long[] H) {
        int height = (int) Math.floor(Math.log(size) / Math.log(2)) + 1;
        int i = 0;
        int j = 0;
        int elements = 1;
        while (i < height) {
            elements += 2 * i;
            while (j < elements) {
                if (j < size) {
                    System.out.print(Long.toString(H[j]) + " ");
                }
                j++;
            }
            System.out.println();
            i++;
        }
    }
}
