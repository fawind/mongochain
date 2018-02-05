from flask import Flask, request, jsonify
app = Flask(__name__)

COMMUNITIES = 2
PRIMARIES = [True for _ in range(COMMUNITIES)]
counter = 0
members = set()


@app.route("/getState")
def get_state():
    global counter
    global members
    members.add(request.remote_addr)
    counter += 1
    return_template = '{}, {}'
    community = counter % COMMUNITIES
    primary = PRIMARIES[community]
    PRIMARIES[community] = False
    return return_template.format(community, primary)


@app.route("/getMembers")
def get_members():
    return jsonify(list(members)), 200
