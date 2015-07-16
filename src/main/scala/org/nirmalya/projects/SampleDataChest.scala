package org.nirmalya.projects

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.nirmalya.projects.gameLobby.contents.{GameTable, GameType, Player, ScoreSheet}

/**
 * Created by nirmalya on 16/7/15.
 */
object SampleDataChest {

  val fmt = DateTimeFormat.forPattern("dd-MM-yyyy")

  val playerNS = Player("Nirmalya Sengupta",fmt.print(new DateTime(System.currentTimeMillis())))
  val playerPK = Player("Prasanna Kumar",   fmt.print(new DateTime(System.currentTimeMillis())))
  val playerID = Player("Irene DCosta",     fmt.print(new DateTime(System.currentTimeMillis())))
  val playerAK = Player("Anwar Khan",       fmt.print(new DateTime(System.currentTimeMillis())))
  val playerJC = Player("Jacquiline Cosme", fmt.print(new DateTime(System.currentTimeMillis())))
  val playerIS = Player("Evie Sterner",     fmt.print(new DateTime(System.currentTimeMillis())))
  val playerGS = Player("Gustavo Sater",    fmt.print(new DateTime(System.currentTimeMillis())))
  val playerSP = Player("Sanora Papas",     fmt.print(new DateTime(System.currentTimeMillis())))

  val knownPlayers = List (playerNS,playerJC,playerID,playerAK)

  // Just a handy triplet, so that we can visualize the relationships
  val knownTableTriplets21 = (GameTable(21,"Lower Hall",8.00f),GameType("Poker-Texas-Holdem"), ScoreSheet(21,"Lower Hall"))
  val knownTableTriplets11 = (GameTable(11,"Upper Hall",7.00f),GameType("Poker-Texas-Holdem"), ScoreSheet(11,"Lower Hall"))
  val knownTableTriplets31 = (GameTable(31,"Upper Hall",4.00f),GameType("Tic-Tac-Toe-For-Fun"),ScoreSheet(31,"Upper Hall"))

  val knownFriendsOfRelationship = List (
    (playerNS,playerAK), (playerNS,playerID),
    (playerID,playerAK), (playerJC,playerNS),
    (playerJC,playerID)
  )

  val roundScoresOfTable21 = IndexedSeq (
    List((playerNS,10),(playerAK,20),(playerJC,10)), // first round   (0)
    List((playerNS,10),(playerAK,20),(playerJC,10)), // second round  (1)
    List((playerNS,20),(playerAK,18),(playerJC,-6)), // third round   (2)
    List((playerNS,-5),(playerAK,-8),(playerJC,20))  // fourth round  (3)
  )


}
