# Are You The One? solver

_Are you the one?_ is a reality show on MTV.  They put 11 women and 11 men together in one house for 10 weeks, each person having a perfect match (determined by a personality test that everyone took prior to going on the show), but not revealed to the participants.  Through the weekly game ceremonies they must work together to figure out everyone's perfect match, and if they succeed, they split $1 million

## Game Ceremonies

Each week there are two ceremonies through which the players can get information about perfect matches:
* __Truth Booth:__ One potential match is sent to the "truth booth" and the group gets a "match" or "no match" result
* __Matchup Ceremony:__ All participants match up (either all guys choose a girl or all girls choose a guy) and the group is told how many of their matches are correct (but not which ones)

## Usage

There are two modes you can run:
* __Simulator:__ This will simulate a game(s) using the stategy below
* __Scenario:__ Given a series of previous results, this will recommend an action using the strategy below

### Simulator

To use the game simulator, the first argument must be `-sim`

simulator required args:
`-sim <num-people> <num-runs>`

extra options:

`--rand` if this flag is set, solutions will be random (otherwise will be in order)  This seems like it would make it easier, but since the early rounds are mostly random it does not change the chance of winning.  By making the solution not random, it's easier to follow the game output.

#### Simulator Output

When you run the simulator, you won't see any names, just numbers.  For truth booths you will see [x y] which means person x from group A and person y from group B are sent to the truth booth.  For matchups you will see this [y0 y1 y2 y3.... yn] where n is the number of people in each group.  The index of the vector represents the person from group A, and the value represents the person from group B.  So [3 2 1 0] represents a matchup ceremony of 0:3 1:2 2:1 3:0.  At the end of each decision you will see how many possibilities reamining in the game

When the simulator is done, you will see all the scores of the games played and the number of won games (got all beams in 10 or fewer rounds)

### Scenario

To use the scenario solver, the first argument must be `-scenario`

scenario required args:
`-scenario <filename>`

extra options:

`--truth` get the recommended truth booth guess

`--matchup` get the recommended matchup ceremony pairings

#### Scenario Input File

Comma separated list of "group A" names (i.e. the men)

Comma separated list of "group B" names (i.e. the women)

Any number of lines in one of the two following formats:
  * Truth booth result: takes the form "_nameA_,_nameB_,[true/false]"
  * Matchup result: takes the form "_name1A_:_name1B_,_name2A_:_name2B_,.....,_number of beams_" where there are the same number of pairings as there are people in each group

For an example, check out "season\_results.txt"

## Examples

using leiningen:

`lein run -scenario season-results.txt --truth`

using the jar:

`java -jar areyoutheone-0.1.0-standalone.jar -scenario season-results.txt --truth`

these exmaples would output the recommended truth booth choice based on the scenario in 'season-results.txt'

## Strategy

At all points of the game, the solver will pick the option that has the least bad worst case scenario.

For the truth booth, this will pick the matchup with probability closest to 50%

For matchup ceremony, this means calculating the number of possibile matchups remaining for each number of beams result, taking the maximum from that list, and picking the matchups that minimize that number.

Early in the game there are too many possibilities to calculate in a reasonable amount of time, so there is also a "soft max comparisons" parameter hardcoded (for now) that limits the number of matchup possibilities that get analyzed

### Future Strategy Improvements

For games with 10 people or fewer in each group, this strategy probably wins every time (in 10 or fewer rounds).  I have yet to see a loss, but I do not have a proof that a win can be forced

For 11 person games, the strategy was able to win in 10 rounds 87% of the games over a sample size of 300 with almost all of the remaining games won after 11 rounds.  Outside of just increasing the number of possibilities tested in the early game, here are some other potential imrpovements

* Risky lategame strategy - It's possible that in the lategame if there are still many possibilities, it is correct to use a riskier strategy in an attempt to win the game more often, the tradeoff being when you dont win it would take more rounds to get all beams
* Smarter choice of matchups in the early game - Right now the potential matchup choices in the early game are chosen randomly.
* Set choices for first two weeks - In an 11 person game, after the first round there are only 20 possibilities of results, yes/no in the truth booth and 0-9 beams.  So we can probably determine a good matchup choice for each possibility ahead of time, rather than take a near random possibility
* Factor in blackouts - Not an improvement, but rather another factor that might change the strategy.  In the real show, a "blackout" is when the team gets no beams in a matchup ceremony (outside the confirmed truth booth picks).  Every time a blackout occurs, the prize fund is cut by 25% of the original amount.  This might affect strategy as it would be beneficial (in terms of expected value) to make a guess that has a lower chance of winning but also a lower chance to blackout

