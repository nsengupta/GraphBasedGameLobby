package org.nirmalya

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.nirmalya.projects.gameLobby.contents._
import org.scalatest._
import org.scalatest.matchers.MustMatchers
/**
 * Created by nirmalya on 14/7/15.
 */
class LobbyTest extends FunSuite with BeforeAndAfter with Matchers {

  val fmt = DateTimeFormat.forPattern("dd-MM-yyyy")

  val lobbyFacade = new LobbyFacade("Serpentine Lane")

  // Such tuples helps us see the association between the three elements: table, type and score
  val knownTable21 = (GameTable(21,"Lower Hall",8.00f),GameType("Poker-Texas-Holdem"), ScoreSheet(21,"Lower Hall"))
  val knownTable11 = (GameTable(11,"Upper Hall",7.00f),GameType("Poker-Texas-Holdem"), ScoreSheet(11,"Lower Hall"))
  val knownTable31 = (GameTable(31,"Upper Hall",4.00f),GameType("Tic-Tac-Toe-For-Fun"),ScoreSheet(31,"Upper Hall"))

  lobbyFacade.setUpTables(List(knownTable11,knownTable21,knownTable31))

  val playerNS = Player("Nirmalya Sengupta",fmt.print(new DateTime(System.currentTimeMillis())))
  val playerID = Player("Irene DCosta",     fmt.print(new DateTime(System.currentTimeMillis())))
  val playerAK = Player("Anwar Khan",       fmt.print(new DateTime(System.currentTimeMillis())))
  val playerJC = Player("Jacquiline Cosme", fmt.print(new DateTime(System.currentTimeMillis())))

  val knownPlayers = List ( playerNS,playerID,playerAK,playerJC ) // Just a handy collection

  val knownFriendsOfRelationship = List (
    (playerNS,playerAK), (playerNS,playerID),
    (playerID,playerAK), (playerJC,playerNS),
    (playerJC,playerID)
  )

  lobbyFacade.assignPlayersToTable(
    List(
      (playerNS,knownTable21._1),
      (playerAK,knownTable21._1),
      (playerJC,knownTable21._1)
    )
  )

  lobbyFacade.assignPlayersToTable(
    List(
      (playerID,knownTable31._1),
      (playerNS,knownTable31._1)
    )
  )

  val roundScoresOfTable21 = IndexedSeq (
    List((playerNS,10),(playerAK,20),(playerJC,10)),
    List((playerNS,10),(playerAK,20),(playerJC,10)),
    List((playerNS,20),(playerAK,18),(playerJC,-6)),
    List((playerNS,-5),(playerAK,-8),(playerJC,20))
  )


  test("All tables can be seen in the lobby") {

    val allTables = lobbyFacade.retrieveAllTables

    allTables should have  length(3)
    allTables should contain (GameTable(21,"Lower Hall",8.00f))
    allTables should contain (GameTable(11,"Upper Hall",7.00f))
    allTables should contain (GameTable(31,"Upper Hall",4.00f))

  }

  test ("All players are spotted in the lobby") {

    val allPlayers = lobbyFacade.retrieveAllPlayers

    assert (allPlayers.size == 4)
    allPlayers should contain (playerNS)
    allPlayers should contain (playerID)
    allPlayers should contain (playerAK)
    allPlayers should contain (playerJC)

  }

  test ("Players on a given table are found as originally seated") {

    val playersOnTable21 = lobbyFacade.retrieveAllPlayersOnTable(GameTable(21))
    val playersOnTable31 = lobbyFacade.retrieveAllPlayersOnTable(GameTable(31))
    val playersOnTable11 = lobbyFacade.retrieveAllPlayersOnTable(GameTable(11))

    playersOnTable21 should have length(3)
    playersOnTable21 should contain (playerNS)
    playersOnTable21 should contain (playerAK)
    playersOnTable21 should contain (playerJC)
    playersOnTable21 should not contain (playerID)

    playersOnTable31 should have length(2)
    playersOnTable31 should contain (playerID)

    playersOnTable11 should have length(0)
  }

  test ("Friendship between players is identified properly") {

    lobbyFacade.linkFriends(knownFriendsOfRelationship)

    val friendsOfAK = lobbyFacade.identifyFriendsOf(playerAK)

    friendsOfAK should have length (2)
    friendsOfAK should contain (playerNS)
    friendsOfAK should contain (playerID)
    friendsOfAK should not contain (playerJC)
  }

  test ("There is one busy player, playing on more than one table simultaneously") {

    val busyPlayers = lobbyFacade.retrieveBusiestPlayers(2)

    busyPlayers should have length(1)
    busyPlayers.head._1 === playerNS
    busyPlayers.head._2 === 2  // #tables on which playerNS is playing

  }

  test ("A Player is spotted at the tables she is supposed to be") {

    val tablesNSisAt = lobbyFacade.retrieveTablesPlayerIsAt(playerNS)
    val tablesAKisAt = lobbyFacade.retrieveTablesPlayerIsAt(playerAK)

    tablesNSisAt should have length(2)
    tablesNSisAt should contain  (knownTable21._1)
    tablesNSisAt should contain  (knownTable31._1)

    tablesAKisAt should have length(1)
    tablesAKisAt should contain  (knownTable21._1)
  }

  test ("Scoresheet for the table is updated correctly") {

    // we will use only the first round scores for this testcase
    lobbyFacade.freshenTableRoundEndScore(GameTable(21),roundScoresOfTable21(0))
    val scoreByAK = lobbyFacade.retrieveLatestScoreByPlayer(playerAK.name,GameTable(21))

    scoreByAK === 20

  }

  test ("TopScorers of Poker-Texas-Holdem shows correct names of players") {

    // Important: the testcase preceding this has already updated scoresheet with round(1) scores
    // So, we update the scores of next 3 rounds

    for (i <- 1 to 3)(
      lobbyFacade.freshenTableRoundEndScore(knownTable21._1,roundScoresOfTable21(i))
    )

    // We need to update the Leaders' scoreboard too because the game is complete (after 4 rounds)
    lobbyFacade.freshenGameEndTopScores(knownTable21._1)

    val leaders = lobbyFacade.retrieveTopScores(knownTable21._2)

    leaders should have length (3)
    leaders.head.playerName  === playerAK.name
    leaders.head.latestTotal === 50
    leaders should contain (TopScore(playerNS.name,35))
  }

}
