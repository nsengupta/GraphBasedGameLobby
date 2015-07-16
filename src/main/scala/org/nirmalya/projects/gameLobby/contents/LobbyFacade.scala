package org.nirmalya.projects.gameLobby.contents

import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.edge.LDiEdge

// labeled directed edge
import scalax.collection.edge.Implicits._ // shortcuts

import scalax.collection.edge.LBase._
object StringLabel extends LEdgeImplicits[String]

import org.joda.time.format.DateTimeFormat

import scalax.collection.immutable.Graph

/**
 * Created by nirmalya on 14/7/15.
 */
class LobbyFacade(arcadeName: String) {

  implicit val factory = scalax.collection.edge.LDiEdge

  val fmt = DateTimeFormat.forPattern("dd-MM-yyyy")

  val playerRootNode = Player("ROOT")
  val tableRootNode  = GameTable(-1)

  // lobby is seeded with necessary 'root-nodes'
  var lobby = Graph[LobbyNode,LDiEdge]() + playerRootNode + tableRootNode

  /*def welcomeNewPlayer(name: String) = {

    lobby = lobby ++ List(Player(name,fmt.print(new DateTime(System.currentTimeMillis()))))
  }*/

  def retrieveAllPlayers = {

    val playerNodes = lobby.get(playerRootNode)
      .outgoing
      .view
      .filter(e => e.label == "IsAPlayer")
      .map(e => e.target)
      .map(_.value)
      .collect { case p: Player => p }
      .toList

    playerNodes
  }

  def retrieveAllTables = {

    retrieveAllTablesInner.view.map(p => p.value).collect { case t: GameTable => t }.toList
  }

  def retrieveAllPlayersOnTable(tableToSearch: GameTable) = {

    val innerTableNode = retrieveAllTablesInner
      .view
      .filter(nextTable => nextTable == tableToSearch)
      .head

    innerTableNode.outgoing
      .view.filter(nextOut => nextOut.label == "seatOccupiedBy")
      .view.map(e => e.target)
      .view.map(_.value)
      .toList
  }

  def assignPlayersToTable(pairs: List[(Player,GameTable)]) = {
    lobby = lobby ++
      pairs.foldLeft(IndexedSeq[LDiEdge[LobbyNode]]())((holder,nextPair) => {
        val rootToPlayer = setRelationWithLabel(playerRootNode,nextPair._1,"IsAPlayer")
        val playerToRoot = setRelationWithLabel(nextPair._1,playerRootNode,"RootedAt")
        (holder :+ rootToPlayer :+ playerToRoot) ++ giveSeatToPlayer(nextPair._1,nextPair._2)
      }).toList
  }

  def setUpTables(tableInfo: List[(GameTable,GameType,ScoreSheet)]) = {

    val arrangedTables = tableInfo.foldLeft(IndexedSeq[LDiEdge[LobbyNode]]())((holder,nextTriple) => {

      val (table,gameType,tableScoreSheet)    = nextTriple
      val rootToTable   = setRelationWithLabel(tableRootNode,table,"IsATable")
      val tableToRoot   = setRelationWithLabel(table,tableRootNode,"RootedAt")
      val tableToType   = setRelationWithLabel(table,gameType,"IsOfType")
      val typeToTable   = setRelationWithLabel(gameType,table,"HasATableLaunched")
      val tableToScore  = setRelationWithLabel(table,tableScoreSheet,"HasScoresAt")

      val topScores     = new TopScorersChart(gameType)
      val typeToTopScores = setRelationWithLabel(gameType,topScores,"HostsTopScoresAt")


      holder :+ rootToTable :+ tableToRoot :+ tableToType :+ typeToTable :+ tableToScore :+ typeToTopScores
    })

    lobby = lobby ++ (arrangedTables.toList)
  }

  // Giving a seat to a Player is essentially setting the properties, to and fro
  def giveSeatToPlayer(p: Player, t: GameTable) = {

    IndexedSeq[LDiEdge[LobbyNode]](
      setRelationWithLabel(p,t,"IsPlayingAt"),
      setRelationWithLabel(t,p,"seatOccupiedBy")
    )
  }
  
  def setRelationWithLabel(source: LobbyNode,target: LobbyNode,label: String) =
    LDiEdge(source, target)(label)

  // To be called at the completion of a Round of game perhaps...
  // TODO: the API should be uniform, we should take Player's name, not Player object
  def freshenTableRoundEndScore(table: GameTable,roundEndScores: List[(Player,Int)]) = {

    val scoreSheet = associatedScoreSheet(table)
    roundEndScores.map(e => (e._1, scoreSheet.updateScore(e._1.name, e._2)))
    table
  }

  def linkFriends(pairs: List[(Player, Player)]) = {
    lobby = lobby ++
      pairs.map(nextPair => (setRelationWithLabel(nextPair._1,nextPair._2,"IsAFriendOf")))
  }

  // To be called at the completion of the Game (after several rounds) perhaps...
  def freshenGameEndTopScores(table: GameTable) = {

    val scoreSheet = associatedScoreSheet(table)

    val latestScore = scoreSheet.getLatestScore

    val gameType = associatedGameType(table)

    val topScores = associatedTopScoreChart(gameType)

    latestScore.foreach(l => topScores.updateWithNewScore(l._1, l._2))

    table

  }

  def associatedScoreSheet(table: GameTable) = {

    lobby
      .get(table)
      .outgoing
      .view
      .filter(e => e.label == "HasScoresAt")
      .view
      .map(e => e.target)
      .map(_.value)
      .collect { case s: ScoreSheet => s }
      .head // We know that this set is going to have exactly one entry

  }

  def associatedGameType(table: GameTable) = {

    lobby
      .get(table)
      .outgoing
      .view
      .filter(e => e.label == "IsOfType")
      .view
      .map(e => e.target)
      .map(_.value)
      .collect { case g: GameType => g }
      .head // We know that this set is going to have exactly one entry

  }

  def associatedTopScoreChart(gameType: GameType) = {

    lobby
      .get(gameType)
      .outgoing
      .view
      .filter(e => e.label == "HostsTopScoresAt")
      .map(e => e.target)
      .map(_.value)
      .collect { case ts: TopScorersChart => ts }
      .head // We know that this set is going to have exactly one entry

  }

  def identifyFriendsOf(player: Player) = {

    lobby.get(player).incoming.view
      .filter(e => e.label == "IsAFriendOf")
      .map(e => e.source)
      .map(_.value)
      .collect { case p: Player => p }
      .toList
  }


  // '3' is arbitrarily chosen, below
  def retrieveBusiestPlayers(mxNumbers: Int = 3) = {

    val playersAndTablesBunch = accumulatePlayersAndTheirTables

    playersAndTablesBunch.view
      .toSeq
      .groupBy   (e => e._1)
      .mapValues (e => e.size)
      .filter    (e => e._2 > 1)   // Busy players play on at least 2 tables simultaneously
      .toList
      .sortBy    (e => -e._2)
      .take      (mxNumbers)
  }

  def retrieveTablesPlayerIsAt(p: Player) = {
    lobby.get(p).outgoing.view
      .filter(e => e.label == "IsPlayingAt")
      .map(e => e.target)
      .map(_.value)
      .collect { case t: GameTable => t }
      .toList
  }

  def retrieveTablesOfGametype(gameType: GameType) = {
    lobby.get(gameType).outgoing.view
      .filter(x => x.label == "IsOfType")
      .map(e => e.target)
      .map(_.value)
      .collect { case gt: GameTable => gt }
      .toList
  }



  def retrieveLatestScoreByPlayer(name: String, atTable: GameTable) = {

    val scoreSheet = associatedScoreSheet(atTable)
    scoreSheet.getLatestScoreOf(name)
  }

  def retrieveTopScores(gameType: GameType) = associatedTopScoreChart(gameType).topScorers(3)

  private def retrieveAllTablesInner = lobby.get(tableRootNode).diSuccessors 

  def accumulatePlayersAndTheirTables = {

    val playersAndTablesBunch =
      for {

        nextPlayerNode   <- lobby.get(playerRootNode).diSuccessors

        tablePlayingAt  <- nextPlayerNode.outgoing.view
          .filter(e => e.label == "IsPlayingAt")
          .map(e => e.target)
      } yield {
        // [NS]: This downcast is necessary because of the type erasure
        (nextPlayerNode.value.asInstanceOf[Player],tablePlayingAt.value.asInstanceOf[GameTable])
      }

    playersAndTablesBunch

  }
}
