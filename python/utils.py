# coding:utf-8
import json
import logging
import time

import requests


def read_line(f_name):
    with open(f_name, "r") as f:
        return f.readline()


def print_time(f):
    def inner(*arg, **kwarg):
        s_time = time.time()
        res = f(*arg, **kwarg)
        e_time = time.time()
        print(f'{f.__name__}调用耗时：{e_time - s_time}秒')
        return res

    return inner


def json_format(s):
    return json.dumps(s, sort_keys=True, indent=2)


def is_empty(a):
    return a is None or len(a.strip() if type(a) == str else a) == 0


def is_not_empty(a):
    return not is_empty(a)


def http_req(url: str, payload: dict = None, method: str = "get", api_key: str = None, headers: dict = None,
             out_format: str = 'json', timeout: int = 300, max_retry=0, retry_interval=5, stream=False):
    _headers = {
        "Content-Type": "application/json"
    }
    if is_not_empty(api_key):
        _headers.update({"Authorization": f"Bearer {api_key}"})
    if headers is not None:
        _headers.update(headers)

    tries = 0
    resp = {"errcode": -1, 'errmsg': None, 'data': None}
    retry_interval = max(retry_interval, 5)
    while tries <= max_retry:
        tries += 1
        if tries > 1:
            time.sleep(retry_interval)
        response = requests.get(url, headers=_headers, timeout=timeout,
                                stream=stream) if payload is None and method == "get" else (
            requests.post(url, headers=_headers, json=payload, timeout=timeout, stream=stream))
        if response.status_code == 200:
            out_format = "raw" if stream else out_format
            return response.json() if out_format == "json" else (response.text if out_format == "text" else response)
        else:
            logging.warning(f"ERROR status_code={response.status_code}: RESPONSE = {response.text}")
            resp["errcode"] = response.status_code
            resp["errmsg"] = response.reason
            # raise ValueError(f"Error response with http_code={http_code}: RESPONSE = {response.text}.")
    return resp


if __name__ == "__main__":
    print(is_empty('    '))
    print(is_empty(' aaa'))
    print(is_not_empty('xxx'))
    res = http_req("https://gateway.53zaixian.com/resource-bs-api/res/home/findLearningPeriodGrade", method="post")
    print(json_format(res))
