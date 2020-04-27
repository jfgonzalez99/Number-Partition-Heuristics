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
            for (int i = 0; i < 100; i++) {
                long[] A = randLong(100, 1000000000000L);

            }
        }

        // Create a list of n random 64-bit positive integers and run the given 
        // algorithm
        if (testcode == 2) {
            // int n = 15;
            // int b = 9;
            // numbers = randLong(n, b);
            numbers = new long[] {9,8,7,7,7,6,5,5,4,3,2,1,0,0,0};
            printLongArr(numbers);
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
        int n = A.length;
        // Karmarkar-Karp
        if (alg == 0) {
            System.out.println(KarmarkarKarp(A,n));
        }
        // Repeated Random
        if (alg == 1) {
            System.out.println(RepeatedRandom(A,n));
        }
        // Hill Climbing
        if (alg == 2) {
            System.out.println(HillClimbing(A,n));
        }
        // Simulated Annealing
        if (alg == 3) {
            System.out.println(SimulatedAnnealing(A,n));
        }
        // Prepartitioned Repeated Random
        if (alg == 11) {
            System.out.println(prePartRepeatedRandom(A,n));
        }
        // Prepartitioned Hill Climbing
        if (alg == 12) {
            System.out.println(prePartHillClimbing(A,n));
        }
        // Prepartitioned Simulated Annealing
        if (alg == 13) {
            System.out.println(prePartSimulatedAnnealing(A,n));
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
     * @param n : length of A
     * @return An upper bound for the residue
     */
    public static long KarmarkarKarp(long[] A, int n) {
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
     * @param n : length of A
     * @return r : residue of solution
     */
    public static long RepeatedRandom(long[] A, int n) {
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

        // printIntArr(S);
        return r;
    }

     /**
     * Repeatedly generate random prepartitions and return the one with the 
     * smallest Karmarkar-Karp residue
     * 
     * @param A : list of positive 64-bit integers
     * @param n : length of A
     * @return r2 : Karmarkar-Karp residue of solution
     */
    public static long prePartRepeatedRandom(long[] A, int n) {
        long[] A2 = regroup(A, prepartition(n), n);
        long r2 = KarmarkarKarp(A2, n);
        printLongArr(A);

        for (int i = 0; i < max_iter; i++) {
            long[] A3 = regroup(A, prepartition(n), n);
            long r3 = KarmarkarKarp(A3, n);
            if (r3 < r2) {
                A2 = A3;
                r2 = r3;
            }
        }

        // printIntArr(A2);
        return r2;
    }

    /**
     * Start with a random solution and try to improve it through moves to 
     * better neighbors
     * 
     * @param A : list of positive 64-bit integers
     * @param n : length of A
     * @return r : residue of solution
     */
    public static long HillClimbing(long[] A, int n) {
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

        // printIntArr(S);
        return r;
    }

    /**
     * Start with a random prepartition and try to improve it through moves to 
     * better neighbors
     * 
     * @param A : list of positive 64-bit integers
     * @param n : length of A
     * @return r : Karmarkar-Karp residue of solution
     */
    public static long prePartHillClimbing(long[] A, int n) {
        int[] P = prepartition(n);
        long[] A2 = regroup(A, P, n);
        long r2 = KarmarkarKarp(A2, n);

        for (int i = 0; i < max_iter; i++) {
            int[] P2 = randPartNeighbor(P, n);
            long[] A3 = regroup(A, P2, n);
            long r3 = KarmarkarKarp(A3, n);
            if (r3 < r2) {
                P = P2;
                A2 = A3;
                r2 = r3;
            }
        }

        // printIntArr(A2);
        return r2;
    }

    /**
     * Start with a random solution and try to improve it through moves to 
     * neigbors that are not necessarily better. Higher probability of makeing 
     * worse moves at the beginning.
     * 
     * @param A : list of positive 64-bit integers
     * @param n : length of A
     * @return r3 : residue of solution
     */
    public static long SimulatedAnnealing(long[] A, int n) {
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

        // printIntArr(S3);
        return r3;
    }

    /**
     * Start with a random prepartition and try to improve it through moves to 
     * neigbors that are not necessarily better. Higher probability of makeing 
     * worse moves at the beginning.
     * 
     * @param A : list of positive 64-bit integers
     * @param n : length of A
     * @return r4 : Karmarkar-Karp residue of solution
     */
    public static long prePartSimulatedAnnealing(long[] A, int n) {
        int[] P = prepartition(n);
        long[] A2 = regroup(A, P, n);
        long r2 = KarmarkarKarp(A2, n);

        int[] P3 = P;
        long[] A4 = A2;
        long r4 = r2;

        Random rand = new Random();
        for (int i = 0; i < max_iter; i++) {
            int[] P2 = randPartNeighbor(P, n);
            long[] A3 = regroup(A, P2, n);
            long r3 = KarmarkarKarp(A3, n);
            if (r3 < r2) {
                P = P2;
                A2 = A3;
                r2 = r3;
            }
            else if (rand.nextDouble() < probability(r2, r3, i)) {
                P = P2;
                A2 = A3;
                r2 = r3;
            }
            if (r2 < r4) {
                P3 = P;
                A4 = A2;
                r4 = r2;
            }
        }

        // printIntArr(A4);
        return r4;
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

    /**
     * Prints array A with 64-bit integers on one line
     * 
     * @param A
     */
    public static void printLongArr(long[] A) {
        for (long a : A) {
            System.out.print(Long.toString(a) + " ");
        }
        System.out.println();
    }

    /**
     * Prints array A with 32-bit integers on one line
     * 
     * @param A
     */
    public static void printIntArr(int[] A) {
        for (int a : A) {
            System.out.print(Integer.toString(a) + " ");
        }
        System.out.println();
    }

    /*-----------------------------------------------------*/
    /*----- Functions for generating random solutions -----*/
    /*-----------------------------------------------------*/

    /**
     * Generates a random partitioning P for n elements
     * 
     * @param n
     * @return P
     */
    public static int[] prepartition(int n) {
        Random rand = new Random();
        int[] P = new int[n];
        for (int i = 0; i < n; i++) {
            P[i] = rand.nextInt(n);
        }
        return P;
    }

    /**
     * Given a sequence A and partitioning P groups together all elements in A 
     * that share the same partition group
     * 
     * @param A
     * @param P
     * @return A2
     */
    public static long[] regroup(long[] A, int[] P, int n) {
        long[] A2 = new long[n];
        for (int i = 0; i < n; i++) {
            A2[P[i]] += A[i];
        }
        return A2;
    }

    /**
     * Returns a random neigbor of a given partition P of length n
     * 
     * @param P
     * @param n
     * @return N : neighbor partition to P
     */
    public static int[] randPartNeighbor(int[] P, int n) {
        Random rand = new Random();
        int[] N = P.clone();
        // Choose random element to change
        int i = rand.nextInt(n);
        int p = P[i];
        while (p == P[i]) {
            p = rand.nextInt(n);
        }
        N[i] = p;
        return N;
    }

    /**
     * Generate a series A of n random 64-bit numbers in [1,b]
     * 
     * @param n
     * @param b
     * @return A
     */
    public static long[] randLong(int n, long b) {
        long[] A = new long[n];
        for (int i = 0; i < n; i++) {
            A[i] = ThreadLocalRandom.current().nextLong(1, b+1);
        }
        return A;
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
