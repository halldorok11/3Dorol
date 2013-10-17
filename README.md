#3Dorol

##Authors
Halldór Örn Kristjánsson - halldorok11@ru.is
Ólafur Daði Jónsson - olafurj11@ru.is

##Setup
Runs in Intellij

How to get it started:

1. Unzip the .zip.
2. Open up Intellij.
3. File -> Import Project.
4. Select the unzipped archive.
5. Simply press next (overwrite anything that comes up).
6. Now the project should be open.
7. File -> Project Structure.
8. Open up the Libraries category.
9. Add a new Java library.
10. Select the libs folder.
11. Go to the modules category and check the export box on you newly added libs folder.
12. OK.
13. Build -> Rebuild project.
14. Open the DesktopStarter class.
15. Run the project with shift+f10, select the DesktopStarter class as the default running class.
16. Project can now be run by shift+f10.

##Gameplay
The goal of the game is to run around the maze and find the portal.
Each time you find it you will be transported to a new bigger level.

###Keybindings
* W : go forward
* S : go backward
* A : turn left
* D : turn right
* ESCAPE : quit game
* P : toggle flightmode
  + R : ascend
  + F : descend

##Development information
The projects was built in Intellij.

###3D objects
All objects are made from the `Cube` class which holds information for all vertices and normals for a 3D cube.
The `Cube` class has a constructor and a `draw` function.
The `draw` function takes as parameters the size, location, orientation and texture of each individual cube.

###The floor
The floor is made up of a single (almost)flat cube in each cell.

###The walls
Each wall is made up of a narrow cube positioned depending on the values of each cells `northpath` and `eastpath` variables

###The portal
The portal consists of 6 columns of 8 cubes spinning around the middle of the final cell.
Each columns "sways" a little for added effect.

###The Maze
The maze consists of rows and columns of cells maintained by a 2D array.
Each call has two boolean variables: `northpath` and `eastpath` maintained in the `Cell` class.

Each cell can be surrounded by at most 3 walls (since there must be at least one path in/out of it).

###Maze Generation
Each maze is generated using Prim's algorithm on a randomly edge-weighted graph.

###Collision
The eye has a hitbox of 1,5x1,5 so anytime the eye moves into an object it moves back in the closest "non-collision" coordinate.

###Lights/Fog
In regular game mode (not flying) then there is a specular light going straight down on the eye and the maze is filled with fog.

In flight mode there is an ambient light coming from the eye and no fog for extra vision.
Flight mode also has increased movement speed.


##Other
If you hit any problems, please contact halldorok11@ru.is and olafurj11@ru.is for more information on running the project.
