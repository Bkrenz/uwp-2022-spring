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
msg_MissingJobName		BYTE	"Missing job name. Input Job Name: ", 0
msg_MissingJobPriority	BYTE	"Missing job priority. Input Job Priority: ", 0
msg_InvalidJobPriority	BYTE	"Invalid job priority. No command executed.", 0
msg_MissingJobRunTime	BYTE	"Missing job runtime. Input Job RunTime: ", 0
msg_InvalidRunTime		BYTE	"Invalid run time. No command executed.", 0

msg_Help				BYTE	"",0   ; TODO

; Input Buffer
buffer					BYTE	21 DUP(0)
byteCount				dword	?


; Job Record Offsets
JName		equ		0
JPriotiy	equ		8
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


; Job Variables
curJobPointer			
curJobName
curJobPriority
curJobRunTime


; System Variables
system_time				dword	0


; Flags
flag_AvailableRecord	BYTE	0
flag_JobExists			BYTE	0


.code
main PROC
	mov edx, OFFSET msg_Details				; Display welcome message
	call WriteString
	call Crlf

	mov system_time, 0
	move curJobPointer, offset endOfJobsarray-SizeOfJob

while1:
	call ProcessCommand
	jc endwhile1
	jmp while1

endwhile1:
	call Quit


; The command handler: it calls GetInput to get the input from the user and extracts
; the word from the input by calling GetWord, then process the case statement calling
; the command routines
ProcessCommand:
	nop


; Clears the input buffer, resets the index, prompts the user, reads the input,
; then calls SkipWhiteSpace
GetInput:
	mov edx, OFFSET buffer					; Read input from user as string
	mov ecx, SIZEOF buffer
	call ReadString
	mov byteCount, eax


; Skips white space in the input from the current index
SkipWhiteSpace:
	nop


; Copies the characters from the input buffer starting with the current
; input index to a word buffer until a non-alpha character, null, or end of line
; is reached.
GetWord:
	nop
	

; Calls SkipWhiteSpace then if there is not a parameter left in the input buffer the
; user will be prompted for a job name and the GetInput procedure will be called.
; Once there is input, the GetWord procedure will be called and the job name is kept.
GetJobName:
	nop


; Converst the digit characters in the input buffer starting with the current index to
; a positive or negative number.
GetNumber:
	nop


; Calls SkipWhiteSpace then if there is not a parameter left in the input buffer the user
; will be prompted for a priority and the GetInput procedure will be called. Once there
; is input, the GetNumber procedure will be called.
; The priority will be validated and the priority will be kept.
; There is no re-prompting for an invalid priority, a message will be displayed and the
; operation will not be performed.
GetPriority:
	nop


; Calls SkipWhiteSpace then if there is not a parameter left in the input buffer the user
; will be prompted for a run time and the GetInput procedure will be called. Once there
; is input the GetNumber procedure will be called.
; The run time will be validated and the run time will be kept.
; There is no re-prompting for and invalid run time, a message will be displayed and the
; operation will not be performed. 
GetRunTime:
	nop


; Finds the next available record by testing if the status field is set to available (0).
; When the first available record is reached, this returns with the address of that record.
; When the procedure reaches the end of the job recods without finding an available space,
; the procedure returns indicating no space is available.
FindNextAvailableRecord:
	nop


; Search through the jobs records to find a job that matches the specified job name that is
; not available space, ie the job is not in a run or hold status. Status != 0.
; When the job name is found, the procedure returns with the address of the record.
; When the procedure reaches the end of the jobs record without finding the job name,
; the procedure returns indicating the job was not found.
FindJob:
	nop


; This calls the GetJobName procedure followed by calling the GetPriority procedure then
; finally the GetRunTime procedure. If all of the data for a job is gathered, the FindJob
; procedure is called to see if the JobName already is loaded. When the job is unique,
; the FindNext procedure is called to find an available location for the job. When a 
; location is found the unique job, the information is placed into the record and the
; status is changed from available to hold.
LoadJob:
	nop


; Gets the job name and if it exists, sets the status to hold.
HoldJob:
	nop

; Gets the job name and sets its status to Run
RunJob:
	nop

; Gets the job and if it is in a hold status, sets its status to available.
; For any other circumstance, an appropriate error message is displayed.
KillJob:
	nop

; This will process the next job with the highest priority that is in run mode. It will
; not always start at the beginning and it will not continue to process the same job.
; The processing will only process the same job if it is the next job of the highest priority
; in the run mode. Every time a step is processed the system time will increment even if there
; are no jobs to process.
Step:
	nop


; The show procedure begins at the beginning of the jobs record and proceeds to the end.
; Each record is checked if it is a run or hold state. When a record is not in the available
; state, the information is retrieved from the record: job name, priority, status, run time,
; and load time. The information is then neatly displayed to the user. The status is printed
; as words not numbers: run or hold.
Show:
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
