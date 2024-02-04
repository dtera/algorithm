#!/bin/bash

if [ "$1" = "-fs" ]; then
    shift
    for f in "$@"
    do
        pdftops -eps "$f"
    done
    exit 1
fi

base_dir="."
if [ "$1" != "" ]; then
    base_dir=$1
fi

if [ ! -d "$base_dir" ]; then
    echo "$base_dir" is not a directory.
    exit 1
fi

for f in "$base_dir"/*.pdf
do 
    pdftops -eps "$f"
done
