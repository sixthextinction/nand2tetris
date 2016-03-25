(START)

@KBD
D = M
@FILL
D;JNE
@WIPE
D;JEQ

@START
0;JMP


//#########################################################################################################
(FILL)

@SCREEN
D = A
@8191				// A = 8191
D = D + A
@temp				// A = wherever temp is
M = D 				// temp holds 8191, use temp value and assign to A each time.

(LOOPF)
@temp
D = M
@SCREEN
D = D - A
@START				//once filled, jump back to start of program. This is the exit out of FILL "function"
D;JLT
@temp
A = M
M = -1
@temp
M = M - 1
@LOOPF
0;JMP
//#########################################################################################################
(WIPE)

@SCREEN
D = A
@8191				// A = 8191
D = D + A
@temp				// A = wherever temp is
M = D 				// temp holds 8191, use temp value and assign to A each time.

(LOOPW)
@temp
D = M
@SCREEN
D = D - A
@START				//once filled, jump back to start of program. This is the exit out of FILL "function"
D;JLT
@temp
A = M
M = 0
@temp
M = M - 1
@LOOPW
0;JMP
//#########################################################################################################
