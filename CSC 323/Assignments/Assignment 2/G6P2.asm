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
rpnStack				BYTE	8 dup(0)
stackSize				DWORD	0
stackSizeMax			DWORD	8

; Input Buffer
buffer					BYTE	21 DUP(0)
byteCount				dword	?

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
	
	mov displayIndex, 0
	mov esi, OFFSET rpnStack
	
DisplayLoop:
	mov edi, displayIndex
	mov eax, [esi+edi]
	call WriteString
	call Crlf
	inc edi
	mov displayIndex, edi
	cmp edi, StackSize
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
	jle RollStackUpEnd
	mov esi, OFFSET rpnStack
	mov rollIndex, 0
	mov edi, stackSize
	dec edi
	mov ebx, [esi+edi]

RSULoop:
	dec edi
	mov eax, [esi+edi]
	mov [esi+edi+1], eax
	cmp edi, 0
	jg RSULoop
	mov [esi], ebx

RollStackUpEnd:
	ret

; Roll the stack Down, only used positions
; Command: 'D'
RollStackDown:
	cmp stackSize,1
	jle RollStackDownEnd
	mov esi, OFFSET rpnStack
	mov ebx, [esi]
	mov ecx, 0

RSDLoop:
	inc esi
	mov eax, [esi]
	mov [esi-1], eax
	inc ecx
	cmp ecx, stackSize
	jl RSDLoop
	mov [esi], ebx

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
	mov esi, OFFSET rpnStack
	mov eax, [esi]
	call RollStackDown
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
	call RollStackUp
	mov esi, OFFSET rpnStack
	mov [esi],eax
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
