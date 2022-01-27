TITLE Average (Average.asm)

; CSC 323 - Group 6 - Assignment 1: Average
; Author: Robert Krency, kre1188@calu.edu
; Author: Anthony Stepich, <email>
; Author: Austin Pringle, <email>
; Author: Camden Kovach, <email>

; This program reads in integer grades from the user and compiles
; an average, total count, and total sum statistics of the input.

INCLUDE Irvine32.inc

.data
msg_Details		BYTE	"Welcome to the grade accumulator.", 0
msg_GetInput	BYTE	"Input an integer, 0-100 inclusive: ", 0
msg_Count		BYTE	"Count: ", 0
msg_Total		BYTE	"Total: ", 0
msg_Average		BYTE	"Average: ", 0

count			dword	0
total			dword	0
average			dword	0

.code
main PROC

	mov edx, OFFSET msg_Details
	call WriteString

GetInput:
	mov edx, OFFSET msg_GetInput
	call WriteString
	



	exit
main ENDP
END main