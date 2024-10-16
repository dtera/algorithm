#include<iostream>
#include"func.h"

int main() {
	MyFunc myFunc;
	int value = myFunc.add(1, 2);
	std::cout << "add value " << value << std::endl;
	return 0;
}