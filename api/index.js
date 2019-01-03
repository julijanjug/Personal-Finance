'use strict'

var sqlite3 = require("sqlite3").verbose();
var crypto = require("crypto")
var db = new sqlite3.Database('testdatabase.sqlite');

db.serialize(function() {
    db.run("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, email TEXT UNIQUE, password TEXT)");
});


var express = require("express");
var api = express();
var bodyParser = require('body-parser');
api.use(bodyParser.json());
api.use(bodyParser.urlencoded({ extended: true }));

var hashPassword = function(password){
    var hash = crypto.createHmac('sha256', "naAsDdACA28a");
    hash.update(password);
    return hash.digest('hex');
};

api.post("/register", function(req, res) {
    var email = req.body.email;
    var password = req.body.password;

    if (email === undefined || password === undefined) {
        res.json({success: false, message: "Some parameters are missing from your request."});
        return;
    }
    db.get("SELECT * FROM users WHERE email = ?", [email], function(err, row) {
        if (row === undefined) {
            db.run("INSERT INTO users (email, password) VALUES (?, ?)", [email, hashPassword(password)], function(err) {
                if (err) {
                    console.log(err)
                    res.json({success: false, message: "An error occurred."});
                } else {
                    res.json({success: true});
                }
            });
        } else {
            res.json({success: false, message: "An account with that email address already exists."});
        }
    });
});

api.post("/login", function(req, res) {
    var email = req.body.email;
    var password = req.body.password;
    if (email === undefined || password === undefined) {
        res.json({success: false, message: "Some parameters are missing from your request."});
        return;
    }
    db.get("SELECT * FROM users WHERE email = ? AND password = ?", [email, hashPassword(password)], function(err, user) {
        if (user === undefined) {
            res.json({success: false, message: "Login failed."})
        } else {
            res.json({success: true})
        }
    });
})

api.listen(7000);
