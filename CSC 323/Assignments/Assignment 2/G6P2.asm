TITLE RPN Stack Calculator (G6P2.asm)				; OF DOOM(ED GRADES)

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
msg_Empty				BYTE    "The stack is empty.", 0
msg_Invalid				BYTE	"Invalid input.", 0
msg_TopOfStack			BYTE	"Top of Stack: ",0
msg_Quit				BYTE	"Exiting...", 0

; Stack
rpnStack				DWORD	8 DUP(0)
stackSize				DWORD	0
stackSizeMax			DWORD	8

; Input Buffer
buffer					BYTE	21 DUP(0)
byteCount				dword	?
validBuffer				BYTE	21 DUP(0)
validByteCount			dword	0
inputNum				dword	0
NULL					equ		0
TAB						equ		9

; Flags
flag_WhiteSpace			dword	0
flag_Number				dword	0
flag_Command			dword	0
flag_MinusSign			dword	0
flag_IsNumber			dword	0
flag_IsNegNumber		dword	0


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
	mov esi, OFFSET buffer
	mov edi, OFFSET validBuffer
	mov validByteCount, 0
	mov flag_IsNumber, 0
	mov flag_IsNegNumber, 0
	mov ebx, 0

	dec esi


GetNextChar:
	cmp ebx, byteCount
	je ProcessValidBuffer
	inc ebx

	inc esi
	mov flag_WhiteSpace, 0
	mov flag_Command, 0
	mov flag_Number, 0
	mov flag_MinusSign, 0

	mov eax, 0
	mov al, [esi]

	call CheckNumber					; Check if the current character is a number
	cmp flag_Number, 1
	je CaseNumber

	cmp flag_IsNumber, 1				; If we have a number already, and we aren't adding another, parse
	je ProcessValidBuffer

	call CheckWhiteSpace				; Check if the current character is a whitespace
	cmp flag_WhiteSpace, 1
	je CaseWhiteSpace

	call CheckMinusSign					; Check if the current character is a minus sign
	cmp flag_MinusSign, 1
	je CaseMinusSign

	call CheckCommand					; Check if the current character is a valid command
	cmp flag_Command, 1
	je CaseCommand

	jmp CaseInvalid						; If it's not a valid input character
	

; Checks if the character in register 'al' is a whitespace or null
CheckWhiteSpace:
	cmp al, ' '
	je SetWhiteSpaceFlag

	cmp al, TAB
	je SetWhiteSpaceFlag

	ret

SetWhiteSpaceFlag:
	mov flag_WhiteSpace, 1
	ret


; Check if the character in register 'al' is a valid command input
CheckCommand:

	cmp al, '+'
	je SetCommandFlag

	cmp al, '*'
	je SetCommandFlag

	cmp al, '/'
	je SetCommandFlag

	and al, 223							; Force letters to upper case
	
	cmp al, 'V'
	je SetCommandFlag

	cmp al, 'Q'
	je SetCommandFlag

	cmp al, 'X'
	je SetCommandFlag

	cmp al, 'U'
	je SetCommandFlag

	cmp al, 'D'
	je SetCommandFlag

	cmp al, 'N'
	je SetCommandFlag

	cmp al, 'C'
	je SetCommandFlag

	ret

SetCommandFlag:
	mov flag_Command, 1
	ret


; Check if the character in register 'al' is a number
CheckNumber:
	cmp al, '0'							; 48 = 0011 0000, '0' in ASCII
	jl NotNumber

	cmp al, '9'							; 57 = 0011 1001, '9' in ASCII
	jg NotNumber

	mov flag_Number, 1

NotNumber:
	ret


; Check if the character in register 'al' is a minus-sign
CheckMinusSign:
	cmp al, '-'
	je SetMinusFlag
	ret

SetMinusFlag:
	mov flag_MinusSign, 1
	ret


; If the current input char is a whitespace, increment the buffer index and start over
CaseWhiteSpace:
	cmp flag_IsNumber, 1
	je ProcessValidBuffer

	cmp flag_IsNegNumber, 1
	je CaseCommand

	jmp GetNextChar


; The character in the register 'al' is for a valid command
CaseCommand:

	cmp flag_IsNegNumber, 1
	jne CommandV
	call Subtraction
CommandV:
	cmp al, 'V'
	jne CommandQ
	call DisplayStack
CommandQ:
	cmp al, 'Q'
	jne CommandX
	call Quit
CommandX:
	cmp al, 'X'
	jne CommandU
	call ExchangeTopTwoElements
CommandU:
	cmp al, 'U'
	jne CommandD
	call RollStackUp
CommandD:
	cmp al, 'D'
	jne CommandN
	call RollStackDown
CommandN:
	cmp al, 'N'
	jne CommandC
	call NegateTopElement
CommandC:
	cmp al, 'C'
	jne CommandAdd
	call ClearStack
CommandAdd:
	cmp al, '+'
	jne CommandMul
	call Addition
CommandMul:
	cmp al, '*'
	jne CommandDiv
	call Multiplication
CommandDiv:
	cmp al, '/'
	jne CommandDone
	call Division
CommandDone:
	call DisplayTopElement
	jmp GetInput


; The current character in the register 'al' is a number
CaseNumber:
	mov [edi], al
	inc edi
	inc validByteCount
	mov flag_IsNumber, 1
	jmp GetNextChar


; The current character in the register 'al' is a minus sign
CaseMinusSign:
	mov flag_IsNegNumber, 1

	cmp validByteCount, 0
	je SetNegNum

	cmp flag_IsNumber, 1
	je ProcessValidBuffer

DoSub:
	call Subtraction
	call DisplayTopElement
	jmp GetInput

SetNegNum:
	mov [edi], al
	inc edi
	inc validByteCount
	cmp byteCount, 1
	je DoSub
	jmp GetNextChar

; The current character in the register 'al' is invalid
CaseInvalid:
	cmp flag_IsNegNumber, 1
	jne Invalid
	call Subtraction
	jmp GetInput
	
Invalid:
	mov edx, OFFSET msg_Invalid
	call WriteString
	call Crlf
	jmp GetInput


; Process the valid input buffer
ProcessValidBuffer:
	mov eax, NULL
	mov [edi], eax
	mov edx, OFFSET validBuffer
	mov ecx, validByteCount
	call ParseInteger32
	mov inputNum, eax
	call PushStack
	call DisplayTopElement
	jmp GetInput
	

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
	cmp stackSize, 0
	je DisplayEmptyStack
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
	cmp stackSize, 0
	je DisplayEmptyStack

	call Crlf
	mov edx, OFFSET msg_TopOfStack
	call WriteString

	mov esi, OFFSET rpnStack
	mov eax, [esi]					;move the top element to eax
	call WriteInt 					;prints out the top stack 
	call Crlf
	call Crlf
	ret

DisplayEmptyStack:
	call Empty
	ret


; Exchange the top two elements on the stack
; Command: 'X'
ExchangeTopTwoElements:
	mov esi, OFFSET rpnStack
	mov eax, [esi]
	mov ebx, [esi+4]
	mov [esi+4], eax
	mov [esi], ebx
	ret


; Negate the top element of the stack
; Command: 'N'
NegateTopElement:
	call PopStack
	neg eax
	mov inputNum, eax
	call PushStack
	ret 
	

; Roll the stack Up, only used positions
; Command: 'U'
RollStackUp:
	cmp stackSize, 1
	jle RollStackUpEnd					; do nothing if stack is size <= 1

	mov esi, OFFSET rpnStack			; move stack start into register
	mov eax, [esi]						; store the top of the stack
	mov ecx, 0

RSULoop:
	mov ebx, [esi+4]
	mov [esi], ebx
	add esi, 4
	inc ecx
	cmp ecx, stackSize
	jl RSULoop
	mov [esi-4], eax

RollStackUpEnd:
	ret

; Roll the stack Down, only used positions
; Command: 'D'
RollStackDown:
	cmp stackSize, 1
	jle RollStackDownEnd
	
	mov edx, stackSize
	sub edx, 1

RSDLoop:
	call RollStackUp
	dec edx
	cmp edx, 0
	jg RSDLoop


	
RollStackDownEnd:
	ret


; Clear the stack
; Command: 'C'
ClearStack:
	mov stackSize, 0
	ret
	
; Addition of two operands, eax + ebx
Addition:
	Call PopStack
	mov ebx,eax
	Call PopStack
	add eax,ebx
	mov inputNum, eax
	Call PushStack
	ret

; Subtraction of two operands, eax - ebx
Subtraction:
	Call PopStack
	mov ebx,eax
	Call PopStack 
	sub eax,ebx
	mov inputNum, eax
	Call PushStack
	ret

; Multiplication of two operations, eax * ebx
Multiplication:
	Call PopStack
	mov ebx,eax
	call PopStack
	mul ebx
	mov inputNum, eax
	Call PushStack
	ret

; Division of two operands, eax / ebx
Division:
	Call PopStack
	mov ebx,eax
	Call PopStack
	cdq
	div ebx
	mov inputNum, eax
	Call PushStack
	ret


; Pop the top element of the stack, placing it in eax
PopStack:
	; move top element to eax
	; roll down the stack
	mov esi, OFFSET rpnStack
	mov eax, [esi]
	call RollStackUp
	call DecreaseSize
	ret


; Decrease the size of the stack, with a minimum of 0
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
	

; Incrase the size of the stack, with a max size of 8
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