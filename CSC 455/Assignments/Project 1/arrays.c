/**
 * @file arrays.c
 * @author your name Robert Krency
 * @brief Benchmarks using pointer arithmetic and array indexing
 * @version 0.1
 * @date 2022-02-23
 * 
 * @copyright Copyright (c) 2022
 * 
 */

#define SIZE 10000
#define gettimeofday mingw_gettimeofday
#include <stdio.h>
#include <time.h>
#include <stdlib.h>

int main () {

    // Initialize
    int *array;
    array = (int*) malloc(sizeof(int)*SIZE*SIZE);

    struct timeval stop, start;
    int *ptr = array;

    // Loop through with indices
    gettimeofday(&start, NULL);
    for (int i = 0; i < SIZE; i++)
        for (int j = 0; j < SIZE; j++)
            array[j*SIZE + i] = 0;
    gettimeofday(&stop, NULL);
    printf("Indexing time: %ld", (stop.tv_sec - start.tv_sec) * 1000000 + stop.tv_usec - start.tv_usec);


    // Loop through with pointers
    gettimeofday(&start, NULL);
    for (int i =0; i < SIZE; i++, ptr++)
        *ptr = 1;
    gettimeofday(&stop, NULL);
    printf("\nPointers time: %ld", (stop.tv_sec - start.tv_sec) * 1000000 + stop.tv_usec - start.tv_usec);


    // Free memory
    free(array);

    // Exit
    return 0;
}