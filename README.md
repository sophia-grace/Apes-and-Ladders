# Apes-and-Ladders
Simulates a set of apes (as threads) that cross a gorge using the rungs of a single (shared) ladder in such a way that apes cross as fast and as often as they can, but do not collide. Implemented in Java.

See the repository on github: https://github.com/sophia-grace/Apes-and-Ladders/

## How to run
This program uses Eclipse to run. 

### To run the concurrent version:
In Eclipse, create a new project called "apes-and-ladders". Create a package "jungle". Put the three .java files (Ape.java, Jungle.java, and Ladder.java) in this package.

### To run the not concurrent version:
This version is not concurrent in the sense that only 1 ape is allowed to cross the ladder at a time (the program does run concurrent threads, but the ladder is mutually exclusive). In Eclipse, create a new project called "apes-and-ladders-notConcurrent". Create a package "jungle_notConcurrent". Put the three .java files (Ape_notConcurrent.java, Jungle_notConcurrent.java, and Ladder_notConcurrent.java) in this package.

## The meaning of the output
For the concurrent version, the output is formatted such that time increases from left to right, top to bottom (such that the bottom right output occurred last). Below is the beginning few lines of a sample output from the concurrent version. Ape E-1 means it is Ape 1 of Apes heading east, and Ape W-7 means it is Ape 7 of Apes heading west.

![alt text](https://github.com/sophia-grace/Apes-and-Ladders/blob/master/concurrent_sample_output.png)

## Efficiency comparison for the concurrent and not concurrent versions
The concurrent version is more efficient than the not concurrent version, as expected. In the worst case, the concurrent version would perform as efficiently as the not concurrent version (in the case that east and west apes perfectly alternate in gaining access to the ladder. This is equivalent to the not concurrent version because it eliminates apes of the same direction being able to cross simultaneously.). This explains some of the variation in execution time of the concurrent program (since the precise order in which access is requested is undetermined).

![alt text](https://docs.google.com/spreadsheets/d/e/2PACX-1vQoX6Jhu8nBQDAmNsfsLDo_UHwggdj-xHmBBIVKiGBi6TNrd0rbAvu5czocsy_cxISO1sVeJes4I8fA/pubchart?oid=1032139997&format=image)
