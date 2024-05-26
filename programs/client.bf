++++++>+++++++ (6 7)
[-<[->>+>+<<<]>>>[-<<<+>>>]<<]<[-]>>[-<<+>>]<< multiply
>++++++++++>++++++++++
[-<[->>+>+<<<]>>>[-<<<+>>>]<<]<[-]>>[-<<+>>]<< multiply
+
[-<[->>+>+<<<]>>>[-<<<+>>>]<<]<[-]>>[-<<+>>]<< multiply
(4242)
` set port
{localhost}
[.%[-]>]

~ connect to localhost
[-] clear cell

{Hello World!}
save | clear | move everything back one
[^ [-] >
[[-<+>]>] << [<]>]
_

[-]
++++++++++
^ send 10 newline
[-]

^ send 0
_ flush all at once to server
! close connection