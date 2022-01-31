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
msg_Details				BYTE	"Welcome to the grade accumulator.", 13, 10, 0
msg_QuitDetails			BYTE	"To quit, enter two invalid inputs in a row.", 13, 10, 0
msg_GetInput			BYTE	"Input an integer, 0-100 inclusive: ", 0
msg_Count				BYTE	13, 10, "Count: ", 0
msg_Total				BYTE	13, 10, "Total: ", 0
msg_Average				BYTE	13, 10, "Average: ", 0
msg_Remainder				BYTE    13, 10, "Remainder:",0
msg_err_NumRange		BYTE	13, 10, "Error: Number not within range 0-100, inclusive.", 13, 10, 0

count					dword	0
total					dword	0
average					dword	0
remainder				dword   0

buffer					BYTE	21 DUP(0)
byteCount				dword	?


flag_Error	dword	0		; Tracks if an error has occurred previously



.code
main PROC
	mov edx, OFFSET msg_Details			; Display welcome message
	call WriteString
	
	mov edx, OFFSET msg_QuitDetails		; Display how to quit
	call WriteString

	
GetInput:
	mov edx, OFFSET msg_GetInput		; Prompt user for input
	call WriteString
	
	cmp 
	
	mov edx, OFFSET buffer				; Read input from user as string
	mov ecx, SIZEOF buffer
	call ReadString
	mov byteCount, eax

	mov edx, OFFSET buffer				; Parse input string as integer
	mov ecx, byteCount
	call ParseInteger32
	
	cmp ecx,1
	JO Get_input						;checks for overflow error
	JNO L1
	
	L1:cmp ecx, 100
	
	JLE L2
	L2:cmp ecx,0					;checks for valid number
	JGE L3

	

	L3:mov ebx, total						; Add the new number to the total
	add ebx, eax
	mov total, ebx

	mov ebx, count						; Increment the counter
	add ebx, 1
	mov count, ebx

	call GetInput						; Loop getting the input


CalcAverage:
	mov eax,total 
	mov edx,count					;produces an average and remainder and assigns them both 
	div edx
	mov average,eax
	mov remainder,edx
	
	

	
PrintStats:
	mov edx, OFFSET msg_Total				; Print out the Total
	call WriteString
	mov eax, total
	call WriteInt

	mov edx, OFFSET msg_Count				; Print out the Count
	call WriteString
	mov eax, count
	call WriteInt

	mov edx, OFFSET msg_Average				; Print out the Average
	call WriteString
	mov eax, average
	call WriteInt
	
	mov edx, OFFSET msg_Remainder				;prints out the remainder
	call Writestring
	mov eax, remainder
	call WriteInt
	
	call GetInput 

PrintErrorNotInRange:
	mov edx, OFFSET msg_err_NumRange
	call WriteString
	call PrintStats					;Prints error message for input not in range 



	exit
main ENDP
END main
