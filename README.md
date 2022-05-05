# M15-FinalProject-Andriamasinoro-Mandresy
This is a command line program that interacts with various APIs. There are 4 options from the menu:

1. Get the weather in any city
2. Get the current location of the International Space Station
3. Get the current location of the International Space Station and the weather of the city below it
4. get the price of any crypto currency

For API keys:

To use the program, you need to get API Keys from the following websites: https://www.coinapi.io/ , https://openweathermap.org/api

These keys must be put in an environment file (.env) and named in this way

  CRYPTO_KEY=your key
  
  WEATHER_KEY=your key

Finally, copy the absolute path of this .env file and put it as a parameter for the Dotenv object instantiation.
