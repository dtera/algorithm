# coding:utf-8
"""
libsvm to csv
"""
import argparse
import numpy as np
import pandas as pd
import os


def set_sample_ids_and_labels(f_path, mns, mxs, sizes, sample_ids, labels, has_label, is_neg_to_zero):
    with (open(f_path, 'rt') as f):
        line = f.readline()
        while line.lstrip().startswith('#'):
            line = f.readline()
        tokens = line.split()
        label_index = 0 if ':' in tokens[1] else 1
        has_sample_id = False if ':' in tokens[0] or (has_label and label_index == 0) else True
        mn = int(tokens[label_index + 1][:tokens[label_index + 1].index(':')])
        mx = int(tokens[-1][:tokens[-1].index(':')])
        f.seek(0)
        size = 0
        for line in f:
            if not (line.lstrip().startswith('#') or line.strip() == ''):
                tokens = line.split()
                if has_sample_id:
                    sample_ids.append(tokens[0])
                if has_label:
                    label = int(tokens[label_index])
                    labels.append(0 if is_neg_to_zero and label < 0 else label)
                t1 = int(tokens[label_index + 1][:tokens[label_index + 1].index(':')])
                t2 = int(tokens[-1][:tokens[-1].index(':')])
                if t1 < mn:
                    mn = t1
                if t2 > mx:
                    mx = t2
                # if size % 1000 == 0:
                #     print(size, '\t', labels[size], '\t', tokens)
                size += 1
        mns.append(mn)
        mxs.append(mx)
        sizes.append(size)
        print(f'f_path: {f_path} \t size: {size} \t min_index: {mn} \t max_index: {mx}')
        return label_index


def set_feats(f_path, feats, mn, offset, feat_offset):
    with (open(f_path, 'rt') as f):
        i = 0
        for line in f:
            if not (line.lstrip().startswith('#') or line.strip() == ''):
                kvs = line.split()[feat_offset:]
                for kv in kvs:
                    index_value = kv.split(':')
                    feats[offset + i, int(index_value[0]) - mn] = float(index_value[1])
                i += 1


def df2csv(sample_ids, labels, feats, feat_names, out_path, is_label_before, label_name='label', id_name='sample_id'):
    label_df = pd.DataFrame(labels, columns=[label_name])
    feats_df = pd.DataFrame(feats, columns=feat_names)
    if len(labels) != 0:
        sample_df = [label_df, feats_df] if is_label_before else [feats_df, label_df]
    else:
        sample_df = [feats_df]
    if len(sample_ids) != 0:
        sample_df.insert(0, pd.DataFrame(sample_ids, columns=[id_name]))
    df = pd.concat(sample_df, axis=1)
    df.to_csv(out_path, index=False)
    print(df)


def libsvm2csv(p, in_path, out_path):
    label_index = 0
    mns, mxs, sizes, sample_ids, labels = [], [], [], [], []
    if not (out_path.strip() == '' or os.path.exists(out_path)):
        if out_path.endswith('.csv'):
            dir_name = os.path.dirname(out_path)
            if not os.path.exists(dir_name):
                os.makedirs(dir_name)
        else:
            os.makedirs(out_path)
    if os.path.isdir(in_path):
        for root, dirs, files in os.walk(in_path):
            fs = sorted(filter(lambda f: not f.endswith('.csv'), files))
            # prepare min and max index and add sample_id and label
            for file in fs:
                f_path = os.path.join(root, file)
                label_index = set_sample_ids_and_labels(f_path, mns, mxs, sizes, sample_ids, labels, p.has_label,
                                                        p.is_neg_to_zero)
            # libsvm2csv
            mx, mn, size = max(mxs), min(mns), sum(sizes)
            feat_size = mx - mn + 1
            feat_names = [f'f_{i}' for i in range(feat_size)]
            feats = np.zeros((size, feat_size), dtype=np.float32)
            for i, file in enumerate(fs):
                f_path = os.path.join(root, file)
                offset = sum(sizes[:i])
                feat_offset = label_index + (0 if len(sample_ids) == 0 and len(labels) == 0 else 1)
                set_feats(f_path, feats, mn, offset, feat_offset)
            if p.is_out_merged or out_path.endswith('.csv'):
                o_path = out_path if out_path.strip() != '' else f'{in_path}.csv'
                df2csv(sample_ids, labels, feats, feat_names, o_path, p.is_label_before)
            else:
                for i, file in enumerate(fs):
                    o_path = os.path.join(out_path if out_path.strip() != '' else root, f'{file}.csv')
                    offset = sum(sizes[:i])
                    r = offset + sizes[i]
                    ids = [] if len(sample_ids) == 0 else sample_ids[offset:r]
                    df2csv(ids, labels[offset:r], feats[offset:r], feat_names, o_path, p.is_label_before)
    else:
        label_index = set_sample_ids_and_labels(in_path, mns, mxs, sizes, sample_ids, labels, p.has_label,
                                                p.is_neg_to_zero)
        feat_size = mxs[0] - mns[0] + 1
        feat_names = [f'f_{i}' for i in range(feat_size)]
        feats = np.zeros((sizes[0], feat_size), dtype=np.float32)
        feat_offset = label_index + (0 if len(sample_ids) == 0 and len(labels) == 0 else 1)
        set_feats(in_path, feats, mns[0], 0, feat_offset)
        o_path = out_path if out_path.strip() != '' else f'{in_path}.csv'
        df2csv(sample_ids, labels, feats, feat_names, o_path, p.is_label_before)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--in_path", type=str, default='../data/a9a', help="input data path.")
    parser.add_argument("--out_path", type=str, default='', help="output data path.")
    parser.add_argument("--is_neg_to_zero", type=int, default=1, help="whether to convert -1 to 0.")
    parser.add_argument("--is_label_before", type=int, default=1, help="whether label is before feats.")
    parser.add_argument("--is_out_merged", type=int, default=1, help="whether output is merged.")
    parser.add_argument("--has_label", type=int, default=1, help="whether has label.")
    p = parser.parse_args()
    libsvm2csv(p, p.in_path, p.out_path)
