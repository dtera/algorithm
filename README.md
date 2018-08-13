# [Algorithm](https://leetcode.com/problemset/algorithms/) ![Language](https://img.shields.io/badge/language-Java%20%2F%20C++%2011-orange.svg) [![License](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE.md) [![SayThanks](https://img.shields.io/badge/say-thanks-ff69b4.svg)](https://saythanks.io/to/zhaohuiqiang)

> In mathematics and computer science, an algorithm (/ˈælɡərɪðəm/) is an unambiguous specification of how to solve a class of problems. Algorithms can perform calculation, data processing and automated reasoning tasks.

> As an effective method, an algorithm can be expressed within a finite amount of space and time and in a well-defined formal language for calculating a function. Starting from an initial state and initial input (perhaps empty), the instructions describe a computation that, when executed, proceeds through a finite number of well-defined successive states, eventually producing "output" and terminating at a final ending state. The transition from one state to the next is not necessarily deterministic; some algorithms, known as randomized algorithms, incorporate random input.

> The concept of algorithm has existed for centuries and the use of the concept can be ascribed to Greek mathematicians, e.g. the sieve of Eratosthenes and Euclid's algorithm; the term algorithm itself derives from the 9th Century mathematician Muḥammad ibn Mūsā al'Khwārizmī, latinized 'Algoritmi'. A partial formalization of what would become the modern notion of algorithm began with attempts to solve the Entscheidungsproblem (the "decision problem") posed by David Hilbert in 1928. Subsequent formalizations were framed as attempts to define "effective calculability" or "effective method"; those formalizations included the Gödel–Herbrand–Kleene recursive functions of 1930, 1934 and 1935, Alonzo Church's lambda calculus of 1936, Emil Post's Formulation 1 of 1936, and Alan Turing's Turing machines of 1936–7 and 1939.

## [Commons Algorithm](https://leetcode.com/problemset/algorithms/) 
> This is commons algorithm implement, such as sort, find and etc.

* [Sort](https://github.com/zhaohuiqiang/LeetCode#bit-manipulation)
* [Find](https://github.com/zhaohuiqiang/LeetCode#array)


## [LeetCode Algorithm](https://leetcode.com/problemset/algorithms/) 
>The number of LeetCode questions is increasing every week.
For more questions and solutions, you can see my [Algorithm](https://github.com/zhaohuiqiang/algorithm) repository.
I'll keep updating for full summary and better solutions. Stay tuned for updates.
(Notes: "📖" means you need to subscribe to [LeetCode premium membership](https://leetcode.com/subscribe/) for the access to premium questions.)

* [Bit Manipulation](https://github.com/zhaohuiqiang/algorithm#bit-manipulation)
* [Array](https://github.com/zhaohuiqiang/algorithm#array)
* [String](https://github.com/zhaohuiqiang/algorithm#string)
* [Linked List](https://github.com/zhaohuiqiang/algorithm#linked-list)
* [Stack](https://github.com/zhaohuiqiang/algorithm#stack)
* [Queue](https://github.com/zhaohuiqiang/algorithm#queue)
* [Heap](https://github.com/zhaohuiqiang/algorithm#heap)
* [Tree](https://github.com/zhaohuiqiang/algorithm#tree)
* [Hash Table](https://github.com/zhaohuiqiang/algorithm#hash-table)
* [Math](https://github.com/zhaohuiqiang/algorithm#math)
* [Two Pointers](https://github.com/zhaohuiqiang/algorithm#two-pointers)
* [Sort](https://github.com/zhaohuiqiang/algorithm#sort)
* [Recursion](https://github.com/zhaohuiqiang/algorithm#recursion)
* [Binary Search](https://github.com/zhaohuiqiang/algorithm#binary-search)
* [Binary Search Tree](https://github.com/zhaohuiqiang/algorithm#binary-search-tree)
* [Breadth-First Search](https://github.com/zhaohuiqiang/algorithm#breadth-first-search)
* [Depth-First Search](https://github.com/zhaohuiqiang/algorithm#depth-first-search)
* [Backtracking](https://github.com/zhaohuiqiang/algorithm#backtracking)
* [Dynamic Programming](https://github.com/zhaohuiqiang/algorithm#dynamic-programming)
* [Greedy](https://github.com/zhaohuiqiang/algorithm#greedy)
* [Graph](https://github.com/zhaohuiqiang/algorithm#graph)
* [Geometry](https://github.com/zhaohuiqiang/algorithm#geometry)
* [Design](https://github.com/zhaohuiqiang/algorithm#design)
* [SQL](https://github.com/zhaohuiqiang/algorithm#sql)
* [Shell Script](https://github.com/zhaohuiqiang/algorithm#shell-script)

### Bit Manipulation
|  #  | Title           |  Solution       |  Time           | Space           | Difficulty    | Tag          | Note| 
|-----|---------------- | --------------- | --------------- | --------------- | ------------- |--------------|-----|
136 | [Single Number](https://leetcode.com/problems/single-number/) | [C++](./C++/single-number.cpp) [Python](./Python/single-number.py) | _O(n)_       | _O(1)_          | Easy         |||
137 | [Single Number II](https://leetcode.com/problems/single-number-ii/) | [C++](./C++/single-number-ii.cpp) [Python](./Python/single-number-ii.py) | _O(n)_ | _O(1)_          | Medium         |||
190 | [Reverse Bits](https://leetcode.com/problems/reverse-bits/)  | [C++](./C++/reverse-bits.cpp) [Python](./Python/reverse-bits.py) | _O(1)_        | _O(1)_          | Easy           |||

### Array
|  #  | Title           |  Solution       |  Time           | Space           | Difficulty    | Tag          | Note| 
|-----|---------------- | --------------- | --------------- | --------------- | ------------- |--------------|-----|
015 | [3 Sum](https://leetcode.com/problems/3sum/)         | [C++](./C++/3sum.cpp) [Python](./Python/3sum.py)       | _O(n^2)_        | _O(1)_          | Medium         || Two Pointers
016 | [3 Sum Closest](https://leetcode.com/problems/3sum-closest/) | [C++](./C++/3sum-closest.cpp) [Python](./Python/3sum-closest.py) | _O(n^2)_       | _O(1)_          | Medium         || Two Pointers
018| [4 Sum](https://leetcode.com/problems/4sum/)         | [C++](./C++/4sum.cpp) [Python](./Python/4sum.py)        | _O(n^3)_    | _O(1)_    | Medium         || Two Pointers

### SQL
|  #  | Title           |  Solution       |  Time           | Space           | Difficulty    | Tag          | Note| 
|-----|---------------- | --------------- | --------------- | --------------- | ------------- |--------------|-----|
175| [Combine Two Tables](https://leetcode.com/problems/combine-two-tables/) | [MySQL](./MySQL/combine-two-tables.sql) | _O(m + n)_   | _O(m + n)_ | Easy     ||
176| [Second Highest Salary](https://leetcode.com/problems/second-highest-salary/) | [MySQL](./MySQL/second-highest-salary.sql) | _O(n)_ | _O(1)_ | Easy         ||
177| [Nth Highest Salary](https://leetcode.com/problems/nth-highest-salary/) | [MySQL](./MySQL/nth-highest-salary.sql) | _O(n^2)_   | _O(n)_ | Medium         ||

### Shell Script
|  #  | Title           |  Solution       |  Time           | Space           | Difficulty    | Tag          | Note| 
|-----|---------------- | --------------- | --------------- | --------------- | ------------- |--------------|-----|
192 | [Word Frequency](https://leetcode.com/problems/word-frequency/) | [Shell](./Shell/word-frequency.sh) | _O(n)_     | _O(k)_          | Medium         ||
193 | [Valid Phone Numbers](https://leetcode.com/problems/valid-phone-numbers/) | [Shell](./Shell/valid-phone-numbers.sh) | _O(n)_ | _O(1)_    | Easy           ||
194 | [Transpose File](https://leetcode.com/problems/transpose-file/) | [Shell](./Shell/transpose-file.sh) | _O(n^2)_   | _O(n^2)_        | Medium         ||
