package org.nirmalya.projects.gameLobby.contents

case class Client (val name: String) extends LobbyNode {
  
  def identifiedAs = s"$name"

}