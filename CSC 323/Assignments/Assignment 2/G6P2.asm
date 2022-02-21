TITLE Average Grade Accumulator (G6P1.asm)				; OF DOOM

; CSC 323 - Group 6 - Assignment 2: RPN Stack Calculator
; Author: Robert Krency, kre1188@calu.edu
; Author: Anthony Stepich, ste4864@calu.edu
; Author: Austin Pringle, pri2679@calu.edu
; Author: Camden Kovach, kov2428@calu.edu

; This program simulates an RPN calculator using a stack with limited
; size of 8 elements.

INCLUDE Irvine32.inc

.data

; Output Strings
msg_Details				BYTE	"Welcome to the RPN Calculator.", 0
msg_empty				BYTE    "The stack is empty.",0
msg_Quit				BYTE	"Exiting...", 0

; Stack
rpnStack				BYTE	8 dup(0)
stackSize				DWORD	0
stackSizeMax			DWORD	8

; Input Buffer
buffer					BYTE	21 DUP(0)
byteCount				dword	?
index					dword	0

; Flags



.code
main PROC
	mov edx, OFFSET msg_Details				; Display welcome message
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







; Parse the input command
ParseInput:
	
	call ParseIntger32		; Parse input string as integer
	call PushStack
	call DisplayStack
	jmp GetInput

	
	
						;cmp stackSize,0
						;call Empty

						; For alphabetic inputs, need to 'or' the input with 20h to ensure capitalization
					
						; after each operation, display the top of the stack
					; call GetInput again


; Display the stack
; Command: 'V'
DisplayStack:
	; Recommended implementation:
	;	Loop stackSize times:
	;		DisplayTopElement
	;		RollStackDown
	
	mov ecx,0

	
	DisplayLoop:
	call DisplayTopElement
	call RollStackDown
	inc ecx
	cmp ecx,StackSize
	jl DisplayLoop
	ret

; Display the top element of the stack
DisplayTopElement:				
	mov eax, [rpnStack]				;move the top element to eax
	call writeint 					;prints out the top stack 
	call crlf
	ret


; Exchange the top two elements on the stack
; Command: 'X'
ExchangeTopTwoElements:
	mov esi,0
	mov [rpnStack+esi]


; Negate the top element of the stack
; Command: 'N'
NegateTopElement:
	
	mov esi,o
	mov eax,[rpnStack+esi]
	neg eax
	mov [rnpStack+esi],eax
	ret 
	
	


; Roll the stack Up, only used positions
; Command: 'U'
RollStackUp:
	nop


; Roll the stack Down, only used positions
; Command: 'D'
RollStackDown:

cmp StackSize,1
je RollStackDownEnd

mov esi,0
mov 

RollStackDownEnd:
ret


; Clear the stack
; Command: 'C'
ClearStack:
	
	
	nop
	


; Perform the input operation
; Possible operations: +, -, /, *
PerformOperation:
	; pop, move eax to ebx
	; pop,
	; operate, operand 1 in eax, operand 2 in ebx, result in eax
	; push
	; display top element
	nop


; Pop the top element of the stack, placing it in eax
PopStack:
	; move top element to eax
	; roll down the stack
	nop


; Push eax onto the top of the stack
	PushStack:
	; roll up the stack
	call IncreaseSize
	mov esi,0   			; move eax to top of stack
	mov [rpnStack+esi],eax
	ret
	
	IncreaseSize:
	cmp StackSize,8
	je IncreaseSizeEnd
	inc stackSize
	
	IncreaseSizeEnd:
	ret
	
	Empty:
	call crlf
	mov edx, OFFSET msg_empty 			;print if the stack is empty 
	call WritetSring
	call crlf


; Quit the program
; Command: 'Q'
Quit:
	call Crlf

	mov edx, OFFSET msg_Quit					; Print the quit message
	call WriteString

	call Crlf

	exit

main ENDP
END main
