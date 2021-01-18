import serial
import time
import math
import json
import pymongo
from pymongo import MongoClient
import time

myclient = pymongo.MongoClient("mongodb://projektVMDuser:Premzl123@cluster0-shard-00-00.g3yf1.mongodb.net:27017,cluster0-shard-00-01.g3yf1.mongodb.net:27017,cluster0-shard-00-02.g3yf1.mongodb.net:27017/projekt_database?ssl=true&replicaSet=atlas-106yxf-shard-0&authSource=admin&retryWrites=true&w=majority")
mydb = myclient["projekt_database"]
mycol = mydb["STMSpeed"]

ser = serial.Serial(port='COM4', baudrate="57600")

ser.isOpen()

def current_milli_time():
    return round(time.time() * 1000)

vx = 0
vy = 0
vz = 0
rez = 0
rezPrev = 0
zadniZavzemCasa = current_milli_time();

while 1 :
    #Shrani pridobljene znake iz CDCja v out
    out = ''
    while ser.inWaiting() > 0:
        out += ser.read(1).decode("utf-8")
    if out != '':
        splitOutput = out.split()

        # rez = int(math.sqrt(math.pow(ax,2)+math.pow(ay,2)+math.pow(az,2)))
        #(Test) izračunava hitrosti
        deltaTime = current_milli_time() - zadniZavzemCasa;
        zadniZavzemCasa = current_milli_time();

        ax = float(splitOutput[0]);
        ay = float(splitOutput[1]);
        az = float(splitOutput[2]);
        #print(az)

        a = int(math.sqrt(math.pow(ax,2)+math.pow(ay,2)+math.pow(az,2)))

        predznak = 1
        if((ax) < 0):
            predznak = -1

        rez = rez + predznak*a*deltaTime

        print(str(int(rez/100)))

        #Zapis v txt datoteko
        f = open("hitrosti.txt", "a")
        f.write(str(int(rez/100))+"\n")
        f.close()

        f = open("testic.txt", "a")
        f.write(out)
        f.close()

        #Vnos v MongoDB bazo
        x = mycol.update_one({'_id' :str(1)},{"$set":{"Speed": int(rez/100)}},True)
        #print(x)
