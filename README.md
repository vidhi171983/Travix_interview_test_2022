BusyFlights is a flights search solution which aggregates flight results initially from 2 different suppliers (CrazyAir and ToughJet). A future iteration (not part of the test) may add more suppliers.
To achieve this I have modified the existing approach.
Have created 2 separate services CrazyAirService and ToughJetService which implements FlightsService interface. So in future
when there are more suppliers it required to just create new service for that supplier.

I have assumed single FlightRequest and FlightResponse for all supplier endpoints given that request and response 
parameters are same.

