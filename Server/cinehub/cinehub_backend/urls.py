from django.urls import path
from . import views
urlpatterns = [
    path('all_movies',views.get_movies, name='get_movies'),
    path('search_movie_by_title',views.get_movies_by_title, name='search_movie_by_title'),
    path('bookings_for_user',views.get_bookings, name='get_bookings'),
    path('add_movie',views.add_movie, name='add_movie'),
    path('add_booking',views.add_booking, name='add_booking'),
    path('update_movie',views.update_movie, name='update_movie'),
    path('delete_movie',views.delete_movie, name='delete_movie'),
    path('delete_booking',views.delete_booking, name='delete_booking')
]