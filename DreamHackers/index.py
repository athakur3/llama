import socketio
import logging
from aiohttp import web

# Set up basic logging configuration (write logs to 'socketio_server.log')
logging.basicConfig(filename='socketio_server.log', level=logging.INFO)

# Create a Socket.IO server with logging enabled
sio = socketio.AsyncServer(logger=True, engineio_logger=True, async_mode='aiohttp')
app = web.Application()
sio.attach(app)

# Define a default route for the root URL
async def index(request):
    return web.Response(text="Socket.IO server is running!")

app.router.add_get('/', index)

# Handle client connection
@sio.event
async def connect(sid, environ):
    logging.info(f"Client {sid} connected.")
    await sio.emit('welcome', {'message': 'Welcome to the server!'}, room=sid)

# Handle client disconnection
@sio.event
async def disconnect(sid):
    logging.info(f"Client {sid} disconnected.")

# Handle incoming 'new message' event from the Android app
@sio.event
async def new_message(sid, data):
    logging.info(f"New message from {sid}: {data}")
    # Send the message back to all clients
    await sio.emit('new message', {'username': 'Server', 'message': f"Server received: {data}"})

# Handle 'new_event' event
@sio.event
async def new_event(sid, data):
    logging.info(f"New event from {sid}: {data}")
    # Respond to the new event
    await sio.emit('new_event_response', {'response': f"New event received: {data}"}, room=sid)

# Start the server
if __name__ == '__main__':
    web.run_app(app, host='0.0.0.0', port=8080)
