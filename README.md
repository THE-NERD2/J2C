# J2C: Java bytecode to native executable

GraalVM native image doesn't seem to work for me, so I decided to make my own replacement.

## Features

> **Note:** Very early stages; not much functionality

- Printing high-level pseudocode with some opcodes
  - If you get an error saying "Unknown opcode: ...", submit your source code as an issue
- Recursive search for unknown classes
