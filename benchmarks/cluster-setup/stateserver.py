from flask import Flask
app = Flask(__name__)

COUNTER = 0
COMMUNITIES = 2
PRIMARIES = [True for _ in range(COMMUNITIES)]


@app.route("/getState")
def get_state():
    global COUNTER
    COUNTER += 1
    return_template = '{}, {}'
    community = COUNTER % COMMUNITIES
    primary = PRIMARIES[community]
    PRIMARIES[community] = PRIMARIES[community] and False
    return return_template.format(community, primary)
