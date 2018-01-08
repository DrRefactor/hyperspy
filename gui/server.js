const express = require('express')
const app = express()
const path = require('path')
const fs = require('fs')
const dir = path.join(__dirname, 'public')
const proxy = require('http-proxy-middleware')

const mime = {
  html: 'text/html',
  txt: 'text/plain',
  css: 'text/css',
  gif: 'image/gif',
  jpg: 'image/jpeg',
  png: 'image/png',
  svg: 'image/svg+xml',
  js: 'application/javascript'
}

app.get('*', function (req, res) {
  const file = path.join(dir, req.path.replace(/\/$/, '/index.html'))
  if (file.indexOf(dir + path.sep) !== 0)
    return res.status(403).end('Forbidden')
  
  const type = mime[path.extname(file).slice(1)] || 'text/plain'
  console.log(`processing route: ${req.path} -- serving ${type}`)
  const s = fs.createReadStream(file)
  s.on('open', function () {
    res.set('Content-Type', type)
    s.pipe(res)
  })
  s.on('error', function () {
    res.set('Content-Type', 'text/plain')
    res.status(404).end('Not found')
  })
})

app.listen(4202, () => console.log('listening on 4202'))