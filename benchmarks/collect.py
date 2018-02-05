import requests
import os
from os.path import dirname, realpath


URL = 'http://139.59.134.185:5000/members'
OUTPUT_DIR = dirname(realpath(__file__)) + '/results/'
REMOTE_PATH = '~/service.log'
FILE_NAME = '{}.log'


def main():
    members = [m for m in requests.get(URL).json() if m['community_id'] != 0]
    for mem in members:
        copy_file(mem['ip'])


def copy_file(ip):
    print(ip)
    file_name = FILE_NAME.format(ip)
    output_dir = OUTPUT_DIR + file_name
    scp_stmnt = 'scp akka@{}:{} {}'.format(ip, REMOTE_PATH, output_dir)
    os.system(scp_stmnt)


if __name__ == '__main__':
    main()
