# DogSim
Dog Simulation for SensorUp

The code models an arena, and a number of dogs with simulated behaviours.  Features include:
Dog behaviour:
-Dogs wander the arena
-Dogs chase other dogs when they are in sight
-Heart rates and internal temperatures are represented
-Temps and HR’s rise while a dog is running
-Dogs rest by staying still in order to bring temp and HR back into acceptable limits.
-Dogs turn around when they near a wall
-Each dog runs in its own thread

Also included is a GUI to show what is going on in the interaction space:
-Shows the arena floor as a soccer field
-Each dog is represented by a dog icon with its ID number shown
-When the user hovers the mouse over a dog, the dog’s vital signs are shown
-The vitals of the previously selected dog are shown below the interaction space so the user
	doesn’t have to follow the dog around to see its vital signs.
-The largest, most dense cluster of dogs is highlighted in red, and their ID numbers
	are added to a watch list for monitoring.

The main class is DogManager.
