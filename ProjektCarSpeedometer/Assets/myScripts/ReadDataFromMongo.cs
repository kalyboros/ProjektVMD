using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using MongoDB.Driver;
using MongoDB.Bson;
using UnityEngine.UI;
using System;

public class ReadDataFromMongo : MonoBehaviour
{
    public Text text;
    public GameObject image;
    public Sprite[] imageList;

    private static string conString = "mongodb://projektVMDuser:Premzl123@cluster0-shard-00-00.g3yf1.mongodb.net:27017,cluster0-shard-00-01.g3yf1.mongodb.net:27017,cluster0-shard-00-02.g3yf1.mongodb.net:27017/projekt_database?ssl=true&replicaSet=atlas-106yxf-shard-0&authSource=admin&retryWrites=true&w=majority";
    private static MongoClient Client = new MongoClient(conString);

    // Start is called before the first frame update
    void Start()
    {
        var DB = Client.GetDatabase("projekt_database");
        var collection = DB.GetCollection<BsonDocument>("STMSpeed");
        //Pridobi prvi podatek v "collection" STMSpeed
        var podatek = collection.Find(Builders<BsonDocument>.Filter.Eq("_id", "1")).ToList();

        //Spremeni UI text na appu na trenuten podatek
        text.text = podatek[0][1].ToString();
    }

    // Update is called once per frame
    void Update()
    {
        var DB = Client.GetDatabase("projekt_database");
        var collection = DB.GetCollection<BsonDocument>("STMSpeed");
        var podatek = collection.Find(Builders<BsonDocument>.Filter.Eq("_id", "1")).ToList();

        Image tmpRender = image.GetComponent<Image>();

        //Spremeni UI image v appu glede na podano hitrost
        switch (Math.Floor(podatek[0][1].ToDecimal() / 9))
        {
            case 0:
                tmpRender.sprite = imageList[0];
                text.color = Color.green;
                break;
            case 1:
                tmpRender.sprite = imageList[1];
                text.color = Color.green;
                break;
            case 2:
                tmpRender.sprite = imageList[2];
                text.color = Color.green;
                break;
            case 3:
                tmpRender.sprite = imageList[3];
                text.color = Color.green;
                break;
            case 4:
                tmpRender.sprite = imageList[4];
                text.color = Color.green;
                break;
            case 5:
                tmpRender.sprite = imageList[5];
                text.color = Color.green;
                break;
            case 6:
                tmpRender.sprite = imageList[6];
                text.color = Color.green;
                break;
            case 7:
                tmpRender.sprite = imageList[7];
                text.color = Color.yellow;
                break;
            case 8:
                tmpRender.sprite = imageList[8];
                text.color = Color.yellow;
                break;
            case 9:
                tmpRender.sprite = imageList[9];
                text.color = Color.yellow;
                break;
            case 10:
                tmpRender.sprite = imageList[10];
                text.color = Color.yellow;
                break;
            case 11:
                tmpRender.sprite = imageList[11];
                text.color = Color.yellow;
                break;
            case 12:
                tmpRender.sprite = imageList[12];
                text.color = Color.yellow;
                break;
            case 13:
                tmpRender.sprite = imageList[13];
                text.color = Color.yellow;
                break;
            case 14:
                tmpRender.sprite = imageList[14];
                text.color = Color.red;
                break;
            case 15:
                tmpRender.sprite = imageList[15];
                text.color = Color.red;
                break;
            case 16:
                tmpRender.sprite = imageList[16];
                text.color = Color.red;
                break;
            case 17:
                tmpRender.sprite = imageList[17];
                text.color = Color.red;
                break;
            case 18:
                tmpRender.sprite = imageList[18];
                text.color = Color.red;
                break;
            case 19:
                tmpRender.sprite = imageList[19];
                text.color = Color.red;
                break;
            default:
                tmpRender.sprite = imageList[20];
                text.color = Color.red;
                break;
        }
        //Spremeni UI text na appu na trenuten podatek
        text.text = podatek[0][1].ToString();
    }
}
