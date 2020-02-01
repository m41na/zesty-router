import path from 'path'
import express from 'express'
import cors from 'cors'
import router from './router'

const publicPath = express.static(path.join(__dirname, '../build'))

const app = express()
app.use(cors())
app.use(publicPath)

app.get('*', router)

export default app;
