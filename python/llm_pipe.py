# coding:utf-8
"""
LLM pipeline
"""
import json
import os

import yaml
from transformers import pipeline


def load_conf(f_path="config.yml"):
    config = {}
    if os.path.exists(f_path):
        with open(f_path) as f:
            config = json.load(f) if f_path.endswith(".json") else yaml.load(f, Loader=yaml.FullLoader)
    return config


if __name__ == '__main__':
    conf = load_conf()

    messages = [
        {"role": "user", "content": conf["prompt"]},
    ]
    pipe = pipeline(conf["task"], model=conf["model"])
    resp = pipe(messages)
    for chunk in resp:
        print(chunk["generated_text"][1]["content"])
