import os
import subprocess

def handle_audio_data(data):
    # Get the directory of the current script
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Construct the local file path
    local_path = os.path.join(script_dir, os.path.basename(data))

    # Execute the adb pull command
    try:
        subprocess.run(['adb', 'pull', data, local_path], check=True)
        print(f"Local Path: {local_path}")
        return local_path
    except subprocess.CalledProcessError as e:
        print(f"Failed to pull file: {e}")
        return None
