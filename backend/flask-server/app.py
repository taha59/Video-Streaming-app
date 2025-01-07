from flask import Flask
from flask import jsonify, send_file, make_response, request

from pytubefix import Search, YouTube
from flask_cors import CORS
import base64
import os
from urllib.request import urlopen
import json
from config import FRONTEND_SERVER, AUTH0_JWK_SET_URI, AUDIENCE

from authlib.integrations.flask_oauth2 import ResourceProtector
from authlib.jose.rfc7517.jwk import JsonWebKey
from authlib.oauth2.rfc7523 import JWTBearerTokenValidator

flask_dir_path = os.path.abspath("backend/flask-server")

app = Flask(__name__)

CORS(app, resources={r"/search": {"origins": FRONTEND_SERVER},
                     r"/download-video": {"origins": FRONTEND_SERVER, "expose_headers": ["X-Video-Title"]}}, supports_credentials=True)

#class for verifying tokens
class ClientCredsTokenValidator(JWTBearerTokenValidator):
    def __init__(self, issuer):
        jsonurl = urlopen(f"{issuer}.well-known/jwks.json")
    
        public_key = JsonWebKey.import_key_set(
            json.loads(jsonurl.read())
        )
        super(ClientCredsTokenValidator, self).__init__(
            public_key
        )
        self.claims_options = {
            "exp": {"essential": True},
            "iss": {"essential": True, "value": issuer},
            "aud": {"essential:": True, "value": AUDIENCE}
        }

require_auth = ResourceProtector()
validator = ClientCredsTokenValidator(AUTH0_JWK_SET_URI)
require_auth.register_token_validator(validator)


def get_file_size_in_megabytes(file_size_bytes):
    """Converts file size from bytes to MB."""
    return file_size_bytes / (1024 * 1024)


@app.route('/search', methods=['POST'])
@require_auth(None)
def search_youtube():
    search_query = request.form.get('searchQuery')
    results = Search(search_query)
    video_dto = [
        {
            'title': video.title,
            'youtubeUrl': video.watch_url,
            'thumbnailUrl': video.thumbnail_url
        }
        for video in results.videos
    ]
    return jsonify(video_dto)


@app.route('/download-video', methods=['POST'])
@require_auth(None)
def download_video():

    
    #Download the video in mp4 format
    url = request.form.get('youtubeUrl')
    yt = YouTube(url)
    ys = yt.streams.filter(file_extension='mp4').get_highest_resolution()
    file_size_mb = get_file_size_in_megabytes(ys.filesize)

    #only download videos upto 200 MB 
    if file_size_mb <= 200:
        
        #before downloading the video in the temp dir remove any prev files
        file_path = f"{flask_dir_path}/tmp/tempVideo.mp4"
        os.makedirs(os.path.dirname(file_path), exist_ok=True)
        if os.path.exists(file_path):
            os.remove(file_path)
        ys.download(os.path.dirname(file_path), "tempVideo.mp4")


        response = make_response(send_file(
            file_path,
            as_attachment=True,
            download_name=f"{yt.title}.mp4"
        ))
        encoded_title = base64.b64encode(yt.title.encode('utf-8')).decode('utf-8')
        response.headers.extend({'X-Video-Title': encoded_title})
        return response

    return jsonify("Download Unsuccessful")


if __name__ == '__main__':
    app.run()
