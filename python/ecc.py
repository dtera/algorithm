# coding:utf-8
"""
Elliptic Curve Cryptography
@author dterazhao
@date 2024/1/3
"""
import random
from math import gcd

import pandas as pd

pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', 20)
pd.set_option('display.width', 1000)


# noinspection PyPep8Naming
class ECC:
    def __init__(self, p, a, b, G=None, sk=None, pk=None, show_points=True):
        if (4 * (a ** 3) + 27 * (b ** 2)) % p == 0:
            print(f"({p}, {a}, {b}) can not be used for ecc\n")
        self.p = p
        self.a = a
        self.b = b
        self.points = self._ecc_all_points()
        if show_points:
            self.ecc_points_print()
        self.size = len(self.points)
        if G is None:
            self.G = self.points[random.randrange(1, self.size)]
            self._set_order()
            while self.n != self.size:
                self.G = self.points[random.randrange(1, self.size)]
                self._set_order()
        else:
            self.G = G
            self._set_order()

        # self.n = len(self.points)
        self._table = {}
        self._np_table = {1: self.G}
        for i in range(2, self.n + 1):
            np = self.ecc_point_add(self._np_table[i - 1], self.G)
            self._np_table[i] = np
            self._table[np] = i
        self._np_table.pop(1)
        self._ec_elgamal_generator(sk, pk)

    def mod_inverse(self, x):
        for i in range(1, self.p):
            if (i * x) % self.p == 1:
                return i
        return -1

    def _eval_lambda(self, x1, y1, x2, y2):
        sign = 1
        if x1 == x2 and y1 == y2:
            numerator = (3 * (x1 ** 2) + self.a)
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
        x_, y_ = x, y
        for i in range(1, n):
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
        self.sk = random.randrange(2, self.n) if sk is None else sk
        # self.pk = self.ecc_point_np(self.sk, self.G) if pk is None else pk
        self.pk = self._np_table[self.sk] if pk is None else pk
        print(f'G = {self.G} \t n = {self.n} \t sk = {self.sk} \t pk = {self.pk}\n')

    def ec_elgamal_encrypt(self, m, lookup=True, r=None):
        r = random.randrange(2, self.n) if r is None else r
        C1 = self._np_table[r]  # self.ecc_point_np(r, self.G)
        mG = m if isinstance(m, tuple) else self._np_table[m]  # self.ecc_point_np(m, self.G)
        rPK = self.ecc_point_np(r, self.pk)
        C2 = self.ecc_point_add(mG, rPK) if lookup else m * rPK[0]
        print(f'r: {r} \t mG: {mG} \t rPK: {rPK} \t C1: {C1} \t C2: {C2}')
        return C1, C2

    def ec_elgamal_decrypt(self, C1, C2, m=None, lookup=True):
        xC1 = self.ecc_point_np(self.sk, C1)
        mG = self.ecc_point_sub(C2, xC1) if lookup else (C2 * self.mod_inverse(xC1[0]) % self.p)
        decrpted_m = self._table[mG] if lookup else mG
        if m is not None and decrpted_m != m:
            print(f'{decrpted_m} != {m}')
        print(f'm: {decrpted_m} \t mG: {mG} \t xC1: {xC1} \t C1: {C1} \t C2: {C2}')
        return decrpted_m


if __name__ == '__main__':
    # ecc = ECC(23, 1, 1, (3, 10), 11)
    ecc = ECC(37, 2, 5)
    p1, p2 = ecc.points[17], ecc.points[3]
    add_res = ecc.ecc_point_add(p1, p2)
    print(f'{p1} + {p2} = ', add_res)
    for i in range(2, ecc.n):
        np = ecc.ecc_point_np(i, ecc.G)
        if np in ecc.points:
            print(f'{i}{ecc.G} = {np}')
        else:
            print(f'##{i}{ecc.G} = {np}')

    m = 9
    lookup = True
    print(f'\nm: {m}')
    r = 22
    C1, C2 = ecc.ec_elgamal_encrypt(m, lookup, r)
    decrypted_m = ecc.ec_elgamal_decrypt(C1, C2, m, lookup)
    print(f'{ecc.sk}{ecc.G} = ', ecc.ecc_point_np(ecc.sk, ecc.G))
    print(f'{r * ecc.sk}{ecc.G} = ', ecc.ecc_point_np(r * ecc.sk, ecc.G))
    print(f'{r} * {ecc.sk}{ecc.G} = ', ecc.ecc_point_np(r, ecc.ecc_point_np(ecc.sk, ecc.G)))
    print(f'{ecc.sk} * {r}{ecc.G} = ', ecc.ecc_point_np(ecc.sk, ecc.ecc_point_np(r, ecc.G)))
    print(f'\n{25}(6, 23) = ', ecc.ecc_point_np(25, (6, 23)))
    print(f'{22}(6, 23) = ', ecc.ecc_point_np(22, (6, 23)))
    print(f'{22 * 25}(6, 23) = ', ecc.ecc_point_np(22 * 25, (6, 23)))
    print(f'{22} * {25}(6, 23) = ', ecc.ecc_point_np(22, ecc.ecc_point_np(25, (6, 23))))
    print(f'{25} * {22}(6, 23) = ', ecc.ecc_point_np(25, ecc.ecc_point_np(22, (6, 23))))
