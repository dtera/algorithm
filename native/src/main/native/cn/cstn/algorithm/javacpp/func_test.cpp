#include <iostream>

#include "func.h"

int main() {
  MyFunc func1, func2;
  int add = func1.add(1, 2);
  int sub = func2.sub(7, 3);
  std::cout << "add value " << add << std::endl;
  std::cout << "sub value " << sub << std::endl;
  return 0;
}
