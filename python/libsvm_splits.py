# coding:utf-8
"""
libsvm splits
"""
import argparse
import os


def libsvm_splits(p, in_path, out_path):
    guest_data, host_data = [], []
    with (open(in_path, 'rt') as f):
        for line in f:
            if not (line.lstrip().startswith('#') or line.strip() == ''):
                tokens = line.split()
                split_index = 1
                for i, kv in enumerate(tokens[1:]):
                    feat_index = int(kv.split(':')[0])
                    if feat_index > p.guest_feat_size:
                        split_index = i + 1
                        break
                guest_data.append(' '.join(tokens[:split_index]))
                host_data.append(' '.join(tokens[split_index:]))
    if not (out_path.strip() == '' or os.path.exists(out_path)):
        os.makedirs(out_path)
    in_file_name = in_path.split('/')[-1]
    guest_out_path = os.path.join(out_path, f'{in_file_name}.guest')
    host_out_path = os.path.join(out_path, f'{in_file_name}.host')
    print(guest_out_path, host_out_path)
    with (open(guest_out_path, 'wt') as f):
        for i, line in enumerate(guest_data):
            f.write(f'{(str(i) + " ") if p.is_out_example_id else ""}{line}\n')
    with (open(host_out_path, 'wt') as f):
        for i, line in enumerate(host_data):
            f.write(f'{(str(i) + " ") if p.is_out_example_id else ""}{line}\n')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--in_path", type=str, default='../data/a9a.train', help="input data path.")
    parser.add_argument("--out_path", type=str, default='../out', help="output data path.")
    parser.add_argument("--guest_feat_size", type=int, default=10, help="feat size of guest.")
    parser.add_argument("--is_out_example_id", type=int, default=1, help="whether out example id.")
    p = parser.parse_args()
    libsvm_splits(p, p.in_path, p.out_path)
