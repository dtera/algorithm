# coding:utf-8

import logging
import time

import requests


def print_time(f):
    def inner(*arg, **kwarg):
        s_time = time.time()
        res = f(*arg, **kwarg)
        e_time = time.time()
        print(f'{f.__name__}调用耗时：{e_time - s_time}秒')
        return res

    return inner


def is_empty(a):
    return a is None or len(a.strip() if type(a) == str else a) == 0


def is_not_empty(a):
    return not is_empty(a)


def http_req(url: str, payload: dict = None, api_key: str = None, headers: dict = None, out_format: str = 'json',
             timeout: int = 300):
    _headers = {
        "Content-Type": "application/json"
    }
    if is_not_empty(api_key):
        _headers.update({"Authorization": f"Bearer {api_key}"})
    if headers is not None:
        _headers.update(headers)

    response = requests.get(url, headers=_headers, timeout=timeout) if payload is None else (
        requests.post(url, headers=_headers, json=payload, timeout=timeout))
    http_code = response.status_code
    if http_code != 200:
        logging.error(f"ERROR http_code={http_code}: RESPONSE = {response.text}")
        raise ValueError(f"Error response with http_code={http_code}: RESPONSE = {response.text}.")
    return response.json() if out_format == "json" else response.text


if __name__ == "__main__":
    print(is_empty('    '))
    print(is_empty(' aaa'))
    print(is_not_empty('xxx'))
    print()
    print(http_req('https://www.baidu.com', out_format='text'))
