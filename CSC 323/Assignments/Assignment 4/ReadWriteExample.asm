TITLE ReadWriteExample (G6P4.asm)				; OF DOOM(ED GRADES)

; CSC 323 - Group 6 - Assignment 4: Network Simulator
; Author: Robert Krency, kre1188@calu.edu
; Author: Anthony Stepich, ste4864@calu.edu
; Author: Camden Kovach, kov2428@calu.edu

; This program is an example of reading and writing a file


INCLUDE Irvine32.inc


; Constants
bufferSize      equ     81
fileBufferSize  equ     100
NULL            equ     0


.data

; File I/O Messages
msg_PromptInputFile     byte    "Enter Input File name: ", 0
msg_PromptOutputFile    byte    "Enter Output File name: ", 0
msg_FileError           byte    "Error opening file.", 0
msg_FileReadError       byte    "Error reading file.", 0
msg_FileWriteError      byte    "Error writing to file.", 0

; File Handles
inputFileHandle         dword   ?
outputFileHandle        dword   ?

; Buffers for the File Names, data, and number of bytes read
fileName                byte    bufferSize dup(0)       ; File Name
fileBuffer              byte    fileBufferSize dup(0)   ; File Buffer
                        byte    0                       ; Null for after the read
bytesRead               dword   0                       ; Number of bytes read from file


.code
main PROC

    ; Get the input file name
    mov edx, OFFSET msg_PromptInputFile
    mov ecx, SIZEOF msg_PromptInputFile
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
    
    call Crlf

    ; Get the output file name
    mov edx, OFFSET msg_PromptOutputFile
    mov ecx, SIZEOF msg_PromptOutputFile
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


ReadWriteLoop:
    ; Read a buffer from the input file
    mov eax, inputFileHandle
    mov edx, OFFSET fileBuffer
    mov ecx, fileBufferSize
    call ReadFromFile
    jc ErrorReadFile

    ; Check if anything was read
    mov bytesRead, eax
    cmp eax, 0
    jle EndReadWriteLoop
    mov fileBuffer[eax], NULL

    ; Write to output file
    mov eax, outputFileHandle
    mov edx, OFFSET fileBuffer
    mov ecx, bytesRead
    call WriteToFile
    cmp eax, 0
    je ErrorWriteFile
    jmp ReadWriteLoop



EndReadWriteLoop:
    mov eax, inputFileHandle
    call CloseFile
    mov eax, outputFileHandle
    call Closefile
    jmp Quit



ErrorOpenFile:
    mov edx, OFFSET msg_FileError
    mov ecx, SIZEOF msg_FileError
    call WriteString
    call Crlf
    jmp Quit

ErrorReadFile:
    mov edx, OFFSET msg_FileReadError
    mov ecx, SIZEOF msg_FileReadError
    call WriteString
    call Crlf
    jmp Quit

ErrorWriteFile:
    mov edx, OFFSET msg_FileWriteError
    mov ecx, SIZEOF msg_FileWriteError
    call WriteString
    call Crlf
    jmp Quit


Quit:
	exit

main ENDP
END main