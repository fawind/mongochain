import random
import string
import subprocess
import sys
from time import sleep

import requests


def api_url(port, key, value):
    return f'http://localhost:{port}/store/api/store/namespace1/{key}/{value}'


def get_random_string():
    return ''.join([random.choice(string.ascii_letters + string.digits) for n in range(16)])


def run_tests(ports, wait_single=0.0, wait_run=0.0):
    number_of_puts = 0
    try:
        while True:
            for port in ports:
                key = get_random_string()
                value = get_random_string()
                url = api_url(port, key, value)
                print(f'Call {url}')
                r = requests.get(url)
                number_of_puts += 1
                print(f'Sleep between requests for {wait_single} sec...')
                sleep(wait_single)
            print(f'Sleep between runs for {wait_run} sec...\n')
            sleep(wait_run)
    except KeyboardInterrupt:
        print(f'Finished. Total number of puts: {number_of_puts}')


def get_docker_ports():
    ports = []
    port_command = 'docker ps --filter expose=9090 --format "{{.Ports}}"'
    result = subprocess.run(port_command.split(), stdout=subprocess.PIPE)
    for line in result.stdout.decode('utf-8').split('\n'):
        if line == '':
            continue

        start = line.index(':') + 1
        end = line.index('-')
        ports.append(line[start:end])
    return ports


if __name__ == '__main__':
    node_ports = get_docker_ports()
    print(f'Send requests to enpoints: {node_ports}')
    if len(sys.argv) == 3:
        run_tests(node_ports, float(sys.argv[1]), float(sys.argv[2]))
    else:
        run_tests(node_ports, 1, 5)
