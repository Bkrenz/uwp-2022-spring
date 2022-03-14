/**
 * @file a5.c
 * @author Robert Krency
 * @brief Assignment 5, CSC 455 Chen
 * @version 0.1
 * @date 2022-02-25
 * 
 */

#include <stdlib.h>
#include <stdio.h>


int* fun(int* a) {
    *a += 10;
    return a;
}

int main() {

    // Assignment 5 Code
    int a, b;
    a = 10;
    b = a + *fun(&a);
    printf("With the function call on the right,");
    printf(" b is: %d\n", b);

    a = 10;
    b = *fun(&a) + a;
    printf("With the function call on the left,");
    printf(" b is: %d\n", b);


    // Test short circuiting
    int x = 0;
    if (x == 0 && x++==1);      // With nothing to short circuit, the x gets incremented
    printf("X is %d\n",x);      // Expected value with no short circuiting: 1
    if (x == 3 && x++==2);      // The left side evaluates to false, so it shorts circuits and doesn't execute the increment
    printf("X is %d\n", x);     // Expected value with short circuiting: 1

    // Exit
    return 0;
}

