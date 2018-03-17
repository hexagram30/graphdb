#!/bin/bash

docker create \
  --name hexagram-orientdb \
  -p 2424:2424 \
  -p 2480:2480 \
  -v `pwd`/data/backup:/orientdb/backup \
  -v `pwd`/data/databases:/orientdb/databases \
  -e ORIENTDB_ROOT_PASSWORD=root \
  orientdb:2.2.33

docker stop hexagram-orientdb
