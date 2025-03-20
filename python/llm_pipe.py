# coding:utf-8
"""
LLM pipeline
"""
import json
import os

import yaml
from transformers import AutoModelForCausalLM, AutoTokenizer


def load_conf(f_path="config.yml"):
    config = {}
    if os.path.exists(f_path):
        with open(f_path) as f:
            config = json.load(f) if f_path.endswith(".json") else yaml.load(f, Loader=yaml.FullLoader)
    return config


if __name__ == '__main__':
    conf = load_conf()

    model = AutoModelForCausalLM.from_pretrained(
        conf["model"],
        torch_dtype="auto",
        device_map="auto"
    )
    tokenizer = AutoTokenizer.from_pretrained(conf["model"])

    messages = [
        {"role": "user", "content": conf["prompt"]},
    ]
    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )
    model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

    generated_ids = model.generate(
        **model_inputs,
        max_new_tokens=32768
    )
    generated_ids = [
        output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
    ]

    response = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]
    print(response)
