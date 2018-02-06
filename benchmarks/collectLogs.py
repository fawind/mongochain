import requests
import os
from os.path import dirname, realpath


URL = 'http://139.59.134.185:5000/members'
OUTPUT_DIR = dirname(realpath(__file__)) + '/logs/'
REMOTE_PATH = '/home/akka/mongochain.log'
FILE_NAME = '{}.log'


def main():
    members = [m for m in requests.get(URL).json()]
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
