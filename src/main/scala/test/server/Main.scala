package test.server

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import kamon.Kamon

object Main extends App {

  val config = ConfigFactory.load()
  lazy val port = config.getInt("pingpong.port")
  val hostname = config.getString("pingpong.host")
  val logLevel = "INFO"
  val extensions = Seq.empty[String]
  lazy val remote = config.getString("pingpong.remote")

  def akkaConfig =
s"""
  akka {
      actor {
          provider = "akka.remote.RemoteActorRefProvider"
      }
      loglevel = $logLevel
      remote {
          transport = "akka.remote.netty.NettyRemoteTransport"
          enabled-transports = ["akka.remote.netty.tcp"]
          netty.tcp {
              hostname = "$hostname"
              port = $port
          }
      }
      extensions = [${ extensions.map(e=>"\"" + e + "\"").mkString(", ") }]
  }
"""

  Kamon.start()

  val system = ActorSystem("pingpongserver",ConfigFactory.parseString(akkaConfig))
  val ping = config.getBoolean("pingpong.ping")
  val total = system.actorOf(Props(new TotalingActor),"total")
  (0 until 100).foreach(i => system.actorOf(Props(new PingPongActor(i,ping,total)),s"pingpong$i"))
}
