import os
import sys
import json
import requests

body = {
    'names': [],
    'region': 'fra1',
    'size': 's-1vcpu-1gb',
    'image': '31529477',
    'ssh_keys': [14240122, 18094769],
    'backups': False,
    'ipv6': False,
    'private_networking': True,
    'tags': ['akka-node']
}

print(sys.argv)
token = os.environ.get('TOKEN')
num_nodes = int(sys.argv[1])
start_index = int(sys.argv[2])
assert num_nodes > 0
assert token
assert start_index >= 0
print('Num of nodes: {}, Start Index: {}'.format(num_nodes, start_index))

for i in range(start_index, start_index + num_nodes):
    name = 'akka-node-{}'.format(i)
    body['names'].append(name)

print(body)
headers = {'Content-Type': 'application/json', 'Authorization': 'Bearer {}'.format(token)}
response = requests.post('https://api.digitalocean.com/v2/droplets', data=json.dumps(body), headers=headers)
print(response.json())
