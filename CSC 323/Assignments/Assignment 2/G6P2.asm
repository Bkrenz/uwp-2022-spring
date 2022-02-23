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
msg_GetInput			BYTE	"Enter input: ", 0
msg_empty				BYTE    "The stack is empty.",0
msg_Quit				BYTE	"Exiting...", 0

; Stack
rpnStack				DWORD	8 DUP(0)
stackSize				DWORD	0
stackSizeMax			DWORD	8

; Input Buffer
buffer					BYTE	21 DUP(0)
byteCount				dword	?
inputNum				dword	0

; Indices
displayIndex			dword	0
rollIndex				dword	0


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
	
	call ParseInteger32		; Parse input string as integer
	mov inputNum, eax
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
	
	mov esi, OFFSET rpnStack
	mov ecx, 0
	
DisplayLoop:
	mov eax, [esi]
	call WriteInt
	call Crlf
	add esi, 4
	inc ecx
	cmp ecx, stackSize
	jl DisplayLoop
	ret
	

; Display the top element of the stack
DisplayTopElement:
	mov esi, OFFSET rpnStack
	mov eax, [esi]					;move the top element to eax
	call WriteInt 					;prints out the top stack 
	call Crlf
	ret


; Exchange the top two elements on the stack
; Command: 'X'
ExchangeTopTwoElements:
	nop


; Negate the top element of the stack
; Command: 'N'
NegateTopElement:
	call PopStack
	neg eax
	call PushStack
	ret 
	
	


; Roll the stack Up, only used positions
; Command: 'U'
RollStackUp:
	cmp stackSize, 1
	jle RollStackUpEnd					; do nothing if stack is size <= 1
	
	mov esi, OFFSET rpnStack			; find start of stack
	mov ebx, [esi]						; grab the first item
	mov ecx, 2							; setup a counter

RSULoop:
	mov eax, [esi+1]
	mov [esi], eax
	inc esi
	inc ecx
	cmp ecx, stackSize
	jl RSULoop
	mov [esi], ebx

RollStackUpEnd:
	ret

; Roll the stack Down, only used positions
; Command: 'D'
RollStackDown:
	cmp stackSize, 1
	jle RollStackDownEnd
	mov esi, OFFSET rpnStack
	mov ecx, 7
	mov edx, [esi+ecx*4]

RSDLoop:
	mov eax, [esi+ecx*4-4]
	mov [esi+ecx*4], eax
	dec ecx
	cmp ecx, 0
	jge RSDLoop
	mov [esi], edx
	
RollStackDownEnd:
	ret


; Clear the stack
; Command: 'C'
ClearStack:
	mov stackSize, 0
	ret
	


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
	mov esi, OFFSET rpnStack
	mov eax, [esi]
	call RollStackUp
	call DecreaseSize
	ret

DecreaseSize:
	cmp stackSize, 0
	je DecreaseSizeEnd
	dec stackSize
DecreaseSizeEnd:
	ret


; Push eax onto the top of the stack
PushStack:
	; roll up the stack
	call IncreaseSize
	call RollStackDown
	mov esi, OFFSET rpnStack
	mov eax, inputNum
	mov [esi], eax
	ret
	
; 
IncreaseSize:
	cmp StackSize, 8
	je IncreaseSizeEnd
	inc stackSize	
IncreaseSizeEnd:
	ret
	

; Print an error message if the stack is empty
Empty:
	call crlf
	mov edx, OFFSET msg_empty 			;print if the stack is empty 
	call WriteString
	call crlf
	ret


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
