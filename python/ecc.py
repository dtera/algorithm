# coding:utf-8
"""
Elliptic Curve Cryptography
"""
import random
from math import gcd

import pandas as pd

pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', 20)
pd.set_option('display.width', 1000)


# noinspection PyPep8Naming
class EccCurve:
    def __init__(self, p, a, b, G=None, sk=None, pk=None):
        if (4 * (a ** 3) + 27 * (b ** 2)) % p == 0:
            print(f"({p}, {a}, {b}) can not be used for ecc\n")
        self.p = p
        self.a = a
        self.b = b
        self.points = self._ecc_all_points()
        self.GS = [(x, (-1 * y) % self.p) for x, y in self.points]
        self.ecc_points_print()
        self.G = self.GS[random.randrange(1, len(self.GS))] if G is None else G
        self._set_order()
        self._ec_elgamal_generator(sk, pk)
        self._table = {}
        for i in range(1, p + 1):
            self._table[self.ecc_point_np(i, self.G)] = i

    def mod_inverse(self, x):
        for i in range(1, self.p):
            if (i * x) % self.p == 1:
                return i
        return -1

    def _eval_lambda(self, x1, y1, x2, y2):
        sign = 1
        if x1 == x2 and y1 == y2:
            numerator = (3 * x1 ** 2 + self.a)
            denominator = (2 * y1)
        else:
            numerator = y2 - y1
            denominator = x2 - x1
            if numerator * denominator < 0:
                sign = -1
                numerator = abs(numerator)
                denominator = abs(denominator)

        d = gcd(numerator, denominator)
        numerator //= d
        denominator //= d
        denominator_ = self.mod_inverse(denominator)
        lambda_ = numerator * denominator_ * sign
        lambda_ %= self.p
        return lambda_

    def ecc_point_add_(self, x1, y1, x2, y2):
        lambda_ = self._eval_lambda(x1, y1, x2, y2)
        x3 = (lambda_ ** 2 - x1 - x2) % self.p
        y3 = (lambda_ * (x1 - x3) - y1) % self.p
        return x3, y3

    def ecc_point_sub_(self, x1, y1, x2, y2):
        return self.ecc_point_add_(x1, y1, x2, (-1 * y2) % self.p)

    def ecc_point_add(self, p1, p2):
        return self.ecc_point_add_(p1[0], p1[1], p2[0], p2[1])

    def ecc_point_sub(self, p1, p2):
        return self.ecc_point_sub_(p1[0], p1[1], p2[0], p2[1])

    def ecc_point_np_(self, n, x, y):
        x_, y_ = self.ecc_point_add_(x, y, x, y)
        for i in range(2, n):
            x_, y_ = self.ecc_point_add_(x_, y_, x, y)
        return x_, y_

    def ecc_point_np(self, n, point):
        return self.ecc_point_np_(n, point[0], point[1])

    def _ecc_all_points(self):
        return [(x, y) for x in range(self.p) for y in range(self.p) if
                (y ** 2 - (x ** 3 + self.a * x + self.b)) % self.p == 0]

    def ecc_points_2d(self, row_size=10, padding=''):
        res = []
        for i in range(len(self.points) // row_size):
            res.append(self.points[i * row_size: (i + 1) * row_size])
        if len(self.points) % row_size != 0:
            r = (len(self.points) % row_size)
            res.append(self.points[- r:] + [padding] * (row_size - r))
        return res

    def ecc_points_print(self, row_size=10, padding=''):
        df = pd.DataFrame(self.ecc_points_2d(row_size, padding))
        print(f'\n{df}\n')

    def _set_order(self):
        x, y = self.G[0], (-1 * self.G[1]) % self.p
        x_, y_ = self.G[0], self.G[1]
        self.n = 1
        while True:
            self.n += 1
            x_, y_ = self.ecc_point_add((x_, y_), self.G)
            if x_ == x and y_ == y:
                return

    def _ec_elgamal_generator(self, sk=None, pk=None):
        self.sk = random.randrange(1, self.n) if sk is None else sk
        self.pk = self.ecc_point_np(self.sk, self.G) if pk is None else pk
        print(f'G = {self.G} \t n = {self.n} \t sk = {self.sk} \t pk = {self.pk}\n')

    def ec_elgamal_encrypt(self, m, r=None):
        r = random.randrange(1, self.n) if r is None else r
        C1 = self.ecc_point_np(r, self.G)
        mG = m if isinstance(m, tuple) else self.ecc_point_np(m, self.G)
        C2 = self.ecc_point_add(mG, self.ecc_point_np(r, self.pk))
        print(f'mG: {mG} \t C1: {C1} \t C2: {C2}')
        return C1, C2

    def ec_elgamal_decrypt(self, C1, C2):
        xC1 = self.ecc_point_np(self.sk, C1)
        mG = self.ecc_point_sub(C2, xC1)
        return self._table[mG]


if __name__ == '__main__':
    # ec = EccCurve(11, 1, 6)
    ec = EccCurve(37, 2, 5)
    p1, p2 = ec.points[1], ec.points[3]
    add_res = ec.ecc_point_add(p1, p2)
    _2p_res = ec.ecc_point_np(2, p1)
    print(f'{p1} + {p2} = ', add_res)
    for i in range(2, 21):
        print(f'{i}{p1} =', ec.ecc_point_np(i, p1))

    m = 9
    print(f'\nm: {m}')
    C1, C2 = ec.ec_elgamal_encrypt(m)
    decrpted_m = ec.ec_elgamal_decrypt(C1, C2)
    print(f'decrpted_m: {decrpted_m}')
