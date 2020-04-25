import numpy as np
from random import randint

def numPartition(A):
    """ 
    Dynamic programming pseudo-polynomial time algorithm for the number 
    partition problem. Runs in O(nb) where n is the length of A and b is the 
    sum of A's elements.

    Input:
    A : list of non-negative integers
    
    Returns:
    S : list of signs such that S[i] = 1 implies A[i] is in group 1 and 
        S[i] = -1 implies A[i] is in group 2
    """
    n = len(A)
    b = sum(A)
    D = np.array([[-1] * (n+1)] * (b+1))

    # First row is all zeros
    for i in range(n + 1):
        D[0,i] = 0
    
    # Fill table
    for i in range(1, b+1):
        for j in range(1, n+1):
            # If first j elements can add up to i then first j+1 elements can too since sorted
            # Point to the previous element in row
            if D[i, j-1] != -1:
                D[i,j] = i
            # Point to i - A[j-1]
            elif D[i - A[j-1], j-1] != -1:
                D[i,j] = i - A[j-1]

    min_res = b
    best_i = 0
    best_j = 0

    # Find minimum residue
    for i in range(b+1):
        for j in range(n+1):
            if D[i,j] != -1:
                r = abs(i-(b-i))
                if r < min_res:
                    min_res = r
                    best_i = i
                    best_j = j

    # Follow path to form A1
    S = unroll(D,best_i,best_j,n)
    A1 = []
    A2 = []
    for i in range(n):
        if S[i] == 1:
            A1.append(A[i])
        else:
            A2.append(A[i])
    
    # Print results
    print("S:", S)
    print("Residue:", min_res)
    print()
    print("A1:", A1)
    print("Sum(A1):", best_i)
    print()
    print("A2:", A2)
    print("Sum(A2):", b - best_i)

def unroll(R,i,j,n):
    S = [-1]*n
    S[j-1] = 1
    while R[i,j] > 0:
        i = R[i,j]
        j = j-1
        if i != R[i,j]:
            S[j-1] = 1
    return S

A = [randint(0,9) for i in range(9)]
print("A:", A)
print()
numPartition(A)
