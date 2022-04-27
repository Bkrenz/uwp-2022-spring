TITLE Network Simulator (G6P4.asm)				; OF DOOM(ED GRADES)

; CSC 323 - Group 6 - Assignment 4: Network Simulator
; Author: Robert Krency, kre1188@calu.edu
; Author: Anthony Stepich, ste4864@calu.edu
; Author: Camden Kovach, kov2428@calu.edu

; This program simulates a Network Topology


INCLUDE Irvine32.inc

.data

; ASCII Equivalents
ascii_Tab	equ 9
ascii_Zero	equ 48

; Output Strings
msg_Details				byte	"Welcome to the Operating System Simulator.", 0
msg_GetInput			byte	">> ", 0
msg_Quit				byte	"Exiting...", 0

msg_InputFileName		byte	"Enter input file name: ", 0
msg_OutputFileName		byte	"Enter output file name: ", 0
msg_SystemTime			byte	"[0000] ", 0
msg_FileOpen			byte	"Opening file.", 0
msg_FileOpenSuccess		byte	"File open successful.", 0
msg_FileOpenError		byte	"File open error.", 0
msg_FileClose			byte	"Closing file.", 0
msg_FileCloseSuccess	byte	"File close successful.", 0
msg_FileCloseError		byte	"File close error.", 0

msg_CurrentNode			byte	"Processing Node:  ", 0
NodePositionOffset		equ		sizeof msg_CurrentNode-2

msg_TransmitConnection	byte	ascii_Tab, "Transmitting Packet to:  ", 0
msgTransmitOffset equ	sizeof	msg_TransmitConnection-2

msg_ReceiveConnection	byte	ascii_Tab, "Receiving Packet from:  ", 0
msgReceiveOffset equ	sizeof	msg_ReceiveConnection-2

msg_SourceNode			byte	"Source Node:  ", 0
msg_DestinationNode		byte	"Destination Node:  ", 0
msg_TimeToLive			byte	"Time to Live:  ", 0
msg_EchoOn				byte	"Echo On: True", 0, 0
msg_EchoOff				byte	"Echo On: False", 0, 0

msg_EnqueuePacket		byte	"Adding packet to transmit queue of Node  .", 0
msgEnqueueOffset		equ		sizeof msg_EnqueuePacket-3

; Packet Constants
Packet_Size					equ 	6

; Network Packet Offsets
Packet_Destination			equ		0
Packet_Sender				equ		1
Packet_Origin				equ		2
Packet_TimeToLive			equ		3
Packet_TimeReceived			equ		4


; Node A Buffers
A_XMT_B			label	byte
B_RCV_A			byte 	Packet_Size dup(0)

A_XMT_E			label	byte
E_RCV_A			byte	Packet_Size dup(0)

; Node B Buffers
B_XMT_A			label	byte
A_RCV_B			byte	Packet_Size dup(0)

B_XMT_C			label	byte
C_RCV_B			byte	Packet_Size dup(0)

B_XMT_F			label	byte
F_RCV_B			byte	Packet_Size dup(0)

; Node C Buffers
C_XMT_B			label	byte
B_RCV_C			byte	Packet_Size dup(0)

C_XMT_D			label	byte
D_RCV_C			byte	Packet_Size dup(0)

C_XMT_E			label	byte
E_RCV_C			byte	Packet_Size dup(0)

; Node D Buffers
D_XMT_C			label	byte
C_RCV_D			byte	Packet_Size dup(0)

D_XMT_F			label	byte
F_RCV_D			byte	Packet_Size dup(0)

; Node E Buffers
E_XMT_A			label	byte
A_RCV_E			byte	Packet_Size dup(0)

E_XMT_C			label	byte
C_RCV_E			byte	Packet_Size dup(0)

E_XMT_F			label	byte
F_RCV_E			byte	Packet_Size dup(0)

; Node F Buffers
F_XMT_B			label	byte
B_RCV_F			byte	Packet_Size dup(0)

F_XMT_D			label	byte
D_RCV_F			byte	Packet_Size dup(0)

F_XMT_E			label	byte
E_RCV_F			byte	Packet_Size dup(0)


; Network Queue Definition
Queue_Size		equ		10

; Network Node Queues
NodeA_Queue		byte	Queue_Size*Packet_Size dup(0)
NodeB_Queue		byte	Queue_Size*Packet_Size dup(0)
NodeC_Queue		byte	Queue_Size*Packet_Size dup(0)
NodeD_Queue		byte	Queue_Size*Packet_Size dup(0)
NodeE_Queue		byte	Queue_Size*Packet_Size dup(0)
NodeF_Queue		byte	Queue_Size*Packet_Size dup(0)


; Network Node Definition
Node_FixedSize		equ		14
Node_ConnectionSize	equ		12

; Network Node Offsets
Node_Name			equ		0
Node_Connections	equ		1
Node_QueueAddress	equ		2
Node_InPointer		equ		6
Node_OutPointer		equ		10

; Network Connection Offsets
Node_Connection		equ		0
Node_Connection_XMT	equ		4
Node_Connection_RCV	equ		8


; Network
Network		label	byte

; Node A Definition
Node_A		byte	'A'				; Name
			byte	2				; Connections
			dword	NodeA_Queue		; Transmit Queue Address
			dword	NodeA_Queue		; Transmit Queue In Pointer
			dword	NodeA_Queue		; Transmit Queue Out Pointer
			; Connection to Node B
			dword 	Node_B
			dword 	A_XMT_B
			dword 	A_RCV_B
			; Connection to Node E
			dword 	Node_E
			dword 	A_XMT_E
			dword 	A_RCV_E


; Node B Definition
Node_B		byte	'B'				; Name
			byte	3				; Connections
			dword	NodeB_Queue		; Transmit Queue Address
			dword	NodeB_Queue		; Transmit Queue In Pointer
			dword	NodeB_Queue		; Transmit Queue Out Pointer
			; Connection to Node A
			dword 	Node_A
			dword 	B_XMT_A
			dword 	B_RCV_A
			; Connection to Node C
			dword 	Node_C
			dword 	B_XMT_C
			dword 	B_RCV_C
			; Connection to Node F
			dword 	Node_F
			dword 	B_XMT_F
			dword 	B_RCV_F


; Node C Definition
Node_C		byte	'C'				; Name
			byte	3				; Connections
			dword	NodeC_Queue		; Transmit Queue Address
			dword	NodeC_Queue		; Transmit Queue In Pointer
			dword	NodeC_Queue		; Transmit Queue Out Pointer
			; Connection to Node B
			dword 	Node_B
			dword 	C_XMT_B
			dword 	C_RCV_B
			; Connection to Node D
			dword 	Node_D
			dword 	C_XMT_D
			dword 	C_RCV_D
			; Connection to Node E
			dword 	Node_E
			dword 	C_XMT_E
			dword 	C_RCV_E


; Node D Definition
Node_D		byte	'D'				; Name
			byte	2				; Connections
			dword	NodeD_Queue		; Transmit Queue Address
			dword	NodeD_Queue		; Transmit Queue In Pointer
			dword	NodeD_Queue		; Transmit Queue Out Pointer
			; Connection to Node C
			dword 	Node_C
			dword 	D_XMT_C
			dword 	D_RCV_C
			; Connection to Node F
			dword 	Node_F
			dword 	D_XMT_F
			dword 	D_RCV_F


; Node E Definition
Node_E		byte	'E'				; Name
			byte	3				; Connections
			dword	NodeE_Queue		; Transmit Queue Address
			dword	NodeE_Queue		; Transmit Queue In Pointer
			dword	NodeE_Queue		; Transmit Queue Out Pointer
			; Connection to Node A
			dword 	Node_A
			dword 	E_XMT_A
			dword 	E_RCV_A
			; Connection to Node C
			dword 	Node_C
			dword 	E_XMT_C
			dword 	E_RCV_C
			; Connection to Node F
			dword 	Node_F
			dword 	E_XMT_F
			dword 	E_RCV_F


; Node F Definition
Node_F		byte	'F'				; Name
			byte	3				; Connections
			dword	NodeF_Queue		; Transmit Queue Address
			dword	NodeF_Queue		; Transmit Queue In Pointer
			dword	NodeF_Queue		; Transmit Queue Out Pointer
			; Connection to Node B
			dword 	Node_B
			dword 	F_XMT_B
			dword 	F_RCV_B
			; Connection to Node D
			dword 	Node_D
			dword 	F_XMT_D
			dword 	F_RCV_D
			; Connection to Node E
			dword 	Node_E
			dword 	F_XMT_E
			dword 	F_RCV_E

EndOfNodes	dword	EndOfNodes

; Temp Variables for operation
currentPacket 	byte 	Packet_Size dup(0)
currentNode		dword	0
outputMessage	byte	500 dup(0)
emptyString		byte	500 dup(0)
system_time		dword	0
intString		byte	4 dup(ascii_Zero), 0
flag_Echo		byte	0

; File IO Vars
bufferSize			equ		81
fileBufferSize		equ		100
NULL				equ		0
inputFileHandle		dword	?
outputFileHandle	dword	?
fileName			byte	bufferSize dup(0)
fileBuffer			byte	fileBufferSize	dup(0)
					byte	0
bytesRead			dword	0
fileCrlf			byte	13, 10

; Operating Stats
NewPackets			word	0
GeneratedPackets	word	0
TotalPackets		word	0
ActivePackets		word	1
ReceivedPackets		word	0
TotalHops			word	0
TotalTime			word	0
AverageHops			word	0
MaxHops				byte 	6


.code
main PROC

	; Setup the initial packet
	mov eax, OFFSET currentPacket
	mov byte ptr Packet_Destination[eax], 'D'
	mov byte ptr Packet_Origin[eax], 'A'
	mov byte ptr Packet_Sender[eax], 'A'
	mov byte ptr Packet_TimeToLive[eax], 6
	mov word ptr Packet_TimeReceived[eax], 0

	; Open the input and output files
	call OpenFiles

	; Copy it into the transmit queue of the origin
	mov edi, OFFSET Node_A
	call PushIntoQueue

	; Print out the initial information
	call PrintInfo

	; Start the node at the initial node of the network
	mov edi, offset Network
	mov currentNode, edi
	jmp MainLoop


; Print out the initial packet information and program settings
PrintInfo:
	mov eax, OFFSET currentPacket

	; Print out the Source Node
	mov edx, OFFSET msg_SourceNode
	mov ecx, SIZEOF msg_SourceNode
	add edx, ecx
	sub edx, 2
	mov bl, byte ptr Packet_Origin[eax]
	mov byte ptr [edx], bl
	mov edx, OFFSET msg_SourceNode
	mov esi, edx
	mov edi, OFFSET outputMessage
	REP MOVSB
	call WriteMessageToOutput

	; Print out the Destination Node
	mov edx, OFFSET msg_DestinationNode
	mov ecx, SIZEOF msg_DestinationNode
	add edx, ecx
	sub edx, 2
	mov bl, byte ptr Packet_Destination[eax]
	mov byte ptr [edx], bl
	mov edx, OFFSET msg_DestinationNode
	mov esi, edx
	mov edi, OFFSET outputMessage
	REP MOVSB
	call WriteMessageToOutput

	; Print out the time to live
	mov edx, OFFSET msg_TimeToLive
	mov ecx, SIZEOF msg_TimeToLive
	add edx, ecx
	sub edx, 2
	mov bl, byte ptr Packet_TimeToLive[eax]
	add bl, 48
	mov byte ptr [edx], bl
	mov edx, OFFSET msg_TimeToLive
	mov esi, edx
	mov edi, OFFSET outputMessage
	REP MOVSB
	call WriteMessageToOutput

	; Print out the echo information
	mov esi, OFFSET msg_EchoOn
	mov ecx, SIZEOF msg_EchoOn
	cmp flag_Echo, 0
	jne PrintEchoOn
	mov esi, OFFSET msg_EchoOff
	mov esi, OFFSET msg_EchoOff
PrintEchoOn:
	mov edi, OFFSET outputMessage
	REP MOVSB
	call writeMessageToOutput

	; Return
	ret 

; Open the input and output files
OpenFiles:
	push eax
	push ecx
	push edx

	; Get the input file name
    mov edx, OFFSET msg_InputFileName
    mov ecx, SIZEOF msg_InputFileName
    call WriteString
    mov edx, OFFSET fileName
    mov ecx, SIZEOF fileName
    call ReadString

    ; Open the Input File
    mov edx, OFFSET fileName
    call OpenInputFile
    mov inputFileHandle, eax
    cmp eax, INVALID_HANDLE_VALUE
    je ErrorOpenFile

    ; Get the output file name
    mov edx, OFFSET msg_OutputFileName
    mov ecx, SIZEOF msg_OutputFileName
    call WriteString
    mov edx, OFFSET fileName
    mov ecx, SIZEOF fileName
    call ReadString

    ; Open the Output File
    mov edx, OFFSET fileName
    call CreateOutputFile
    mov outputFileHandle, eax
    cmp eax, INVALID_HANDLE_VALUE
    je ErrorOpenFile

	; Successful file opening
	pop edx
	pop ecx
	pop eax
	ret

; There was an error opening the file
ErrorOpenFile:
	; Copy Error Message
    mov esi, OFFSET msg_FileOpenError
    mov edi, OFFSET outputMessage
	mov ecx, SIZEOF msg_FileOpenError
	cld
	REP MOVSB
	; Write out the error message
    call WriteMessageToOutput
    call Crlf
    jmp Quit

; The main loop of the program
MainLoop:
	; Process the transmit procedure for each Node
	mov edx, OFFSET Network
	mov currentNode, edx
	call TransmitLoop

	; Increment the System Time
	inc system_time

	; Process the receive procedure for each Node
	mov edx, OFFSET Network
	mov currentNode, edx
	call ReceiveLoop

	; If there are still packets alive, continue looping
	cmp ActivePackets, 0
	jg MainLoop
	jmp Quit

; Process a connection
ConnectionLoop:
	; Point to the Connection to process
	mov eax, Node_ConnectionSize			; Get the size of a connection
	mul bl									; Multiply by connection index
	mov esi, Node_FixedSize[edi+eax]		; Get the connection address

	; Move node name into message
	mov edx, offset msg_TransmitConnection	; Get the message address
	mov ecx, sizeof msg_TransmitConnection	; Get the message size
	mov al, Node_Name[esi]					; Get the name of the node
	mov msgTransmitOffset[edx], al	; Move the name into the message
	Call WriteString						; Print the name of the connection
	Call Crlf

	; Process the next connection
	inc ebx
	cmp bl, Node_Connections[edi]			; Check if we've processed all connections
	jl ConnectionLoop


; Step to the Next Node
StepToNextNode:
	jmp Quit


; Process each node's transmit queues
TransmitLoop:
	; Print out the name of the node being processed
	mov edx, currentNode
	mov esi, OFFSET msg_CurrentNode
	mov bl, byte ptr Node_Name[edx]
	mov byte ptr NodePositionOffset[esi], bl
	mov edi, OFFSET outputMessage
	mov ecx, SIZEOF msg_CurrentNode
	REP MOVSB
	call WriteMessageToOutput

	; Pop a packet from the queue
	call PopFromQueue
	jc TransmitLoopNextNode

	; For each connection of this node, copy the current packet into the transmit buffer
	mov ecx, 0
TransmitConnectionLoop:
	mov edx, CurrentNode
	cmp cl, byte ptr Node_Connections[edx]
	je TransmitLoopNextNode
	
	mov edx, currentNode
	; Get the current connection
	mov eax, ecx
	push ecx
	mov ebx, Node_ConnectionSize
	mul ebx
	mov edx, currentNode
	add eax, Node_FixedSize
	add eax, edx
	mov ebx, [eax]

	; Get the connection name
	mov esi, OFFSET msg_TransmitConnection
	mov al, byte ptr Node_Name[ebx]
	mov byte ptr msgTransmitOffset[esi], al
	mov edi, OFFSET outputMessage
	mov ecx, SIZEOF msg_TransmitConnection
	CLD
	REP MOVSB
	call WriteMessageToOutput

	; Update the packet info
	mov esi, OFFSET currentPacket
	mov ebx, 0
	mov bl, byte ptr Packet_Sender[esi]
	mov byte ptr Packet_Sender[esi], al
	dec Packet_TimeToLive[esi]

	; If the packet is still alive
	cmp Packet_TimeToLive[esi], 0
	je TransmitNextConnection

	; If Echo is turned off, don't send to the previous sender
	cmp flag_Echo, 0
	jne TransmitEchoOn
	cmp bl, al
	je TransmitNextConnection
TransmitEchoOn:

	; Copy to the transmit buffer
	add eax, 4
	mov edi, eax
	mov esi, OFFSET currentPacket
	mov ecx, Packet_Size
	CLD
	REP MOVSB

	; Update Packet Counts
	inc Packets

TransmitNextConnection:
	; Increment the connection count and loop again
	pop ecx
	inc ecx
	jmp TransmitConnectionLoop

TransmitLoopNextNode:
	push ecx
	mov ecx, 0
	mov edx, CurrentNode
	mov eax, 0								; clear register eax
	mov al, Node_Connections[edx]			; Get the number of Connections
	mov cl, Node_ConnectionSize				; Get the size of a connection
	mul cl									; Multiply the number of connections by the size of connections
	add edx, Node_FixedSize					; Move past the Fixed Size portion of the Node
	add edx, eax							; Move past the Connections portion of the Node
	mov currentNode, edx
	pop ecx

	; Check if we've processed all nodes
	cmp edx, EndOfNodes
	jl TransmitLoop
	
TransmitLoopEnd:
	ret


ReceiveLoop:
	; Print out the name of the node being processed
	mov edx, currentNode
	mov esi, OFFSET msg_CurrentNode
	mov bl, byte ptr Node_Name[edx]
	mov byte ptr NodePositionOffset[esi], bl
	mov edi, OFFSET outputMessage
	mov ecx, SIZEOF msg_CurrentNode
	REP MOVSB
	call WriteMessageToOutput

	; For each connection of this node, copy the current packet into the transmit buffer
	mov ecx, 0
ReceiveConnectionLoop:
	mov edx, CurrentNode
	cmp cl, byte ptr Node_Connections[edx]
	je TransmitLoopNextNode
	
	mov edx, currentNode
	; Get the current connection
	mov eax, ecx
	push ecx
	mov ebx, Node_ConnectionSize
	mul ebx
	mov edx, currentNode
	add eax, Node_FixedSize
	add eax, edx
	mov ebx, [eax]

	; Check if there is a packet to receive
	
	; Get the connection name
	mov esi, OFFSET msg_ReceiveConnection
	mov al, byte ptr Node_Name[ebx]
	mov byte ptr msgReceiveOffset[esi], al
	mov edi, OFFSET outputMessage
	mov ecx, SIZEOF msg_ReceiveConnection
	CLD
	REP MOVSB
	call WriteMessageToOutput



	; Go to next connection
	pop ecx
	inc ecx
	jmp ReceiveConnectionLoop

ReceiveLoopNextNode:
	push ecx
	mov ecx, 0
	mov edx, CurrentNode
	mov eax, 0								; clear register eax
	mov al, Node_Connections[edx]			; Get the number of Connections
	mov cl, Node_ConnectionSize				; Get the size of a connection
	mul cl									; Multiply the number of connections by the size of connections
	add edx, Node_FixedSize					; Move past the Fixed Size portion of the Node
	add edx, eax							; Move past the Connections portion of the Node
	mov currentNode, edx
	pop ecx

	; Check if we've processed all nodes
	cmp edx, EndOfNodes
	jl ReceiveLoop
	
ReceiveLoopEnd:
	ret	


; Increments the queue pointer in EAX, and wraps to start of queue
IncrementQueuePointer:
	; Increment the pointer to the next position
	add eax, Packet_Size

	; Find the end of the queue
	push eax
	mov ebx, Node_QueueAddress[edi]
	add ebx, Queue_Size*Packet_Size
	
	; Compare the pointer to the end of the queue
	cmp eax, ebx
	jl EndIncrementQueuePointer
	mov eax, Node_QueueAddress[edi]

EndIncrementQueuePointer:
	pop ebx
	ret
	

; Current node is in register edi
; Current packet is stored in currentPacket
PushIntoQueue:
	push eax
	push ebx
	mov bl, Node_Name[edi]
	
	; Log message for adding packet to Node's queue
	push esi
	push edi
	push ecx
	mov esi, OFFSET msg_EnqueuePacket
	mov byte ptr msgEnqueueOffset[esi], bl
	mov ecx, SIZEOF msg_EnqueuePacket
	mov edi, OFFSET outputMessage
	cld
	REP MOVSB
	call WriteMessageToOutput
	pop ecx
	pop edi
	pop esi
	pop ebx

	
	; Check if the queue has space available
	mov eax, Node_InPointer[edi]				; Input Pointer for the Node's Queue
	call IncrementQueuePointer
	push ebx
	mov ebx, Node_OutPointer[edi]
	cmp eax, ebx
	pop ebx
	je QueueFull

	; Reset the pointer to the input pointer
	mov eax, Node_InPointer[edi]	; Input Pointer for the Node's Queue

	; Copy the packet into the queue
	push edi
	push esi
	push ecx
	mov esi, OFFSET currentPacket
	mov edi, eax
	mov ecx, Packet_Size
	cld
	REP MOVSB
	pop ecx
	pop esi
	pop edi

	; Move the InPointer to the next part of the queue
	call IncrementQueuePointer
	mov Node_InPointer[edi], eax

	; Set the carry flag to 0, as the operation was performed successfully, then return
	CLC
	pop eax
	ret

; If the queue is full, set the carry flag to 1 and do not perform any operation
QueueFull:
	STC
	pop eax
	ret


; Pops a packet from the queue
;	If the queue is empty, sets the carry flag
PopFromQueue:
	push eax
	push edi
	mov edi, currentNode
	mov eax, Node_OutPointer[edi]

	; Check if the Queue is Empty
	push ebx
	mov ebx, Node_InPointer[edi]
	cmp eax, ebx
	pop ebx
	je QueueEmpty

	; Copy the packet at the Out position of the queue to the currentPacket
	push esi
	push edi
	push ecx
	mov esi, eax
	mov edi, OFFSET currentPacket
	mov ecx, Packet_Size
	cld
	REP MOVSB
	pop ecx
	pop edi
	pop esi

	; Move the out pointer to the next position in the queue
	call IncrementQueuePointer

	; Set the carry flag to 0 for successful operation, then return
	pop edi
	pop eax
	CLC
	ret

; If the queue is empty, set the carry flag to 1
QueueEmpty:
	pop edi
	pop eax
	STC
	ret


; Write a Message to the output
;	Message stored in variable outputMessage
WriteMessageToOutput:
	push eax
	push edx
	push ecx

	; Write the system time out to the console
	mov edx, OFFSET msg_SystemTime				; Get the System Time message "[0000]"
	mov eax, systemTime							; Get the current system time
	call IntToString							; Convert the integer in eax to a String in edx, size in ecx

	; Copy the int string to the system time message
	inc edx										; Move the edx pointer back a couple bytes to fit the new string
	mov esi, OFFSET intString
	mov edi, edx
	mov ecx, 4
	REP MOVSB									; Copy the int into the message to output

	; Move the start of edx back to the beginning of the system time message
	dec edx
	mov ecx, SIZEOF msg_SystemTime

	; Get the file to write to
	mov eax, outputFileHandle
	call WriteString
	call WriteToFile
	
	; Write the given message in outputMessage to the console
	mov eax, outputFileHandle
	mov edx, OFFSET outputMessage
	mov ecx, SIZEOF outputMessage
	call WriteString
	call WriteToFile
	
	; Add new lines
	call Crlf
	mov edx, OFFSET fileCrlf
	mov ecx, 2
	mov eax, outputFileHandle
	call WriteToFile

	; Clear the output message buffer
	push esi
	push edi
	mov esi, OFFSET emptyString
	mov edi, OFFSET outputMessage
	mov ecx, SIZEOF emptyString
	CLD
	REP MOVSB
	pop edi
	pop esi

	; Return
	pop ecx
	pop edx
	pop eax
	ret


; Convert an integer to a string
;	integer stored in eax
IntToString:
	push eax
	push ebx
	push ecx
	push edx
	push edi
	mov eax, system_time
	mov edi, OFFSET intString
	add edi, 3
	mov ebx, 10

IntToStringLoop:
	cdq
	div ebx
	add edx, 48
	mov [edi], dl
	dec edi
	cmp eax, 0
	je EndIntToStringLoop
	jmp IntToStringLoop

EndIntToStringLoop:
	pop edi
	pop edx
	pop ecx
	pop ebx
	pop eax
	ret


; Quit the program
Quit:
	call Crlf

	mov edx, OFFSET msg_Quit					; Print the quit message
	call WriteString

	call Crlf

	exit


main ENDP
END main
