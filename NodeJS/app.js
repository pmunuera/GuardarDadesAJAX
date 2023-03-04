const express = require('express')
const fs = require('fs/promises')
const url = require('url')
const post = require('./post.js')
const { v4: uuidv4 } = require('uuid')
const mysql = require('mysql2')

// Wait 'ms' milliseconds
function wait (ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// Start HTTP server
const app = express()

// Set port number
const port = process.env.PORT || 3000

// Publish static files from 'public' folder
app.use(express.static('public'))

// Activate HTTP server
const httpServer = app.listen(port, appListen)
function appListen () {
  console.log(`Listening for HTTP queries on: http://localhost:${port}`)
}

// Set URL rout for POST queries
app.post('/dades', getDades)
async function getDades (req, res) {
  let receivedPOST = await post.getPostObject(req)
  let result = {};

  var textFile = await fs.readFile("./public/consoles/consoles-list.json", { encoding: 'utf8'})
  var objConsolesList = JSON.parse(textFile)

  if (receivedPOST) {
    if (receivedPOST.type == "test") {
      result = { status: "OK" }
    }
    if (receivedPOST.type == "add") {
      await wait(1500);
      var correct = true;
      var sendText="";
      if(receivedPOST.firstName.length==0){
        sendText=sendText+"NO_fname"
        correct = false
      }
      if(receivedPOST.lastName.length==0){
        sendText=sendText+"NO_lname"
        correct = false
      }
      if(receivedPOST.mail.length==0){
        sendText=sendText+"NO_mail"
        correct = false
      }
      if(receivedPOST.phone.toString.length==0){
        sendText=sendText+"NO_phone"
        correct = false
      }
      else if(receivedPOST.phone.toString().length!=9){
        console.log(receivedPOST.phone.toString().length);
        sendText=sendText+"NO_len"
        correct = false
      }
      if(receivedPOST.direction.length==0){
        sendText=sendText+"NO_dir"
        correct = false
      }
      if(receivedPOST.city.length==0){
        sendText=sendText+"NO_city"
        correct = false
      }
      if(correct==true){
        try{
          await queryDatabase("INSERT into Usuari (firstName,lastName,mail,phone,direction,city) values('"+receivedPOST.firstName+"','"+receivedPOST.lastName+"','"+
          receivedPOST.mail+"',"+receivedPOST.phone+",'"+receivedPOST.direction+"','"+receivedPOST.city+"');");
          result = { status: "OK"}
        }
        catch(error){
          sendText=senText+"NO_type"
          result = {status:"NO_type"}
        }
    }
    else{
      result={status:sendText}
    }
    }
    if (receivedPOST.type == "modify") {
      await wait(1500);
      var correct = true;
      var sendText="";
      if(receivedPOST.firstName.length==0){
        sendText=sendText+"NO_fname"
        correct = false
      }
      if(receivedPOST.lastName.length==0){
        sendText=sendText+"NO_lname"
        correct = false
      }
      if(receivedPOST.mail.length==0){
        sendText=sendText+"NO_mail"
        correct = false
      }
      if(receivedPOST.phone.toString.length==0){
        sendText=sendText+"NO_phone"
        correct = false
      }
      else if(receivedPOST.phone.toString().length!=9){
        console.log(receivedPOST.phone.toString().length);
        sendText=sendText+"NO_len"
        correct = false
      }
      if(receivedPOST.direction.length==0){
        sendText=sendText+"NO_dir"
        correct = false
      }
      if(receivedPOST.city.length==0){
        sendText=sendText+"NO_city"
        correct = false
      }
      if(correct==true){
        try{
          await queryDatabase("UPDATE Usuari SET firstName='"+receivedPOST.firstName+"',lastName='"+receivedPOST.lastName+"',mail='"+
          receivedPOST.mail+"',phone="+receivedPOST.phone+",direction='"+receivedPOST.direction+"',city='"+receivedPOST.city+"'"+
          "WHERE id="+receivedPOST.userId+";");
          result = { status: "OK"}
        }
        catch(error){
          result = {status:"NO_type"}
        }
    }
    else{
      result={status:sendText}
    }
    }
    if(receivedPOST.type == "carrega"){
      var resultado=await queryDatabase("SELECT id,firstName from Usuari");
      await wait(1500);
      result = { status: "OK", result: resultado}
    }
  }

  res.writeHead(200, { 'Content-Type': 'application/json' })
  res.end(JSON.stringify(result))
}

// Run WebSocket server
const WebSocket = require('ws')
const wss = new WebSocket.Server({ server: httpServer })
const socketsClients = new Map()
console.log(`Listening for WebSocket queries on ${port}`)

// What to do when a websocket client connects
wss.on('connection', (ws) => {

  console.log("Client connected")

  // Add client to the clients list
  const id = uuidv4()
  const color = Math.floor(Math.random() * 360)
  const metadata = { id, color }
  socketsClients.set(ws, metadata)

  // Send clients list to everyone
  sendClients()

  // What to do when a client is disconnected
  ws.on("close", () => {
    socketsClients.delete(ws)
  })

  // What to do when a client message is received
  ws.on('message', (bufferedMessage) => {
    var messageAsString = bufferedMessage.toString()
    var messageAsObject = {}
    
    try { messageAsObject = JSON.parse(messageAsString) } 
    catch (e) { console.log("Could not parse bufferedMessage from WS message") }

    if (messageAsObject.type == "bounce") {
      var rst = { type: "bounce", message: messageAsObject.message }
      ws.send(JSON.stringify(rst))
    } else if (messageAsObject.type == "broadcast") {
      var rst = { type: "broadcast", origin: id, message: messageAsObject.message }
      broadcast(rst)
    } else if (messageAsObject.type == "private") {
      var rst = { type: "private", origin: id, destination: messageAsObject.destination, message: messageAsObject.message }
      private(rst)
    }
  })
})

// Send clientsIds to everyone
function sendClients () {
  var clients = []
  socketsClients.forEach((value, key) => {
    clients.push(value.id)
  })
  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) {
      var id = socketsClients.get(client).id
      var messageAsString = JSON.stringify({ type: "clients", id: id, list: clients })
      client.send(messageAsString)
    }
  })
}

// Send a message to all websocket clients
async function broadcast (obj) {
  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) {
      var messageAsString = JSON.stringify(obj)
      client.send(messageAsString)
    }
  })
}

// Send a private message to a specific client
async function private (obj) {
  wss.clients.forEach((client) => {
    if (socketsClients.get(client).id == obj.destination && client.readyState === WebSocket.OPEN) {
      var messageAsString = JSON.stringify(obj)
      client.send(messageAsString)
      return
    }
  })
}

// Perform a query to the database
function queryDatabase (query) {

  return new Promise((resolve, reject) => {
    var connection = mysql.createConnection({
      host: process.env.MYSQLHOST || "containers-us-west-141.railway.app",
      port: process.env.MYSQLPORT || 7664,
      user: process.env.MYSQLUSER || "root",
      password: process.env.MYSQLPASSWORD || "NRBm8SeWMqUDkU7umyS6",
      database: process.env.MYSQLDATABASE || "railway"
    });

    connection.query(query, (error, results) => { 
      if (error) reject(error);
      resolve(results)
    });
     
    connection.end();
  })
}