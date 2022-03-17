# d-interpreter

An interpreter for a dynamic language.

Any variable can have any type, determined at runtime. Several types are supported: interger, real, boolean, string (basic types), array, tuple (user-defined types), function. All functions are anonymous, but can be assigned to variables.

The project is for educational purpose mainly, but many basic algorithms can be implemented using this language (see ***example/\**** folder).

The grammar of language defined here: https://hackmd.io/@rVcuiUEATqOC-eoEZx_mmQ/rk4NYwfb9

## Main parts of interpreter are:

1. Lexical analyzer
2. Syntax analyzer
3. Semantics analyzer
4. Interpreter 

### Lexical analyzer
Split each symbol to token. Checks the validity of keywords, literals, identifiers

### Syntax analyzer
Constructs AST from the tokens

### Semantics analyzer
Checks validity of identifier assignment

### Interpreter
Interprets the program represented as an AST tree

## Technological stack:
1. Java 17
2. Gradle
3. JUnit 5

## Installation steps:
1. Download SDK 17 on your machine
2. Clone the project
3. Use terminal or any GUI to run the project


## Run
One way to run the interpretation is using `gradle`:

```gradle run --args YOUR_PROGRAM_FILE```

Example:
```gradle run --args example/binary_search.txt```

