# GraphBasedGameLobby

This is a small application that aims to use Scala Graph library (http://www.scala-graph.org/)
APIs to create a multiplayer Game's lobby service. 

A lobby is a component where games are not played. However, all current and past information about
players, tables, stakes and results are readily available here. Typically, it runs as a service,
which is queried (READ) by external entities and applications to gather such information as mentioned above.
Other services like login, game and admin update (WRITE) lobby's contents from time to time, sometimes continuously.

There are more than one ways to depict the relationships between entities in a lobby service. In this
application, such relationships are modelled as a **property graph**. Every entity is a *node* 
and their associations are *edges* in the graph. The edges are *labeled* with the information about what
the association is. Therefore more precisely speaking, a lobby is modelled as Labeled Directed Graph.

For example, a Player playing at a Table, is modelled as
  
[Player]--- IsPlayingAt --> [GameTable]

Conversely, the fact that the table has players playing on it, is modelled as

[GameTable]-- SeatOccupiedBy --> [Player]

Such an arrangement seems natural.

Several such associations are easily captured:

[GameTable] -- IsOfType --> [GameType]

[GameTable] -- HasScoresAt --> [ScoreSheet]

[GameType] -- HasTopScorersAt --> [TopScores]

So on and so forth...

Finding all the laid-out tables of a certain type of game is a simple function
composition then:

    lobby
    .get(gameType).outgoing.view
      .filter(x => x.label == "IsOfType")
      .map(e => e.target)
      .map(_.value)  
      .collect { case gt: GameTable => gt }
      .toList

For more such useful, pithy and expressive operations on a lobby, take a look at:

    org.nirmalya.LobbyTest
and

    org.nirmalya.projects.gameLobby.contents.LobbyFacade
    

    
    