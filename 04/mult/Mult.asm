
@R2						// this is the product
M = 0 						//@R2 = 0
@R0						// this is the multiplicand
D = M						//inputs get D = M always. Here, @R0 = M (user input in RAM[A])
@R1						// this is the multiplicator ("R1 times")
D = M						//inputs get D = M always. Here, @R1 = M(user input in RAM[A])
@i
M = D						//@i = R1. i will count down from R1, and once its equal to 0, we jump to END

(LOOP)

//start exit condition check
// jump if i = 0
@i
D = M
@END
D;JEQ
// end exit condition check

//start otherwise

//decrement i 
@i
D = M
@1
D = D - A
@i
M = D

// R2 = R2 + R0, R2's starting value was 0
@R2
D = M
@R0
D = D + M
@R2
M = D

//end otherwise

@LOOP
0;JMP




(END)
@END
0;JMP




 
