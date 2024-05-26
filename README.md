# NetFuck
## Overview:

This is a BrainFuck interpreter that supports networking commands. All BrainFuck operations work the same as normal, but this interpreter includes functionality for commands for network communications.\
\
All cells contain both a value in conventional BrainFuck, and a space to store TCP socket information, which is manipulated by the networking commands.

## Networking Commands:

**\`** Set the port using the value from the current cell\
**?** Opens port to be connected to\
**%** Adds value to IP address in character format (running % on a cell of 65 would add 'A' to your IP address)\
**~** Connect to an IP address currently specified and clears it, the socket handle is stored in the current cell. For ease of use, a value of 0 represents localhost. Addresses starting with "https://" will transfer data under SSL protocol. The port must be set using \` otherwise an error will occur\
**=** Accept a connection and store the socket handle in the current cell. The port must be set using \` otherwise an error will occur.
**!** Close a connection using the socket handle from the current cell\
**^** Send the current cell value into a buffer associated with the socket handle stored from the current cell\
**_** Send all data currently in the buffer with the socket handle stored from the current cell\
**V** Receive a value using the socket handle from the current cell and store it in the current cell\
**#** Sleep for the number of milliseconds specified in the current cell. (Due to integer size limits you can only sleep up to 28 days in one operation)

Note: All networking commands return a -1 on error

## Other Notes

{[char]} inputs a character into the given cell, for example {A} becomes [A]/[65]\
This also works for strings of characters, ie {AAB} -> [65, 65, 66]\
While this might go against the "spirit" of BrainFuck in some respects, it makes it more tolerable to connect to website addresses, and perform HTTP requests. 

There are several parameters which can be changed by passing flags: \
`-v verbose. Print information about network activity running in the NetFuck program.`\
`-e print errors. All networking errors already return a value of -1 to the current cell, but passing -e will print the Java error stack trace.`\
`-s output compiled BrainFuck code. Using {[char]} inserts in your NetFuck program requires the interpreter to recompile the program to add the operations that will directly add these characters to the stack. Passing -s will print the compiled NetFuck code to a new file with the _comp suffix.`\
All of these values are off by default, so passing the parameter simply activates them.

If you're connecting to a server through HTTP, the standard is to use port 80 for standard HTTP connections, and port 443 for secure HTTPS/TLS connections.

## Example Programs

Some sample programs that can be used with the NetFuck interpreter\
Stored in the programs folder, programs marked in bold are programs are programs that utilize the networking commands in NetFuck. The other (non-bolded) programs can be run as both NetFuck and normal BrainFuck code. These are useful for testing and debugging.

* fib.bf - Simple (non-network) fibonacci program
* helloworld.bf - Simple (non-network) hello world program
* **client.bf - Connects to a server on port 4242 of localhost and sends the string "Hello World!".**
* **client2.bf - Connects to a server on port 4242 of localhost and sends all user input until the character '0' is input.**
* **server.bf - Intended to work with client.bf and client2.bf. accepts connections on port 4242 of localhost and simply prints any messages sent by the client.**
* **client_rand.bf - Connects to www.randomnumberapi.com, sends an HTTP GET request for a random three-digit number, and prints that to the console.**
* **discord_client.bf - Connects to a discord webhook and sends user-input messages. replace the TOKEN value with the link to your discord webhook, in the form /api/webhooks/.../...**

## References / Other Resources

* modified from the specifications of GitHub user animehunter, 
https://github.com/animehunter/netfuck

* the first BrainFuck compiler was written in 1993 by Urban Müller

* here's a great tool for learning BrainFuck, which was also essential for debugging and making this interpreter a reality, https://minond.xyz/brainfuck/