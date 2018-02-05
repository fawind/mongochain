import random
import string
import subprocess
import sys
from time import sleep

import requests

URL = 'http://139.59.134.185:5000/members'
# URL = 'http://localhost:5000/members'


def api_url(host, key, value):
    return f'http://{host}:9090/store/api/store/namespace1/{key}/{value}'


def get_random_string():
    return ''.join([random.choice(string.ascii_letters + string.digits)
                    for n in range(16)])


def run_tests(members, wait_single=0, wait_run=0):
    number_of_puts = 0
    try:
        while True:
            for host in members:
                key = get_random_string()
                value = get_random_string()
                url = api_url(host, key, value)
                print(f'Call {url}')
                r = requests.get(url)
                number_of_puts += 1
                print(f'Sleep between requests for {wait_single} sec...')
                sleep(wait_single)
            print(f'Sleep between runs for {wait_run} sec...\n')
            sleep(wait_run)
    except KeyboardInterrupt:
        print(f'Finished. Total number of puts: {number_of_puts}')

if __name__ == '__main__':
    members = [m['ip'] for m in requests.get(URL).json()
               if m['community_id'] != 0]
    print(f'Send requests to enpoints: {members}')
    if len(sys.argv) == 3:
        run_tests(members, float(sys.argv[1]), float(sys.argv[2]))
    else:
        run_tests(members, 1, 5)
