package com.akkapi.remote

import akka.actor.{Actor, Props, ActorSystem}
import com.typesafe.config.ConfigFactory

object RemoteApp extends App {
  implicit val actorSystem = ActorSystem("AkkaPi-System", ConfigFactory.parseString("""
    akka {
       actor {
           provider = "akka.remote.RemoteActorRefProvider"
             }
       remote {
           transport = ["akka.remote.netty.tcp"]
       netty.tcp {
           hostname = "127.0.0.1"
           port = 2552
                 }
             }
        }
    """)
  )
  val remoteActor = actorSystem.actorOf(Props(classOf[RemoteActor]), name = "RemoteAkkaActor")
  println(remoteActor.path)

}

class RemoteActor extends Actor {
  def receive = {
    case _ => println("remote actor")
  }
}
