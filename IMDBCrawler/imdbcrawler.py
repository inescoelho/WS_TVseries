###
# Based on imdb-crawler made by girish3, a crawler meant for creating movie databases
# https://github.com/girish3/imdb-crawler
#
# Adapted to retrieve tv shows information from the IMDB website
from bs4 import BeautifulSoup

import urllib.request
import re


class IMDBCrawler:
    def __init__(self):
        self.url = ""

    def crawl2(self):

        flash_url = "http://www.imdb.com/title/tt3107288/fullcredits"

        c = urllib.request.urlopen(flash_url)
        soup = BeautifulSoup(c.read())

        # print soup

        cast_list = soup.find('table', {'class': 'cast_list'})
        character_list = cast_list.findAll('td', {'class': 'character'})
        for character in character_list:
            # FIXME: Get actor name

            a = character.find('a')
            if a is not None:
                # FIXME: Get character name
                print(a.text)
            else:
                div = character.find('div')
                string_unchanged = div.text[0:div.text.index("(")]
                string_changed = re.sub(r'[^\w# ]', '', string_unchanged)

                string_changed = string_changed.lstrip()
                string_changed = string_changed.rstrip()
                print(string_changed)

    def crawl(self):
        limit = 1
        print("crawling....")
        genres = ("action", "comedy", "mystery", "sci_fi", "adventure", "fantasy", "horror", "animation", "drama",
                  "thriller")
        iteration = 0
        count = 0
        while count < limit:
            for genre in genres:
                if count > limit:
                    break
                c = self.get_webpage(genre, iteration)
                if c is None:
                    continue
                soup = BeautifulSoup(c.read())
                data = self.get_tv_show_data(soup)
                print("Got movie data " + str(data))
                print(len(data))
                count += 50
            iteration += 1

    def get_webpage(self, genre, iteration):
        try:
            self.url = "http://www.imdb.com/search/title?at=0&genres=" + genre + "&sort=moviemeter,asc&start=" + str(
                iteration * 50 + 1) + "&title_type=tv_series"

            print(self.url)
            c = urllib.request.urlopen(self.url)
            return c
        except Exception as e:
            print("error is " + str(e))
            print("could not open url " + str(self.url))
            return None

    def get_tv_show_data(self, soup):
        tr_tag = soup.table.tr
        tr_next = tr_tag.next_sibling.next_sibling
        data = []
        while tr_next:
            td = tr_next.contents[5]
            name = self.get_tv_show_name(td)
            year = self.get_tv_show_year(td)
            movie_id = self.get_movie_id(td)
            rating = self.get_tv_show_rating(td)
            users = self.get_tv_show_users(td)
            summary = self.get_tv_show_summary(td)
            genre = self.get_tv_show_genre(td)
            tr_next = tr_next.next_sibling.next_sibling

            data.append({'title': name, 'year': year, 'movie_id': movie_id, 'rating': rating, 'users': users,
                         'summary': summary, 'genre': genre})
        return data

    def get_tv_show_name(self, text):
        try:
            return text.span.next_sibling.next_sibling.string
        except:
            print("check movie name tag in this url " + str(self.url))

    def get_tv_show_rating(self, text):
        div = text.div.div

        if div is not None and div.has_attr('title') and div['title'].split()[0] == 'Users':
            value = div['title']
            return value.split()[3].split('/')[0]
        else:
            return 0

    def get_tv_show_users(self, text):
        div = text.div.div

        if div is not None and div.has_attr('title') and div['title'].split()[0] == 'Users':
            value = div['title']
            temp = value.split()[4].split('(')[1].split(',')
            return ''.join(temp)
        else:
            return 0

    def get_tv_show_year(self, text):
        try:
            return text.contents[5].string.split('(')[1].split(')')[0]
        except:
            print("check movie year tag in this url " + str(self.url))

    def get_movie_id(self, text):
        try:
            return text.span['data-tconst']
        except:
            print("check movie id tag in this url " + str(self.url))

    def get_tv_show_genre(self, text):
        br = text.br
        try:
            tag = br.contents[7]
            a = tag.a
            string_of_genre = ""
            while a:
                string_of_genre += a.string
                string_of_genre += ','
                if a.next_sibling:
                    a = a.next_sibling.next_sibling
                else:
                    break
            return string_of_genre
        except:
            # print "check movie genre in this url",self.url
            return ""

    def get_tv_show_summary(self, text):
        return text.div.next_sibling.next_sibling.string


crawler = IMDBCrawler()
# crawler.crawl()
crawler.crawl2()
