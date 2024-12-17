#include <iostream>

#include "func.h"

int main() {
    const int add = MyFunc::add(1, 2);
    const int sub = MyFunc::sub(7, 3);
    std::cout << "add value " << add << std::endl;
    std::cout << "sub value " << sub << std::endl;
    return 0;
}
