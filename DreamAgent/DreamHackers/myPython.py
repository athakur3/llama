import socketio
import logging
from aiohttp import web
from audio_handler import handle_audio_data

logging.basicConfig(filename='socketio_server.log', level=logging.INFO)

sio = socketio.AsyncServer()
app = web.Application()
sio.attach(app)

@sio.on('*')
async def catch_all(event, sid, data):
    logging.info(f"Event: {event}, SID: {sid}, Data: {data}")

@sio.event
async def connect(sid, environ, auth):
    print('connect ', sid)
    logging.info(f"Client {sid} connected.")
    await sio.emit('welcome', {"message": "Custom welcome message"}, room=sid)

@sio.event
async def disconnect(sid):
    print('disconnect ', sid)
    logging.info(f"Client {sid} disconnected.")

@sio.event
async def audio_data(sid, data):
    local_path = handle_audio_data(data)
    if local_path:
        # Do something with the local audio file if needed
        pass

if __name__ == '__main__':
    web.run_app(app, host='0.0.0.0', port=3000)
