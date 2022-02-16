# Chapter 7 Notes

- Introduction
- Arithmetic Expressions
- Overloaded Operators
- Type Conversions
- Relational and Boolean Expressions
- Short-Circuit Evaluation
- Assignment Statements
- Mixed-Mode Assignment


## Introduction
- Expressions are the fundamental means of specifying computations in a programming language
- To understand expression evaluation, need to be familiar with the orders of operator and operand evaluation
    - Associativity
    - Precedence
- Essence of imperative languages is dominant role of assignment statements

## Arithmetic Expressions
- Arithmetic evaluation was one of the motivations for the development of the first programming languages
- Arithmetic expressions consist of
    - operators (unary, binary, ternary; infix/prefix)
    - Operands
    - Parentheses
    - Function calls

### Design Issues
- Operator Precedence rules
- Operator Associativity rules
- Order of operand evaluation
- Operand evaluation side effects
- Operator overloading
- Type mixing in expressions

### Operators
- A unary operator has one operand
    - -a
- A binary operator has two operands
    - a + b
- A ternary operator has three operands
    - a ? b : c

### Operator Precedence Rules
- The *operator precedence rules* for expression evaluation define the order in which adjacent operators of different precedence levels are evaluated
- Typical precedence levels:
    1. Parentheses
    2. Unary Operators
    3. Exponentiation
    4. *, /
    5. +, -

### Operator Associativity Rules
- The *operator associativity rules* for expression evaluation define the order in which adjacent operators with the same precedence level are evaluated
- Typical associativity rules:
    - a-b+c = (a-b) + c
    - a^b^c = a ^ (b ^ c)
    - Not necessarily allowed in all languages; ADA does not allow for exponentiation like that
- APL is different; all operators have equal precedence and all operators associate **right to left**
    - A * B + C = A*(B+C)
- mathematically associate: associativity  rules have no impact on the value of an expression containing only those operators
    - A + B + C
    - Still have to be cautious about overflow with order

### Parentheses
- Can alter the precedence and associativity rules
    - A + B + C + D = (A+B)+(C+D)

### Expressions in Ruby and Scheme
- Ruby
    - All arithmetic, relational, and assignment operators, as well as array indexing, shifs, and bit-wise logic operators, are implemented as methods
    - One result of this is that these operators can all be overriden by application programs
- Scheme (and Common LISP)
    - All arithmetic and logic oeprators are by explicitly called subprograms
    - a + b * c = (+ a (* b c))

