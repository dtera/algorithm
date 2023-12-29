# coding:utf-8
"""
Elliptic Curve Cryptography
"""
from math import gcd

import pandas as pd

pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', 20)
pd.set_option('display.width', 1000)


def mod_inverse(p, x):
    for i in range(1, p):
        if (i * x) % p == 1:
            return i
    return -1


def eval_lambda(p, a, x1, y1, x2, y2):
    sign = 1
    if x1 == x2 and y1 == y2:
        numerator = (3 * x1 ** 2 + a)
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
    denominator_ = mod_inverse(p, denominator)
    lambda_ = numerator * denominator_ * sign
    lambda_ %= p
    return lambda_


def ecc_point_add_(p, a, x1, y1, x2, y2):
    lambda_ = eval_lambda(p, a, x1, y1, x2, y2)
    x3 = (lambda_ ** 2 - x1 - x2) % p
    y3 = (lambda_ * (x1 - x3) - y1) % p
    return x3, y3


def ecc_point_add(p, a, p1, p2):
    return ecc_point_add_(p, a, p1[0], p1[1], p2[0], p2[1])


def ecc_point_np_(n, p, a, x, y):
    x_, y_ = ecc_point_add_(p, a, x, y, x, y)
    for i in range(2, n):
        x_, y_ = ecc_point_add_(p, a, x_, y_, x, y)
    return x_, y_


def ecc_point_np(n, p, a, point):
    return ecc_point_np_(n, p, a, point[0], point[1])


def ecc_all_points(p, a, b):
    return [(x, y) for x in range(p) for y in range(p) if (y ** 2 - (x ** 3 + a * x + b)) % p == 0]


def ecc_points_2d(ps, row_size=10, padding=''):
    res = []
    for i in range(len(ps) // row_size):
        res.append(ps[i * row_size: (i + 1) * row_size])
    if len(ps) % row_size != 0:
        r = (len(ps) % row_size)
        res.append(ps[- r:] + [padding] * (row_size - r))
    return res


def ecc_points_print(ps, row_size=10, padding=''):
    df = pd.DataFrame(ecc_points_2d(ps, row_size, padding))
    print(f'\n{df}\n')


if __name__ == '__main__':
    p, a, b = 37, 2, 5
    ps = ecc_all_points(p, a, b)
    p1, p2 = ps[1], ps[3]
    ecc_points_print(ps)
    add_res = ecc_point_add(p, a, p1, p2)
    _2p_res = ecc_point_np(2, p, a, p1)
    print(f'{p1} + {p2} = ', add_res)
    for i in range(2, 21):
        print(f'{i}{p1} =', ecc_point_np(i, p, a, p1))
