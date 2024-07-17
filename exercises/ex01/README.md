# EX01: Guessing Game

Welcome to CSE 332! This is your first programming assignment of the course. We will also help you get your computer set up for future assignments.

"Guessing Game" is a game with two parties: the "chooser" and the "guesser":
- The "chooser" picks a natural number larger than zero, and when given a guess, will return whether if the real answer is higher or lower. When you submit your code for grading, we will provide our secret implementation of the chooser.
- The "guesser" must guess what the number is by repeatedly making guesses to the "chooser" until the
correct answer is found. This is the class that you will be implementing.

## Tasks

### Set Up

Before you begin, go through the [Setting Up Your CSE 332 Environment](https://docs.google.com/document/d/1CWj02e1xjJY0J3CG_1tDxiOlDMnd2GU9PKmS5sskfXU/edit?usp=sharing)
handout. This will prepare your development environment for both this assignment and future programming projects.

After you have IntelliJ and Git set up, follow the instructions on that document to clone the repository that we individually generate for 
everyone. You should be cloning `ex01-yourNetID`, instead of `ex01-public`. 

### Completing the Exercise

Write Java code for `findNumber()`, a method that simulates the "guesser" in a guessing game by making a series of guesses
and returning the correct answer.

You should not have to bruteforce the number one-by-one. If the number chosen is $`n \geq 2`$, your code may make at
most $`2 \log_2(n)`$ guesses. What kind of algorithm (that you've learned from CSE 143) should you be using here to achieve
this complexity class?

Also note that there is no upper bound on the number the "chooser" can pick, so this is not something that you should
hardcode.

Open `src/main/java/Guesser.java` to get started. It might also be helpful to take a look at the `Chooser.java`
interface; however, you should only edit the method `findNumber` in `Guesser.java`. When you are not sure about what behavior 
to implement, be sure to check the class's comments _and_ any potential parent classes that it extends/implements -- this 
technique will become extremely important in the projects.

This repository will not contain the testing code for the staff `Chooser` which will be used to autograde your exercise.
However, if you just want to test if you have set up IntelliJ and Java correctly, you can click the green Run arrow
in `src/test/java/TestDummy.java` to run a sample test to test compilation.

### Submission on Gradescope

Log into Gradescope [here](https://www.gradescope.com/saml) using your UW email. If you do not see CSE 332, let the course staff know using a private Ed post or by writing to `cse332-staff@cs`.

If this is your first time submitting a coding assignment on Gradescope, take a look at
the [Submitting Projects](https://docs.google.com/document/d/155QBmHj9ztkvulFDS0f3UDZq_Tz8b7M2HR-aVfcBzQI/edit) handout.
Remember to use the GitLab option on Gradescope instead of uploading files directly.

### Grading
Submit on Gradescope to get your grade. There are no hidden tests for EX01, so the percentage that you see on Gradescope is your final grade.

### Important Notes
1. Avoid using `System.out.println()` when running the autograder. If you use any print statements, always remove them -- they perform poorly with large datasets. 
2. There is a small buffer on top of the $`2\log_2(k)`$ to handle scenarios such as when $`n = 2`$.

