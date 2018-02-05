import os
import sys
import json
import requests

body = {
    'names': [],
    'region': 'fra1',
    'size': 's-1vcpu-1gb',
    'image': '31528203',
    'ssh_keys': [14240122, 18094769],
    'backups': False,
    'ipv6': False,
    'private_networking': True,
    'tags': ['akka-node']
}

token = os.environ.get('TOKEN')
num_nodes = int(sys.argv[1])
assert num_nodes > 0
assert token
print('Num of nodes {}'.format(num_nodes))

for i in range(num_nodes):
    name = 'akka-node-{}'.format(i)
    body['names'].append(name)

print(body)
headers = {'Content-Type': 'application/json', 'Authorization': 'Bearer {}'.format(token)}
response = requests.post('https://api.digitalocean.com/v2/droplets', data=json.dumps(body), headers=headers)
print(response.json())
