TITLE Network Simulator (G6P4.asm)				; OF DOOM(ED GRADES)

; CSC 323 - Group 6 - Assignment 4: Network Simulator
; Author: Robert Krency, kre1188@calu.edu
; Author: Anthony Stepich, ste4864@calu.edu
; Author: Camden Kovach, kov2428@calu.edu

; This program simulates a Network Topology


INCLUDE Irvine32.inc

.data

; Output Strings
msg_Details				BYTE	"Welcome to the Operating System Simulator.", 0
msg_GetInput			BYTE	">> ", 0
msg_Quit				BYTE	"Exiting...", 0




; Network Packet Definition
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

			



.code
main PROC

	nop




; Quit the program
Quit:
	call Crlf

	mov edx, OFFSET msg_Quit					; Print the quit message
	call WriteString

	call Crlf

	exit


main ENDP
END main