import re

import pandas as pd

# parses all needed values to groups, except Client logs
one_regex_to_rule_them_all =\
    '(?P<timestamp>\d{1,2}:\d{1,2}:\d{1,2}[,]?\d{1,3}) (?P<node_type>\w+):\d* - Event:(?P<event_type>\w+).*/user/consensus-\w+-(?P<community_id>\d+).*\((sequence=(?P<sequence_number>\d*)|transaction).*contentHash=(?P<content_hash>\w+)\).*identity=(?P<node_id>[^\|^,)]*)'

def parse_log_line(line):
    match = re.search(one_regex_to_rule_them_all, line)
    if match:
        return match.groupdict()
    return


def parse_log(log_file):
    rows = []
    for line in log_file:
        if line:
            parsed_line = parse_log_line(line)
            if parsed_line:
                rows.append(parsed_line)
    return rows


def build_df(file_path):
    with open(file_path, 'r') as log_file:
        dataframe = pd.DataFrame(parse_log(log_file))
        return dataframe
