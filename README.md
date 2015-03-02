# DogSim
Dog Simulation for SensorUp<BR>
Author: Karel Bergmann<BR>
The main class is DogManager.<P>

RUNNING:<BR>
Simply run as java dogsim/DogManager  No additional parameters are needed, but simulation is fully customizable from parameters withing dogsim.Dog, dogsim.Arena and dogsim.DogManager.<P>

The code models an arena, and a number of dogs with simulated behaviours.  Features include:<BR>
Dog behaviour:<BR>
-Dogs wander the arena<BR>
-Dogs chase other dogs when they are in sight<BR>
-Heart rates and internal temperatures are represented<BR>
-Temps and HR’s rise while a dog is running<BR>
-Dogs rest by staying still in order to bring temp and HR back into acceptable limits.<BR>
-Dogs turn around when they near a wall<BR>
-Each dog runs in its own thread<P>

The program connects to a web service, and provides dog information to the server.  The server-side code and configuration is located at the Github repository github.com/kpbergma/DogPark.<p>

Also included is a GUI to show what is going on in the interaction space:<BR>
-Shows the arena floor as a soccer field<BR>
-Each dog is represented by a dog icon with its ID number shown<BR>
-When the user hovers the mouse over a dog, the dog’s vital signs are shown<BR>
-The vitals of the previously selected dog are shown below the interaction space so the user
	doesn’t have to follow the dog around to see its vital signs.<BR>
-The largest, most dense cluster of dogs is highlighted in red, and their ID numbers
	are added to a watch list for monitoring.<P>
