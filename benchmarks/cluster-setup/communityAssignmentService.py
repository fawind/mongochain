from flask import Flask, request, jsonify
import json
app = Flask(__name__)

COMMUNITY_COUNT = 2
primaries = [True for _ in range(COMMUNITY_COUNT)]
counter = 0
members = []


@app.route("/join")
def get_state():
    global counter
    global members
    community_id = counter % COMMUNITY_COUNT
    is_primary = primaries[community_id]
    primaries[community_id] = False
    counter += 1
    members.append({'ip': request.remote_addr, 'community_id': community_id,
                    'isPrimary': is_primary})
    return '{},{}'.format(community_id, json.dumps(is_primary))


@app.route("/members")
def get_members():
    return jsonify(members), 200

@app.route("/members/count")
def get_members():
    return len(members), 200

@app.route("/clear")
def clear():
    global counter
    global members
    global primaries
    primaries = [True for _ in range(COMMUNITY_COUNT)]
    counter = 0
    members = []
    return 'cleared', 200
