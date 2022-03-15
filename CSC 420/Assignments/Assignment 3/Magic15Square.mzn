% Author: Robert Krency
% Date: 3/14/2022
% CSC 420 - Assignment 3

include "alldifferent.mzn";

var 1..9: A;
var 1..9: B;
var 1..9: C;
var 1..9: D;
var 1..9: E;
var 1..9: F;
var 1..9: G;
var 1..9: H;
var 1..9: I;

constraint alldifferent([A, B, C, D, E, F, G, H, I]);
constraint A + B + C = 15;
constraint D + E + F = 15;
constraint G + H + I = 15;
constraint A + D + G = 15;
constraint B + E + H = 15;
constraint C + F + I = 15;
constraint A + E + I = 15;
constraint C + E + G = 15;

solve satisfy;

output ["\(A) \(B) \(C) \n",
        "\(D) \(E) \(F) \n",
        "\(G) \(H) \(I) "];