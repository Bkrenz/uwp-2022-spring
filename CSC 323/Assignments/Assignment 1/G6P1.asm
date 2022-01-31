TITLE Average Grade Accumulator (G6P1.asm)				; OF DOOM

; CSC 323 - Group 6 - Assignment 1: Average
; Author: Robert Krency, kre1188@calu.edu
; Author: Anthony Stepich, ste4864@calu.edu
; Author: Austin Pringle, pri2679@calu.edu
; Author: Camden Kovach, kov2428@calu.edu

; This program reads in integer grades from the user and compiles
; an average, total count, and total sum statistics of the input.

INCLUDE Irvine32.inc

.data

; Output Strings
msg_Details				BYTE	"Welcome to the grade accumulator.", 0
msg_QuitDetails			BYTE	"To quit, enter a null value as first input each round.", 0
msg_GetInput			BYTE	"Input an integer, 0-100 inclusive: ", 0
msg_Count				BYTE	"Count: ", 0
msg_Total				BYTE	"Total: ", 0
msg_Average				BYTE	"Average: ", 0
msg_Remainder			BYTE    "Remainder: ", 0
msg_Quit				BYTE	"Exiting...", 0
msg_err_NumRange		BYTE	"Error: Number not within range 0-100, inclusive.", 0

; Grade Stats
count					dword	0
total					dword	0
average					dword	0
remainder				dword   0

; Input Buffer
buffer					BYTE	21 DUP(0)
byteCount				dword	?

; Flags
flag_FirstInput			dword	1			; Flag for if the first user input this round


.code
main PROC
	mov edx, OFFSET msg_Details				; Display welcome message
	call WriteString
	call Crlf
	
	mov edx, OFFSET msg_QuitDetails			; Display how to quit
	call WriteString
	call Crlf


GetInput:									
	; Get user input

	mov edx, OFFSET msg_GetInput			; Prompt user for input
	call WriteString 
	
	mov edx, OFFSET buffer					; Read input from user as string
	mov ecx, SIZEOF buffer
	call ReadString
	mov byteCount, eax

	mov edx, OFFSET buffer					; Parse input string as integer
	mov ecx, byteCount


ValidateInput:
	; Validate that the input is an integer between 0-100
	; If not:
	;		(1) If input is null and first input, quit
	;		(2) If input is integer out of range, display error message
	;		(3) If input is not an integer and not null, display error message
	; If either of condition (2) or (3) is met, display the stats after the error message

	cmp eax, 0									; Check for null input
	JNE ValidateInteger

	mov ebx, flag_FirstInput					; Check if this was the first input
	cmp ebx, 1								
	JE Quit										; If so, quit.

	Call PrintStats								; Null Input, but not first input


ValidateInteger:
	; Validates that the input is an integer between 0 and 100, inclusive

	call ParseInteger32							; Try to parse the input number
	
	JO PrintStats								; Checks for overflow error, indicating user input was not a valid integer
	
	cmp eax, 100								; Check if number is less than 100
	JG PrintErrorNotInRange
	cmp eax, 0									; Check if number is greater than 0
	JL PrintErrorNotInRange

	call AddNumbers								; Valid input


PrintErrorNotInRange:
	; Error for the input number not being between 0 and 100, inclusive

	mov edx, OFFSET msg_err_NumRange
	call WriteString
	call Crlf
	
	call PrintStats								; Prints error message for input not in range 


AddNumbers:
	; Adds the validated user input to the stats

	mov ebx, total								; Add the new number to the total
	add ebx, eax
	mov total, ebx

	mov ebx, count								; Increment the counter
	add ebx, 1
	mov count, ebx

	mov eax,total								; Calculate the Average using Integer division, storing the quotient and remainder
	cdq
	mov ebx,count								
	div ebx
	mov average,eax
	mov remainder,edx

	mov flag_FirstInput, 0						; Successful input, set first input flag to zero
	call GetInput								; Loop getting the input

	
PrintStats:
	; Display the stats

	call Crlf

	mov edx, OFFSET msg_Total					; Print out the Total
	call WriteString
	mov eax, total
	call WriteInt
	call Crlf

	mov edx, OFFSET msg_Count					; Print out the Count
	call WriteString
	mov eax, count
	call WriteInt
	call Crlf

	mov edx, OFFSET msg_Average					; Print out the Average
	call WriteString
	mov eax, average
	call WriteInt
	call Crlf
	
	mov edx, OFFSET msg_Remainder				; Prints out the remainder
	call Writestring
	mov eax, remainder
	call WriteInt
	call Crlf

	call Crlf
	call Crlf
	
	mov flag_FirstInput, 1						; New round of inputs
	call GetInput 


Quit:
	call Crlf

	mov edx, OFFSET msg_Quit					; Print the quit message
	call WriteString

	call Crlf

	exit

main ENDP
END main
