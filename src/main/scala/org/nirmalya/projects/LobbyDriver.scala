package org.nirmalya.projects


import org.joda.time.DateTime
import org.nirmalya.projects.gameLobby.contents.LobbyFacade


import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.edge.LDiEdge     // labeled directed edge
import scalax.collection.edge.Implicits._ // shortcuts

import scalax.collection.edge.LBase._
object StringLabel extends LEdgeImplicits[String]

import StringLabel._

import org.joda.time.format.DateTimeFormat
import org.nirmalya.projects.gameLobby.contents.{Client,Player,LobbyNode,ScoreSheet,LobbyRelations,GameTable,GameType}


import scalax.collection.Graph

object LobbyDriver extends App {

  implicit val factory = scalax.collection.edge.LDiEdge

  val fmt = DateTimeFormat.forPattern("dd-MM-yyyy")

  val lobbyFacade = new LobbyFacade("At New Arcade (Westside)")



  val tablesLaid   = lobbyFacade.setUpTables(
    List(
      SampleDataChest.knownTableTriplets11,
      SampleDataChest.knownTableTriplets21,
      SampleDataChest.knownTableTriplets31
    )
  )

  val playersReady = lobbyFacade.assignPlayersToTable(
    List(
        (SampleDataChest.playerNS,SampleDataChest.knownTableTriplets21._1),
        (SampleDataChest.playerAK,SampleDataChest.knownTableTriplets21._1),
        (SampleDataChest.playerJC,SampleDataChest.knownTableTriplets21._1),

        // playerNS is sitting at two tables simultaneously
        (SampleDataChest.playerNS,SampleDataChest.knownTableTriplets31._1),
        (SampleDataChest.playerID,SampleDataChest.knownTableTriplets31._1)
    )
  )

  // Rounds of game continue (possibly at the GameServer's container). When the rounds ends, lobby facade
  // receives the information. Upon getting that, scores are updated, thus:

  lobbyFacade
    .freshenTableRoundEndScore(
      GameTable(21),
      SampleDataChest.roundScoresOfTable21(0)  // first round only
    )

  // We also come to know about friendships between players or admiration of one for another. We
  // capture that:

  lobbyFacade.linkFriends(SampleDataChest.knownFriendsOfRelationship)

  // The games continue at various tables. We want to know if any player too busy, meaning if she is playing
  // on more than one table, simultaneously:

  val busiestPlayers = lobbyFacade.retrieveBusiestPlayers(2).map(elem => elem._1)

  // We want to know which tables is this player, playing on:

  val tables = lobbyFacade.retrieveTablesPlayerIsAt(busiestPlayers.head)

  // Meanwhile, subsequent rounds follow (we play for 4 rounds):
  for (i <- 1 to 3)(
    lobbyFacade
      .freshenTableRoundEndScore(
        SampleDataChest.knownTableTriplets21._1,
        SampleDataChest.roundScoresOfTable21(i))
    )

  // The game ends after 4 rounds. We update the top scorers chart for this particular
  // type (Poker-Texas-Holdem) of the game, thus:

  lobbyFacade.freshenGameEndTopScores(SampleDataChest.knownTableTriplets21._1)

  // Who are the top two players after the game is over?

  val leaders = lobbyFacade.retrieveTopScores(SampleDataChest.knownTableTriplets21._2).take(2)

  // We want to know who are the friends of the current leader

  val friends = lobbyFacade.identifyFriendsOf(Player(leaders.head.playerName))


}