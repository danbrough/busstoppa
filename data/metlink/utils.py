#!/usr/bin/env python


import json
import requests
import sys


def dump_json(url):

    resp = requests.get(url)

    for header in resp.headers:
        sys.stderr.write("%s : %s\n" % (header, resp.headers[header]))

    if len(sys.argv) > 1:
        output = open(sys.argv[1],'w')
    else:
        output = sys.stdout

    output.write(json.dumps(resp.json(), indent=2))
    output.flush()


