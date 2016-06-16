#This script is used to build mongodb from dump file.
from pymongo import MongoClient

client = MongoClient('localhost', 27017)

print(client.database_names())

db_name = 'test'
urls_collection_name = 'mytest'
hosts_collection_name = ''
domains_collection_name = ''

nutch_base_dir = '/Volumes/MySeagateDisk/nutch/'

db = client[db_name]
urls_collection = db[urls_collection_name]
urls_collection.create_index('url')


def process_scores_dump_line(line, db_collection):
    data = line.split()
    if len(data) == 2:
        result = db_collection.update_one({'url': data[0]}, {'$set': {'score': data[1]}}, upsert=True)
    else:
        print ("ERROR: " + line)
        return


def process_inlinks_dump_line(line, db_collection):
    data = line.split()
    if len(data) == 2:
        db_collection.update_one({'url': data[0]}, {'$set': {'inlinks': data[1]}}, upsert=True)
    else:
        return


def process_outlinks_dump_line(line, db_collection):
    data = line.split()
    if len(data) == 2:
        db_collection.update_one({'url': data[0]}, {'$set': {'outlinks': data[1]}}, upsert=True)
    else:
        return


def process_hosts_dump_line(line, db_collection) :
    data = line.split()
    if len(data) == 2 :
        db_collection.update_one({'host' : data[1]}, {'$set' : {'stats' : data[0]}}, upsert=True)
    else:
        return


def process_domains_dump_line(line, db_collection) :
    data = line.split()
    if len(data) == 2 :
        db_collection.update_one({'domain' : data[1]}, {'$set' : {'stats' : data[0]}}, upsert=True)
    else:
        return


def process_dump(dumpFile, dumpFileType, db_collection):
    print ("processing dump file: " + dumpFile + " type: " + dumpFileType)
    line_num = 1
    with open(dumpFile) as infile:
        for line in infile:
            if dumpFileType is 'scores':
                process_scores_dump_line(line, db_collection)
            elif dumpFileType is 'inlinks':
                process_inlinks_dump_line(line, db_collection)
            elif dumpFileType is 'outlinks':
                process_outlinks_dump_line(line, db_collection)
            elif dumpFileType is 'hosts' :
                process_hosts_dump_line(line, db_collection)
            elif dumpFileType is 'domains' :
                process_domains_dump_line(line, db_collection)
            else:
                print("Dump file type is not supported")
                return
            line_num += 1


def process_scores_dump(dumpFile, db_collection):
    process_dump(dumpFile, 'scores', db_collection)


def process_inlinks_dump(dumpFile, db_collection):
    process_dump(dumpFile, 'inlinks', db_collection)


def process_outlinks_dump(dumpFile, db_collection):
    process_dump(dumpFile, 'outlinks', db_collection)


def process_hosts_dump(dumpFile, db_collection) :
    process_dump(dumpFile, 'hosts', db_collection)


def process_domains_dump(dumpFile, db_collection) :
    process_dump(dumpFile, 'domains', db_collection)


#scores dump first.
scores_dump_path = nutch_base_dir + 'dump/nodes/scores/part-00000'
inlinks_dump_path = nutch_base_dir + 'dump/nodes/inlinks/part-00000'
outlinks_dump_path = nutch_base_dir + 'dump/nodes/outlinks/part-00000'

print ("Scroes dump file path: " + scores_dump_path)
print ("Inlinks dump file path: " + inlinks_dump_path)
print ("Outlinks dump file path: " + outlinks_dump_path)

#process_scores_dump(scores_dump_path, urls_collection)
#process_inlinks_dump(inlinks_dump_path, urls_collection)
#process_outlinks_dump(outlinks_dump_path, urls_collection)

hosts_dump_path = nutch_base_dir + 'dump/hoststats/part-r-00000'
domains_dump_path = nutch_base_dir + 'dump/domainstats/part-r-00000'
print ("Hosts dump file path: " + hosts_dump_path)
print ("Domains dump file path: " + domains_dump_path)

hosts_collection = db['hosts']
hosts_collection.create_index('host')
process_hosts_dump(hosts_dump_path, hosts_collection)


domains_collection = db['domains']
domains_collection.create_index('domain')
process_domains_dump(domains_dump_path, domains_collection)

print (db.collection_names())

print ("Finished")


