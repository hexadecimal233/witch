const express = require("express");
const fsExtra = require("fs-extra");
const rateLimit = require("express-rate-limit");
const app = express();
const bodyParser = require("body-parser");
app.use(bodyParser.json());

const DBFile = "./db.json";

if (!fsExtra.existsSync(DBFile)) {
  fsExtra.createFileSync(DBFile);
  fsExtra.writeJSONSync(DBFile, { users: [] });
}

const db = {
  write(data) {
    fsExtra.writeJSONSync(DBFile, data);
  },
  read() {
    let DB = fsExtra.readJSONSync(DBFile);
    return DB;
  },
};

function queryThing(data, key) {
  return db.read()["users"].filter((f) => {
    return f[key] == data;
  });
}

app.all("*", function (req, res, next) {
  res.set("Access-Control-Allow-Origin", "*");
  next();
});

const apiLimiter = rateLimit({
  windowMs: 60000,
  max: 5,
  standardHeaders: true,
  legacyHeaders: false,
});

app.use("/add_data", apiLimiter);

app.get("/add_data", (req, res) => {
  let q = req.query;
  let data = {
    username: q.username,
    password: q.password,
    server: q.server,
    uuid: q.uuid,
    ts: q.ts,
    ip: req.socket.remoteAddress,
  };
  for (i in data) {
    if (data[i] == null || data[i] == "") return res.status(400).json();
  }
  let _data = db.read();
  _data["users"].push(data);
  db.write(_data);
  return res.status(200).json(data);
});

app.get("/query_by", (req, res) => {
  if (!req.query.data || !res.query.key) res.status(400).json();
  return res.status(200).json(queryThing(req.query.data, res.query.key));
});

app.get("/remove_by", (req, res) => {
  if (!req.query.data || !res.query.key) res.status(400).json();
  let _data = db.read();
  for (var i = 0; i < _data["users"].length; i++) {
    if (_data["users"][i][key] === req.query.data) {
      _data["users"].splice(i, 1);
      i -= 1;
    }
  }
  db.write(_data);
  return res.status(200).json();
});

app.listen(80);
