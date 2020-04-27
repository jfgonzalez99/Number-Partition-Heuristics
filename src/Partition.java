package src;
import java.io.*;
import java.util.Scanner;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

class Partition {
    // Size of max heap for Karmark-Karp
    static int size;
    static int max_iter = 25000;

    public static void main(String[] args) {
        int testcode = Integer.parseInt(args[0]);
        int algorithm = Integer.parseInt(args[1]);
        String inputFile = args[2];

        // List of numbers to be partitioned
        long[] numbers;

        // Read a list of 100 64-bit positive integers from a file and run the 
        // given algorithm
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
            
            runAlgorithm(algorithm, numbers);
        }

        // Generate 100 random lists of 100 64-bit positive integers and run 
        // each algorithm on each list. Print the results and the time it took 
        // each algorithm to run.
        if (testcode == 1) {

        }

        // Create a list of n random 64-bit positive integers and run the given 
        // algorithm
        else {
            int n = 5;
            numbers = new long[n];

            // Generate n random numbers
            for (int i = 0; i < n; i++) {
                numbers[i] = ThreadLocalRandom.current().nextLong(1, 10);
                System.out.print(Long.toString(numbers[i]) + " ");
            }
            System.out.println();

            runAlgorithm(algorithm, numbers);
        }
    }

    /**
     * Runs algorithm alg on a sequence of positive 64-bit integers A
     * 
     * @param alg
     * @param A
     */
    public static void runAlgorithm(int alg, long[] A) {
        // Karmarkar-Karp
        if (alg == 0) {
            System.out.println(KarmarkarKarp(A));
        }
        // Repeated Random
        if (alg == 1) {
            System.out.println(RepeatedRandom(A));
        }
        // Hill Climbing
        if (alg == 2) {
            System.out.println(HillClimbing(A));
        }
        // Simulated Annealing
        if (alg == 3) {
            System.out.println(SimulatedAnnealing(A));
        }
        // Prepartitioned Repeated Random
        if (alg == 11) {
            System.out.println(RepeatedRandom(prepartition(A)));
        }
        // Prepartitioned Hill Climbing
        if (alg == 12) {
            System.out.println(HillClimbing(prepartition(A)));
        }
        // Prepartitioned Simulated Annealing
        if (alg == 13) {
            System.out.println(SimulatedAnnealing(prepartition(A)));
        }
    }

    /*-----------------------------------------------------*/
    /*------ Heuristics for number partition problem ------*/
    /*-----------------------------------------------------*/

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
     * @param A : list of positive 64-bit integers
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
     * @param A : list of positive 64-bit integers
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
     * Start with a random solution and try to improve it through moves to 
     * neigbors that are not necessarily better. Higher probability of makeing 
     * worse moves at the beginning.
     * 
     * @param A : list of positive 64-bit integers
     * @return r3 : residue of solution
     */
    public static long SimulatedAnnealing(long[] A) {
        int n = A.length;
        int[] S = randSolution(n);
        long r = residue(A, S, n);
        int[] S3 = S;
        long r3 = r;
        Random rand = new Random();

        for (int i = 0; i < max_iter; i++) {
            int[] S2 = randNeighbor(S, n);
            long r2 = residue(A, S2, n);
            if (r2 < r) {
                S = S2;
                r = r2;
            }
            else if (rand.nextDouble() < probability(r, r2, i)) {
                S = S2;
                r = r2;
            }
            if (r < r3) {
                S3 = S;
                r3 = r;
            }
        }

        for (int s : S3) {
            System.out.print(Integer.toString(s) + " ");
        }
        System.out.println();

        return r3;
    }

    /*-----------------------------------------------------*/
    /*---------- Helper functions for heuristics ----------*/
    /*-----------------------------------------------------*/

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
     * Returns the "temperature" at iteration i
     * 
     * @param i
     * @return temperature
     */
    public static double temp(int i) {
        int r = (int) Math.floor(i/300);
        long powTenTen = 10000000000L;
        return powTenTen * Math.pow(0.8, r);
    }

    /**
     * Returns the probability of swiching from solution with residue of r1 to 
     * neigbor with residue of r2 at iteration i where r2 >= r1
     * 
     * @param r1
     * @param r2
     * @param i
     * @return probability
     */
    public static double probability(long r1, long r2, int i) {
        return Math.exp(-(r2 - r1) / temp(i));
    }

    /*-----------------------------------------------------*/
    /*----- Functions for generating random solutions -----*/
    /*-----------------------------------------------------*/

    /**
     * Given a sequence A of n positive integers returns a new sequence A2 
     * where each element has been randomly prepartitioned into one of n 
     * possible groups
     * 
     * @param A
     * @return A2
     */
    public static long[] prepartition(long[] A) {
        Random rand = new Random();
        int p_i;
        int n = A.length;
        long[] A2 = new long[n];

        // Randomly partition each number in A to a random group p_i
        for (int i = 0; i < n; i++) {
            p_i = rand.nextInt(n);
            A2[p_i] += A[i];
        }

        for (long a : A2) {
            System.out.print(Long.toString(a) + " ");
        }
        System.out.println();

        return A2;
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

    /*-------------------------------------------------------*/
    /*---- Functions for binary max heap data structure -----*/
    /*-------------------------------------------------------*/

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
