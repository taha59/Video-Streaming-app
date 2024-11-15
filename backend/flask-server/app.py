from flask import Flask, jsonify, request, send_file, make_response
from pytubefix import Search, YouTube
from flask_cors import CORS
import base64

import os

frontend_server = "http://localhost:4200"
app = Flask(__name__)
# Enable CORS for all routes and expose the header for title
CORS(app, resources={r"/search": {"origins": frontend_server},
                     r"/download-video": {"origins": frontend_server, "expose_headers": ["X-Video-Title"]}})

def getFileSizeInMegaBytes(file_size_bytes):
    return file_size_bytes / (1024 * 1024)  # Convert bytes to MB

@app.route('/search', methods=['POST'])
def searchYoutube():
    searchQuery = request.form.get('searchQuery')
    
    results = Search(searchQuery)

    videoDto = []
    for video in results.videos:

        videoDto.append({
            'title': video.title,
            'youtubeUrl': video.watch_url,
            'thumbnailUrl': video.thumbnail_url
        })
        
    return jsonify(videoDto)

@app.route('/download-video', methods=['POST'])
def downloadVideo():
    url = request.form.get('youtubeUrl')
    yt = YouTube(url)
    ys = yt.streams.filter(file_extension='mp4').get_highest_resolution()

   
    file_size_mb = getFileSizeInMegaBytes(ys.filesize)

    # Set a 200 MB limit
    if file_size_mb <= 200:

        file_path = "tmp/tempVideo.mp4"
    
        # If the file already exists, delete it
        if os.path.exists(file_path):
            os.remove(file_path)

        ys.download("tmp/", "tempVideo.mp4")
        
        # Create a response to send the file
        response = make_response(send_file(file_path, 
        as_attachment=True, download_name=f"{yt.title}"))

        # URL encode the title
        encoded_title = base64.b64encode(yt.title.encode('utf-8')).decode('utf-8')
        # Add a custom header with the video title
        response.headers.extend({'X-Video-Title': encoded_title})

        # Return the response with the custom header
        return response
    return jsonify("Download Unsuccessfull")

if __name__ == '__main__':
    app.run()
