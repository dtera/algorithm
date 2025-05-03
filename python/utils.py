# coding:utf-8
import json
import logging
import re

import time

import requests

import os
import img2pdf


def read_line(f_name):
    with open(f_name, "r") as f:
        return f.readline()


def print_time(f):
    def inner(*arg, **kwarg):
        s_time = time.time()
        result = f(*arg, **kwarg)
        e_time = time.time()
        print(f'{f.__name__}调用耗时：{e_time - s_time}秒')
        return result

    return inner


def json_format(s):
    return json.dumps(s, sort_keys=True, indent=2)


def is_empty(a):
    return a is None or len(a.strip() if type(a) == str else a) == 0


def is_not_empty(a):
    return not is_empty(a)


# noinspection PyBroadException
def http_req(url: str, payload: dict = None, method: str = "get", api_key: str = None, headers: dict = None,
             out_format: str = 'json', timeout: int = 300, max_retry=0, retry_interval=5, stream=False,
             save_path=None, chunk_size=128):
    _headers = {
        "Content-Type": "application/json"
    }
    if is_not_empty(api_key):
        _headers.update({"Authorization": f"Bearer {api_key}"})
    if headers is not None:
        _headers.update(headers)

    tries = 0
    resp = {"errcode": -1, "errmsg": "", "data": None}
    retry_interval = max(retry_interval, 5)
    while tries <= max_retry:
        try:
            tries += 1
            if tries > 1:
                time.sleep(retry_interval)
            if save_path:
                download_url(url, save_path, chunk_size=chunk_size)
                return {"errcode": 200, "errmsg": "download sucess", "data": save_path}

            response = requests.get(url, headers=_headers, timeout=timeout,
                                    stream=stream) if payload is None and method == "get" else (
                requests.post(url, headers=_headers, json=payload, timeout=timeout, stream=stream))
            if response.status_code == 200:
                out_format = "raw" if stream else out_format
                return response.json() if out_format == "json" else (
                    response.text if out_format == "text" else response)
            else:
                logging.warning(f"ERROR status_code={response.status_code}: RESPONSE = {response.text}")
                resp["errcode"] = response.status_code
                resp["errmsg"] = response.reason
                # raise ValueError(f"Error response with http_code={http_code}: RESPONSE = {response.text}.")
        except Exception:
            pass
    return resp


def download_url(url, save_path, chunk_size=128):
    r = requests.get(url, stream=True)
    with open(save_path, 'wb') as fd:
        for chunk in r.iter_content(chunk_size=chunk_size):
            fd.write(chunk)


def imgs_to_pdf(img_dir, out_pdf_path=None, suffix=".jpg", sort=True):
    img_dir = img_dir.rstrip("/")
    if out_pdf_path is None:
        out_pdf_path = os.path.join(img_dir, f"{re.sub(r".*/", "", img_dir)}.pdf")
    with open(out_pdf_path, "wb") as f:
        imgs = [f"{img_dir}/{i}" for i in os.listdir(img_dir) if i.endswith(suffix)]
        f.write(img2pdf.convert(sorted(imgs) if sort else imgs))


if __name__ == "__main__":
    print(is_empty('    '))
    print(is_empty(' aaa'))
    print(is_not_empty('xxx'))
    res = http_req("https://gateway.53zaixian.com/resource-bs-api/res/home/findLearningPeriodGrade", method="post")
    print(json_format(res))
