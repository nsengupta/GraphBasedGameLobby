package org.nirmalya.projects.gameLobby.contents

/**
 * Created by nirmalya on 19/5/15.
 */
case class GameType ( val name: String) extends LobbyNode {

   def identifiedAs = name
}
