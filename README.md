Used Java 17 with Springboot and Maven.
To test run application and send POST request to http://localhost:[port]/api/tickets/draft-price.
Request body examples:
{
  "route": "vilnius",
  "passengers": [
    {
      "type": "ADULT",
      "luggage": ["bag"]
    }
  ]
}

{
  "route": "Berlin",
  "passengers": [
    {
      "type": "ADULT",
      "luggage": ["bag"]
    },
        {
      "type": "CHILD",
      "luggage": ["bag1", "bag2"]
    }
  ]
}
