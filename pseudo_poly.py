import numpy as np
from math import floor
from random import randint
from collections import Counter

def numPartition(A):
    """ 
    Dynamic programming pseudo-polynomial time algorithm for the number 
    partition problem. Runs in O(nb) where n is the length of A and b is the 
    sum of A's elements.
    """
    A = sorted(A)
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

    optimal = b/2
    best_r = 2*b
    best_i = 0
    # Find minimum residual
    for i in range(b+1):
        for j in range(n+1):
            if D[i,j] != -1:
                r = abs(i-optimal)
                if r < best_r:
                    best_r = r
                    best_i = i

    # Follow path to form A1
    A1 = list(reversed(unroll(D,best_i,A)))
    A2 = list((Counter(A) - Counter(A1)).elements())

    print("A1:", A1)
    print("Sum(A1):", best_i)
    print()
    print("A2:", A2)
    print("Sum(A2):", b - best_i)
    print()
    print("Residual:", best_r)

def unroll(R,i,A):
    S = []
    j = 0
    while R[i,j] == -1:
        j += 1
    while R[i,j] > 0:
        if i != R[i,j]:
            S.append(A[j-1])
        i = R[i,j]
        j = j-1
    S.append(A[j-1])
    return S

A = [randint(0,9) for i in range(8)]
print("A:", A)
print()
numPartition(A)
