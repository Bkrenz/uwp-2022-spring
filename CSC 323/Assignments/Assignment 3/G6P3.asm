TITLE Operating System Simulator (G6P3.asm)				; OF DOOM(ED GRADES)

; CSC 323 - Group 6 - Assignment 3: Operating System Simulator
; Author: Robert Krency, kre1188@calu.edu
; Author: Anthony Stepich, ste4864@calu.edu
; Author: Austin Pringle, pri2679@calu.edu
; Author: Camden Kovach, kov2428@calu.edu

; This program simulates an Operating System that handles up to ten jobs and
; the stepping of those jobs.


; TODO:
;	Write Help Messages


; NOTES:
;	ASCII Capital letters: 65-90


; QUESTIONS:
;	Store job name as is, or can force to lower/upper case
;	Overwrite existing jobs?


INCLUDE Irvine32.inc

.data

; Output Strings
msg_Details				BYTE	"Welcome to the Operating System Simulator.", 0
msg_GetInput			BYTE	"Enter input: ", 0
msg_Quit				BYTE	"Exiting...", 0
msg_InvalidCommand		BYTE	"Invalid command entered."
msg_MissingJobName		BYTE	"Invalid job name. Input Job Name (max 8 chars): ", 0
msg_MissingJobPriority	BYTE	"Missing job priority. Input Job Priority: ", 0
msg_InvalidJobPriority	BYTE	"Invalid job priority. No command executed.", 0
msg_MissingJobRunTime	BYTE	"Missing job runtime. Input Job RunTime: ", 0
msg_InvalidRunTime		BYTE	"Invalid run time. No command executed.", 0
msg_JobFinished			BYTE	"Job finished."

msg_Help				BYTE	"Help",0   ; TODO


; Input Buffer
bufferSize				equ		100
buffer					BYTE	bufferSize DUP(0)
byteCount				DWORD	?
wordMaxSize				equ		bufferSize
currentWord				BYTE	wordMaxSize DUP(0)
currentWordSize			BYTE	0


; ASCII Equivalents
ascii_Z_Upper		equ 90
ascii_A_Upper		equ 65
ascii_Z_Lower		equ 122
ascii_A_Lower		equ 97
ascii_Tab			equ 9
ascii_Space			equ 32
ascii_Null			equ 0
ascii_EndOfLine		equ 3
ascii_Zero			equ 48
ascii_Nine			equ 57
ascii_MinusSign		equ 45

; Job Record Offsets
JName		equ		0
JPriority	equ		8
JStatus		equ		9
JRunTime	equ		10
JLoadTime	equ		12


; Job Constants
JobAvailable	equ		0
JobRun			equ		1
JobHold			equ		2
LowestPriority	equ		7
SizeOfJob		equ		14
NumberOfJobs	equ		10


; Job Records
;	Byte 0-7: Job Name
;	Byte 8: Priority
;	Byte 9: Status
;	Byte 10-11: Run Time
;	Byte 12-13: Load Time
jobsArray				BYTE	NumberOfJobs*SizeOfJob dup(JobAvailable)
endOfJobsArray			DWORD	endOfJobsArray
jobIndex				DWORD	0


; Job Variables
curJobPointer			DWORD	0
curJobName				BYTE	8 DUP(0)
curJobPriority			BYTE	0
curJobStatus			BYTE	0
curJobRunTime			WORD	0
curJobLoadtime			WORD	0


; Command Names
cmd_QUIT				BYTE	"quit", 0
cmd_HELP				BYTE	"help", 0
cmd_LOAD				BYTE	"load", 0
cmd_RUN					BYTE	"run", 0
cmd_HOLD				BYTE	"hold", 0
cmd_KILL				BYTE	"kill", 0
cmd_SHOW				BYTE	"show", 0
cmd_STEP				BYTE	"step", 0
cmd_CHANGE				BYTE	"change", 0


; System Variables
system_time				word	0


; Flags
flag_AvailableRecord	BYTE	0
flag_JobExists			BYTE	0
flag_NegativeNumber		BYTE	0
flag_JobStepAvailable	BYTE	0


.code
main PROC
	mov edx, OFFSET msg_Details				; Display welcome message
	call WriteString
	call Crlf

	mov system_time, 0
	mov curJobPointer, offset endOfJobsarray-SizeOfJob

while1:
	call ProcessCommand
	;jc endwhile1
	jmp while1

endwhile1:
	call Quit


; The command handler: it calls GetInput to get the input from the user and extracts
; the word from the input by calling GetWord, then process the case statement calling
; the command routines
ProcessCommand:

	; Get input from the user
	call GetInput

	; Check if we're at the end of the input line
	mov al, [esi]
	cmp al, ascii_Null
	je EndProcessCommand

	; Get the word from the input
	call GetWord

	; Move word into register for comparisons
	mov esi, OFFSET currentWord

	; Check which command this is
case_cmd_QUIT:
	mov edi, OFFSET cmd_QUIT
	cld
	mov ecx, LENGTHOF cmd_QUIT
	REPE CMPSB
	jne case_cmd_HELP
	call Quit

case_cmd_HELP:
	mov edi, OFFSET cmd_HELP
	cld
	mov ecx, LENGTHOF cmd_HELP
	REPE CMPSB
	jne case_cmd_LOAD
	call Help
	jmp EndProcessCommand

case_cmd_LOAD:
	mov edi, OFFSET cmd_LOAD
	cld
	mov ecx, LENGTHOF cmd_LOAD
	REPE CMPSB
	jne case_cmd_RUN
	call LoadJob
	jmp EndProcessCommand

case_cmd_RUN:
	mov edi, OFFSET cmd_RUN
	cld
	mov ecx, LENGTHOF cmd_RUN
	REPE CMPSB
	jne case_cmd_HOLd
	call RunJob
	jmp EndProcessCommand

case_cmd_HOLD:
	mov edi, OFFSET cmd_HOLD
	cld
	mov ecx, LENGTHOF cmd_HOLD
	REPE CMPSB
	jne case_cmd_KILL
	call HoldJob
	jmp EndProcessCommand

case_cmd_KILL:
	mov edi, OFFSET cmd_KILL
	cld
	mov ecx, LENGTHOF cmd_KILL
	REPE CMPSB
	jne case_cmd_SHOW
	call KillJob
	jmp EndProcessCommand

case_cmd_SHOW:
	mov edi, OFFSET cmd_SHOW
	cld
	mov ecx, LENGTHOF cmd_SHOW
	REPE CMPSB
	jne case_cmd_STEP
	call Show
	jmp EndProcessCommand

case_cmd_STEP:
	mov edi, OFFSET cmd_STEP
	cld
	mov ecx, LENGTHOF cmd_STEP
	REPE CMPSB
	jne case_cmd_CHANGE
	call Step
	jmp EndProcessCommand

case_cmd_CHANGE:
	mov edi, OFFSET cmd_CHANGE
	cld
	mov ecx, LENGTHOF cmd_CHANGE
	REPE CMPSB
	jne case_default
	call Change
	jmp EndProcessCommand

case_default:
	mov edx, OFFSET msg_InvalidCommand
	call WriteString
	call Crlf

EndProcessCommand:
	; Return to the main loop
	ret


; Clears the input buffer, resets the index, prompts the user, reads the input,
; then calls SkipWhiteSpace
GetInput:
	mov edx, OFFSET msg_GetInput
	call WriteString

	mov edx, OFFSET buffer
	mov ecx, SIZEOF buffer
	call ReadString
	mov byteCount, eax

	; Skip the white space in the input
	mov esi, OFFSET buffer
	call SkipWhiteSpace

	ret


; Skips white space in the input from the current index
SkipWhiteSpace:
	mov al, [esi]
	cmp al, ascii_Tab
	je SkipChar

	mov al, [esi]
	cmp al, ascii_Space
	je SkipChar

	ret

SkipChar:
	inc esi
	jmp SkipWhiteSpace


; Copies the characters from the input buffer starting with the current
; input index to a word buffer until a non-alpha character, null, or end of line
; is reached.
GetWord:
	mov edi, OFFSET currentWord
	mov ebx, 0

GetWordLoop:
	; Move the current character into the al register, then force it to upper case
	mov al, [esi]
	and al, 223

	; Check that it is a valid character
	cmp al, ascii_A_Upper
	jl InvalidChar
	cmp al, ascii_Z_Upper
	jg InvalidChar

	mov edx, [esi]
	mov [edi+ebx], edx
	inc ebx
	inc esi

	jl GetWordLoop
	mov currentWordSize, bl

InvalidChar:
	ret
	

; Calls SkipWhiteSpace then if there is not a parameter left in the input buffer the
; user will be prompted for a job name and the GetInput procedure will be called.
; Once there is input, the GetWord procedure will be called and the job name is kept.
GetJobName:
	call SkipWhiteSpace
	
	mov al, [esi]
	cmp al, ascii_Null
	jne GetJobWord

GetJobInput:
	mov edx, OFFSET msg_MissingJobName
	call WriteString
	call GetInput
	jmp GetJobName

GetJobWord:
	call GetWord

	; Make sure the word is a valid size
	cmp currentWordSize, 0
	je GetJobInput
	cmp currentWordSize, 8
	jg GetJobInput

	; Move the Word to the current Job Name
	mov esi, OFFSET currentWord
	mov edi, OFFSET curJobName
	mov ecx, LENGTHOF currentWord
	cld
	REP MOVSB

	ret


; Converst the digit characters in the input buffer starting with the current index to
; a positive or negative number.
GetNumber:
	mov eax, 0
	mov ebx, 10
	
	; Check if negative number
	mov flag_NegativeNumber, 0
	mov al, [esi]
	cmp al, ascii_MinusSign
	jne GetNumberLoop
	mov flag_NegativeNumber, 1
	inc esi

GetNumberLoop:
	mov dl, [esi]
	cmp dl, ascii_Zero
	jl CheckNegative
	
	mov dl, [esi]
	cmp dl, ascii_Zero
	jg CheckNegative

	mov ecx, [esi]
	add ecx, -48
	mul ebx
	add eax, ecx

	inc esi
	jmp GetNumberLoop

CheckNegative:
	cmp flag_NegativeNumber, 1
	jne GetNumberEnd
	neg eax

GetNumberEnd:
	ret


; Calls SkipWhiteSpace then if there is not a parameter left in the input buffer the user
; will be prompted for a priority and the GetInput procedure will be called. Once there
; is input, the GetNumber procedure will be called.
; The priority will be validated and the priority will be kept.
; There is no re-prompting for an invalid priority, a message will be displayed and the
; operation will not be performed.
GetPriority:
	call SkipWhiteSpace

	mov dl, [esi]
	cmp dl, ascii_Null
	jne ProcessPriority

	call GetInput
	call GetNumber

ProcessPriority:
	cmp eax, 0
	jl InvalidPriority

	cmp eax, 7
	jg InvalidPriority

	mov curJobPriority, al

InvalidPriority:
	mov edx, OFFSET msg_InvalidJobPriority
	call WriteString
	call Crlf


; Calls SkipWhiteSpace then if there is not a parameter left in the input buffer the user
; will be prompted for a run time and the GetInput procedure will be called. Once there
; is input the GetNumber procedure will be called.
; The run time will be validated and the run time will be kept.
; There is no re-prompting for and invalid run time, a message will be displayed and the
; operation will not be performed. 
GetRunTime:
	call SkipWhiteSpace

	mov dl, [esi]
	cmp dl, ascii_Null
	jne ProcessRunTime

	call GetInput
	call GetNumber

ProcessRunTime:
	cmp eax, 0
	jl InvalidRunTime

	cmp eax, 7
	jg InvalidRunTime

	mov curJobRunTime, ax
	ret

InvalidRunTime:
	mov edx, OFFSET msg_InvalidRunTime
	call WriteString
	call Crlf
	ret


; Finds the next available record by testing if the status field is set to available (0).
; When the first available record is reached, this returns with the address of that record.
; When the procedure reaches the end of the job records without finding an available space,
; the procedure returns indicating no space is available.
FindNextAvailableRecord:
	mov esi, OFFSET jobsArray
	cmp jobsArray[JStatus], 0
	je AvailableRecord

	cmp esi, endOfJobsArray
	je NoAvailableRecord

	add esi, SizeOfJob
	jmp FindNextAvailableRecord

AvailableRecord:
	mov curJobPointer, esi
	mov flag_AvailableRecord, 1
	ret

NoAvailableRecord:
	mov flag_AvailableRecord, 0
	ret

; Search through the jobs records to find a job that matches the specified job name that is
; not available space, ie the job is not in a run or hold status. Status != 0.
; When the job name is found, the procedure returns with the address of the record.
; When the procedure reaches the end of the jobs record without finding the job name,
; the procedure returns indicating the job was not found.
FindJob:
	mov esi, OFFSET curJobName
	mov edx, OFFSET jobsArray

FindJobLoop:
	mov edi, OFFSET jobsArray+JName
	mov ecx, LENGTHOF curJobName
	cld
	REPE CMPSB
	je JobFound

	cmp edx, endOfJobsArray
	je NoJobFound

	add jobsArray, SizeOfJob
	jmp FindJobLoop

JobFound:
	mov curJobPointer, edx
	mov flag_JobExists, 1
	ret

NoJobFound:
	mov flag_JobExists, 0
	ret

; This calls the GetJobName procedure followed by calling the GetPriority procedure then
; finally the GetRunTime procedure. If all of the data for a job is gathered, the FindJob
; procedure is called to see if the JobName already is loaded. When the job is unique,
; the FindNext procedure is called to find an available location for the job. When a 
; location is found for the unique job, the information is placed into the record and the
; status is changed from available to hold.
LoadJob:
	call GetJobName
	call GetPriority
	call GetRunTime

	; Check if good TODO

	call FindJob
	cmp flag_JobExists, 1
	je EndLoadJob

	call FindNextAvailableRecord
	cmp flag_AvailableRecord, 1
	jne EndLoadJob

	mov esi, OFFSET curJobName
	mov edi, OFFSET curJobPointer[JName]
	mov ecx, LENGTHOf curJobName
	cld
	REP MOVSB

	mov al, curJobPriority
	mov byte ptr curJobPointer[JPriority], al

	mov al, JobHold
	mov byte ptr curJobPointer[JStatus], al

	mov ax, curJobRunTime
	mov word ptr curJobPointer[JRunTime], ax

	mov ax, system_time
	mov word ptr curJobPointer[JLoadTime], ax

EndLoadJob:
	ret


; Gets the job name and if it exists, sets the status to hold.
HoldJob:
	call GetJobName
	; Check for valid input

	call FindJob

	cmp flag_JobExists, 1
	jne EndHold

	mov curJobPointer[JStatus], JobHold

EndHold:
	ret

; Gets the job name and sets its status to Run
RunJob:
	call GetJobName
	; Check for valid input

	call FindJob

	cmp flag_JobExists, 1
	jne EndRun

	mov curJobPointer[JStatus], JobRun

EndRun:
	ret

; Gets the job and if it is in a hold status, sets its status to available.
; For any other circumstance, an appropriate error message is displayed.
KillJob:
	call GetJobName
	; Check for valid input

	call FindJob

	cmp flag_JobExists, 1
	jne EndKill

	mov curJobPointer, JobAvailable

EndKill:
	ret

; This will process the next job with the highest priority that is in run mode. It will
; not always start at the beginning and it will not continue to process the same job.
; The processing will only process the same job if it is the next job of the highest priority
; in the run mode. Every time a step is processed the system time will increment even if there
; are no jobs to process.
Step:
	call SkipWhiteSpace
	call GetNumber
	; Check for valid input

	mov flag_JobStepAvailable, 0
	mov esi, OFFSET jobsArray
	mov curJobPriority, 8

StepLoop:
	cmp eax, 0
	jle EndStep

	dec eax

	call FindHighestPriorityJob
	inc system_time

	cmp flag_JobStepAvailable, 1
	jne StepLoop

	call ShowCurrentJob
	dec curJobPointer[JRunTime]

	cmp curJobPointer[JRunTime], 0
	jle StepLoopPartDeux

	mov curJobPointer[JStatus], JobAvailable
	mov edx, OFFSET msg_JobFinished
	call WriteString
	call Crlf

StepLoopPartDeux:
	add esi, SizeOfJob
	jmp StepLoop

EndStep:
	ret

FindHighestPriorityJob:
	cmp esi, curJobPointer
	je EndFindPriority

	mov al, [esi+JStatus] 
	cmp al, JobRun
	jne NextRecord

	mov al, [esi+JPriority]
	cmp al, curJobPriority
	jge NextRecord

	mov curJobPointer, esi
	mov al, byte ptr curJobPointer[JPriority]
	mov curJobPriority, al

NextRecord:
	add esi, SizeOfJob
	jmp FindHighestPriorityJob

EndFindPriority:
	mov flag_JobStepAvailable, 1
	ret


; The show procedure begins at the beginning of the jobs record and proceeds to the end.
; Each record is checked if it is a run or hold state. When a record is not in the available
; state, the information is retrieved from the record: job name, priority, status, run time,
; and load time. The information is then neatly displayed to the user. The status is printed
; as words not numbers: run or hold.
Show:
	mov esi, OFFSET jobsArray

ShowLoop:
	mov curJobPointer, esi
	call ShowCurrentJob

	cmp esi, endOfJobsArray
	je EndShow

	add esi, SizeOfJob
	jmp ShowLoop

EndShow:
	ret

ShowCurrentJob:
	call Crlf
	mov edx, curJobPointer[JName]
	call WriteString
	call Crlf
	mov eax, 0
	mov al, byte ptr curJobPointer[JPriority]
	call WriteInt
	call Crlf
	mov al, byte ptr curJobPointer[JStatus]
	call WriteInt
	call Crlf
	mov ax, word ptr curJobPointer[JRunTime]
	call WriteInt
	call Crlf
	mov ax, word ptr curJobPointer[JLoadTime]
	call WriteInt
	call Crlf
	ret


; Changes the job priority
Change:
	call FindJob
	cmp flag_JobExists, 1
	jne EndChange

	call GetNumber
	cmp eax, 0
	jl EndChange
	cmp eax, 7
	jg EndChange

	mov byte ptr curJobPointer[JPriority], al

EndChange:
	ret

; Prints out the help messages
Help:
	nop


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
