package org.nirmalya.projects.gameLobby.contents

import com.github.nscala_time._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.scalacheck.{Prop, Gen}

/**
 * Created by nirmalya on 28/5/15.
 */
object EntityGenerators {

  val fmt = DateTimeFormat.forPattern("dd-mm-yyyy")

  val gameTypeGenerator = Gen.oneOf(GameType("Poker-Texas-Holdem"),GameType("Poker-7-Cards-Omaha"),GameType("Tic-Tac-Toe-For-Fun"))

  val clientNameGenerator =  Gen.containerOfN[List,String](
    10,
    Gen.oneOf(
      "Jacquiline Cosme", "Kecia Vines", "Ronnie Eagar", "Raymonde Broeckel", "Edyth Pries",
      "Ladawn Allin", "Enid Loveridge", "Shelia Cola", "Damaris Nehls", "Will Nantz",
      "Alexa Seaton", "Lynne Bracewell", "Waltraud Lester", "Cierra Placek",
      "Fredrick Oh", "Evie Sterner", "Kelli Lachapelle", "Clotilde Delorey", "Debroah Cannella",
      "Carolee Tash", "Merle Ickes", "Eli Rahim", "Tatum Dahlstrom", "Armanda Stclair",
      "Christina Gadberry", "Cyrus Thomsen", "Denisha Behn",
      "Bryanna Shah  ","Kris Lyda  ","Marty Gladwin  ","Roscoe Mau",
      "Azucena Fearon","Gustavo Sater", "Taren Reams", "Tiny Timpson",
      "Elisa Radebaugh  ","Denny Mattocks","Shela Mccane","Darla Yoshida","Sam Straughter",
      "Christena Hamon  ","Elmira Rathbone","Shasta Looney","Gina Easter","Mike Cabaniss",
      "Devona Gamble","Charles Borror","Marina Bodnar","Sanora Papas","Reginia Bluitt"))

  val tableGenerator = for {

    id <- Gen.choose(1,10)
    location <- Gen.oneOf("Lower Hall","Middle Hall","Upper Hall")
    stake <- Gen.choose(1.0f,20.0f)

  } yield GameTable(id,location,stake)

  val playerGenerator = for {

        i <- clientNameGenerator
        j <- Gen.containerOfN[List,Long](10,Gen.choose(1L,10L))

  } yield (i,j)

  def associateTableToGameType = {

    var pairsTableGametype = IndexedSeq[(GameTable,GameType)]()

    val p = Prop.forAll(tableGenerator,gameTypeGenerator){ (table,gameType) =>

      pairsTableGametype = pairsTableGametype :+ (table,gameType)

      (1 == 1)

    }
    p.check

    pairsTableGametype

  }

  def producePlayers = {

    var players = IndexedSeq[Player]()

    val p = Prop.forAll(playerGenerator) { (g) =>

      val constructorPairs = (g._1 zip g._2)
        .map(e => Player (
                    e._1,
                    fmt.print(new DateTime(System.currentTimeMillis() - e._2))
        ))
      players = players ++ constructorPairs.toIndexedSeq

      (1 == 1)

    }
    p.check
    players
  }
}
