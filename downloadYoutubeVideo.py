from pytubefix import YouTube
import sys

def main():
    url = sys.argv[1]
    yt = YouTube(url, use_oauth=True, allow_oauth_cache=True)

    ys = yt.streams.filter(file_extension='mp4').get_highest_resolution()
    print(ys.download("tmp/", "tempVideo.mp4"))
    print(yt.thumbnail_url)
    print(yt.title)

if __name__ == "__main__":
    main()