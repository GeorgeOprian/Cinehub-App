from types import coroutine

from django.db.models.fields import NullBooleanField
from cinehub_backend.models import Booking, Movie
from cinehub_backend.models import Running_movie
# from cinehub_backend.models import Reserved_Seat
from django.http.response import JsonResponse
from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.db import connection
import json
from datetime import datetime, timedelta

# Create your views here.

RESP_CODE_SUCCES = 200
RESP_CODE_COULD_NOT_INSERT_IN_DB = 512
RESP_CODE_BOOKINGS_LINKED_TO_MOVIE = 513
RESP_CODE_RESOURCE_NOT_FOUND = 204

#time checks
PAUSE_BETWEEN_MOVIES = 15

#merge cu obiect de return in java
def get_movies(request): #basic get
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    print ("Get movies: ")
    
    runnings = Running_movie.objects.all()
    list_of_movies = []
    for running in runnings:
        movie_dto = create_movie_dto(running.movie.__dict__, running.__dict__)
        list_of_movies.append(movie_dto)

    resp = {}
    resp['ListOfMovies'] = list_of_movies
    print("Response:")
    print (resp)
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    return JsonResponse(resp)

def get_movies_by_title(request):
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    print("Get movies by title: ")
    resp_code = RESP_CODE_SUCCES
    movie_title = request.GET['movie_title']
    print ("movie title =" , movie_title)

    movie_title = '%' + movie_title + '%'
    
    cursor = connection.cursor()
    cursor.execute('select * from cinehub_backend_movie m'
                    + ' where lower(title) like %s', [movie_title.lower()])
    res = dictfetchall(cursor)
    if len(res) == 0:
        return HttpResponse(status = RESP_CODE_RESOURCE_NOT_FOUND)
    list_of_movies = []
    print("##############################")
    for result in res:
        
        cursor.execute('select * from cinehub_backend_running_movie'
                    + ' where movie_id like %s', [result['imdb_id']])
        associated_runnings = dictfetchall(cursor)

        print ("IMDB id: ", result['imdb_id'], " -> associated running:", associated_runnings)
        print("##############################")
        for running in associated_runnings:
            list_of_movies.append(create_movie_dto(result, running))

    resp = {}
    resp['ListOfMovies'] = list_of_movies
    
    print (resp)
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    return JsonResponse(resp)


def get_bookings(request):
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    user_id = request.GET['user_id']
    print ("Get bookings for user: ", user_id)
    
    # user_id = "CAAGM8TJqxeyz4qrTr8EWfjKvJw1"
    cursor = connection.cursor()
    cursor.execute('select booking_id, title, poster, date, time, seats, user_id, hall_id  from cinehub_backend_booking b '
                    + ' join cinehub_backend_running_movie r on b.running_id = r.running_id '
                    + ' join cinehub_backend_movie m on r.movie_id = m.imdb_id '
                    + ' where user_id = %s order by date desc', [user_id])
    res = dictfetchall(cursor)
    
    bookings = []
    for result in res:
        bookings.append(create_booking_dto(result))
    
    resp = {}
    resp['ListOfBookings'] = bookings
    print("Response: ", resp)

    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    return JsonResponse(resp)
    
def load_movies_that_run_on_same_date(date, hall_id):
    cursor = connection.cursor()
    cursor.execute('select * from cinehub_backend_running_movie '
                    + ' where date = %s and hall_id = %s ', [date, hall_id])
    res = dictfetchall(cursor)
    return res

def add_movie(request): #basic post
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    print("Add movie to DB")
    resp_code = RESP_CODE_SUCCES
    received_json_data = json.loads(request.body)
    movie_model = create_movie_model(received_json_data)
   
    print ("Movie received: ", movie_model.__dict__)
    
    running_to_insert = create_running_movie_model(received_json_data)
    
    running_in_the_same_day = load_movies_that_run_on_same_date(running_to_insert.date, running_to_insert.hall_id)

    if len(running_in_the_same_day) > 0:
        if can_add_movie_with_running_date_problems(running_to_insert, running_in_the_same_day):
            movie_model.save()
            running_to_insert.save()
        else:
            print ("There is another movie that runs at the same date and time.")
            resp_code = RESP_CODE_COULD_NOT_INSERT_IN_DB
    else:
        movie_model.save()
        running_to_insert.save()
    
    resp = {"resp":"movie added succesfully"} 
    if resp_code == RESP_CODE_SUCCES:
        print("The movie was added succesfully")
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    return JsonResponse(resp, status = resp_code)

def can_add_movie_with_running_date_problems(running_to_insert, running_in_the_same_day):   
    print("##############################")
    print("Movies that run in the same day: ", running_in_the_same_day)
    print("##############################")
    
    insert_movie = False
    for running_movie in running_in_the_same_day:
        cursor = connection.cursor()
        cursor.execute('select duration from cinehub_backend_movie '
                        + ' where cinehub_backend_movie.imdb_id = %s', [running_movie['movie_id']])
        duration_dict = dictfetchall(cursor)
        
        if can_movie_be_inserted_or_updated(running_movie['time'], running_to_insert.time, duration_dict[0]['duration']):
            insert_movie = True

    return insert_movie

def add_booking(request):
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    print("Add booking to DB")
    received_json_data = json.loads(request.body)

    print ("Booking received: ", received_json_data)

    resp_code = update_running_movie_seats(received_json_data)
    if resp_code == RESP_CODE_SUCCES:
        booking_model = create_booking_model(received_json_data)
        booking_model.save()
    
    resp = {"resp":"booking added succesfully"} 
    if resp_code == RESP_CODE_SUCCES:
        print("Booking added succesfully.")
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    return JsonResponse(resp, status = resp_code)

def update_movie(request):
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    print("Update movie request")
    running_dto = json.loads(request.body)
    print ("Running to update received: ", running_dto)
    resp_code = RESP_CODE_SUCCES
    running_model = create_running_movie_model(running_dto)
    running_model.running_id = running_dto['RunningId']
    
    running_in_the_same_day = load_movies_that_run_on_same_date(running_model.date, running_model.hall_id)

    if len(running_in_the_same_day) > 0:
        
        if can_add_movie_with_running_date_problems(running_model, running_in_the_same_day):
            running_model.save()
        else:
            print ("There is another movie that runs at the same date and time")
            resp_code = RESP_CODE_COULD_NOT_INSERT_IN_DB
    else:
        running_model.save()
    if running_model is RESP_CODE_SUCCES:
        print ("Running updated succesfully")
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    resp = {"resp":"movie added succesfully"} 
    
    return JsonResponse(resp, status = resp_code)

def delete_movie(request):
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    print("Delete movie request")
    resp_code = RESP_CODE_SUCCES
    imdb_id = request.GET['imdb_id']
    print ("Movie to delete: ", imdb_id)
    movies = Movie.objects.all()
    movie_to_be_deleted = None
    movie_found = False
    for movie in movies:
        if imdb_id.lower().strip() in movie.imdb_id.lower():
            movie_to_be_deleted = movie
            movie_found = True
    
    if not movie_found:
        print ("The movie was not found.")
        resp_code = RESP_CODE_RESOURCE_NOT_FOUND 
        return HttpResponse(status = resp_code)

    runnings = Running_movie.objects.all().filter(movie_id = movie_to_be_deleted.imdb_id)
    for running in runnings:
        if len(running.occupied_seats) != 0:
            resp_code = RESP_CODE_BOOKINGS_LINKED_TO_MOVIE
            return HttpResponse(status = resp_code)

    movie_to_be_deleted.delete()
    if resp_code is RESP_CODE_SUCCES:
        print("The movie was deleted succesfully.")
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    return HttpResponse(status = resp_code)

def delete_booking(request):
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    print("Delete booking request")
    resp_code = RESP_CODE_SUCCES
    booking_id_from_client = request.GET['booking_id']
    print ("Booking to delete: ", booking_id_from_client)
    booking_to_delete = Booking.objects.all().filter(booking_id = booking_id_from_client)
    
    booking_to_delete.delete()
    if resp_code is RESP_CODE_SUCCES:
        print ("Booking was deleted succesfully")
    print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    return HttpResponse(status = resp_code)


def dictfetchall(cursor):
    columns = [col[0] for col in cursor.description]
    return [
        dict(zip(columns, row))
        for row in cursor.fetchall()
    ]

def create_movie_model (rec_data):
    movie = Movie(
                imdb_id = rec_data['ImdbID'],
                title = rec_data['Title'],
                released = rec_data['Released'],
                duration = rec_data['Duration'],
                genre = rec_data['Genre'],
                director = rec_data['Director'],
                writer = rec_data['Writer'],
                actors = rec_data['Actors'],
                plot = rec_data['Plot'],
                language = rec_data['Language'],
                awards = rec_data['Awards'],
                poster = rec_data['Poster'],
                imdb_rating = rec_data['imdbRating']
            )
    return movie

def create_running_movie_model(rec_data):
  
    running_movie = Running_movie(
                        date = rec_data['RunningDate'],
                        time = rec_data['RunningTime'],
                        movie_id = rec_data['ImdbID'],
                        hall_id = rec_data['HallNumber']
                    )
    return running_movie


def create_booking_model(rec_data):
    now = datetime.now()
    dt_string = now.strftime("%d/%m/%Y %H:%M:%S")
    booking = Booking(
        seats = convert_list_of_seats_from_int_to_string(rec_data['ReservedSeats']),
        user_id = rec_data['UserId'],
        running_id = rec_data['RunningId'],
        date_time = dt_string
    )

    return booking

def update_running_movie_seats(rec_data):
    running_movie = Running_movie.objects.get(running_id = rec_data['RunningId'])
    seats_from_client = rec_data['ReservedSeats']

    resp_status = RESP_CODE_SUCCES
    for seat in seats_from_client:
        if str(seat) in running_movie.occupied_seats:
            resp_status = RESP_CODE_COULD_NOT_INSERT_IN_DB
            return resp_status
        running_movie.occupied_seats += " " + str(seat)

    running_movie.save()
    return resp_status

#objects to be sent to the client
def create_movie_dto(movie, running):
    movie_dto = {}
    movie_dto["ImdbID"] = movie['imdb_id']
    movie_dto['Title'] = movie['title']
    movie_dto['Released'] = movie['released']
    movie_dto['Duration'] = movie['duration']
    movie_dto['Genre'] = movie['genre']
    movie_dto['Director'] = movie['director']
    movie_dto['Writer'] = movie['writer']
    movie_dto['Actors'] = movie['actors']
    movie_dto['Plot'] = movie['plot']
    movie_dto['Language'] = movie['language']
    movie_dto['Awards'] = movie['awards']
    movie_dto['Poster'] = movie['poster']
    movie_dto['imdbRating'] = movie['imdb_rating']

    if running is not None:
        movie_dto['RunningId'] = running['running_id']
        movie_dto['RunningDate'] = running['date']
        movie_dto['RunningTime'] = running['time']
        movie_dto['HallNumber'] = running['hall_id']

        movie_dto['OccupiedSeats'] = convert_list_of_seats_from_string_to_int(running['occupied_seats'])

    # movie_dto['OccupiedSeats'] = select_seats_for_running(running)
    return movie_dto

def convert_list_of_seats_from_string_to_int(list_of_seats_string):
    if list_of_seats_string == '':
        return []
    list_of_parsed_seats = list_of_seats_string.split(" ")
    list_of_seats_ints = []    
    for seat in list_of_parsed_seats:
        if seat != "":
            list_of_seats_ints.append(int(seat))
    return list_of_seats_ints

def convert_list_of_seats_from_int_to_string(list_of_seats_int):
    if list_of_seats_int == []:
        return ""
    string_ints = [str(seat) for seat in list_of_seats_int]
    list_of_seats_string = " ".join(string_ints)
    return list_of_seats_string

def create_booking_dto (booking):
    print ("booking = ", booking)
    booking_dto = {}
    booking_dto['BookingId'] = booking['booking_id']
    booking_dto['MovieTitle'] = booking['title']
    booking_dto['Poster'] = booking['poster']
    booking_dto['RunningDate'] = booking['date']
    booking_dto['RunningTime'] = booking['time']
    booking_dto['ReservedSeats'] = convert_list_of_seats_from_string_to_int(booking['seats'])
    booking_dto['HallNumber'] = booking['hall_id']
    return booking_dto




#############
#time checks
def can_movie_be_inserted_or_updated(movie_to_insert_hour, movie_from_db, duration_string):
    t1_extended = add_duration_to_running_hour(movie_to_insert_hour, duration_string, PAUSE_BETWEEN_MOVIES)

    # t1 = convert_string_time_to_object(t1_extended)
    t2 = convert_string_time_to_object(movie_from_db)
    
    if t1_extended < t2:
        return True
    else:
        return False


def convert_string_time_to_object(string_time):
    return datetime.strptime(string_time, '%H:%M')

def convert_time_to_formated_string(time):
    return time.strftime('%H:%M')


def add_duration_to_running_hour(running_hour_string, duration_string, pause_time):
    running_hour = convert_string_time_to_object(running_hour_string)
    duration_int = int(duration_string.split()[0])

    extended_hour = (running_hour + timedelta(minutes = (duration_int + pause_time)))
    return extended_hour


