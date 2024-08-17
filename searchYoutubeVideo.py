from pytubefix import Search
import sys

def main():
    searchQuery = sys.argv[1]
    results = Search(searchQuery)

    for video in results.videos:
        print(video.watch_url)
        print(video.thumbnail_url)

if __name__ == "__main__":
    main()