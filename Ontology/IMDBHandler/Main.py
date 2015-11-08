# coding: utf-8
from imdb import IMDb
import csv

__author__ = "Joaquim Leitão"
__email__ = "jocaleitao93@gmail.com"


def main():
    # Open ListSeries.txt e depois vir aqui buscar o id e guardar num csv
    series_file = open("files/ListSeries.txt")
    tv_show_file = open("files/TV_Show_Series.csv", "wb")
    writer = csv.writer(tv_show_file)

    series_file_content = series_file.readlines()
    imdb_handler = IMDb()

    for current_name in series_file_content:
        # Remove '\n' from show name
        if current_name[len(current_name) - 1] == '\n':
            current_name = current_name[0:len(current_name)-1]
        tv_show = imdb_handler.search_movie(current_name, 1)
        # Save to file
        writer.writerow([current_name, tv_show[0].movieID])

    series_file.close()
    tv_show_file.close()


if __name__ == '__main__':
    main()


