package com.akkapi.api

import akka.actor._
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http
import spray.routing.HttpService


object AkkaPiApi extends App {
  implicit val actorSystem = ActorSystem("AkkaPi-System", ConfigFactory.parseString("""
    akka {
       actor {
           provider = "akka.remote.RemoteActorRefProvider"
             }
       remote {
           transport = ["akka.remote.netty.tcp"]
       netty.tcp {
           hostname = "127.0.0.1"
           port = 0
                 }
             }
        }
                                                                                    """)
  )
  IO(Http) ! Http.Bind(
    actorSystem.actorOf(Props(classOf[ApiActor]),
      name = "APIServiceActor"),
    interface = "0.0.0.0",
    port = 8080
  )

}

class ApiActor extends Actor with ApiInterface {
  implicit val actorRefFactory = context
  val remoteActor = actorRefFactory.actorSelection("akka.tcp://AkkaPi-System@127.0.0.1:2552/user/RemoteAkkaActor")

  def receive = runRoute(route)
}

trait ApiInterface extends HttpService {

  val remoteActor: ActorSelection

  val route = {
    path(""){
      complete{
        remoteActor ! "msg"
        "ok"
      }
    }
  }
}