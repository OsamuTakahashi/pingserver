package test.server

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}
import akka.actor.Actor.Receive
import akka.remote.RemotingLifecycleEvent
import akka.util.ByteString

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by takahashi on 2016/12/22.
  */
class PingPongActor(no:Int,isPing:Boolean,val total:ActorRef) extends Actor with ActorLogging {
  private var _remoteActor:Option[ActorRef] = None
  private var _timeoutTimer:Option[Cancellable] = None
  private var _count = 0L
  private var _sendTime = 0L

  private lazy val _randomBytes:ByteString =
    ByteString((0 until 1024).map(_=>(math.random * 127).toByte).toArray)

  private def _sendMsg(actor:ActorRef,bytes:ByteString):Unit = {
    if (isPing) {
      _timeoutTimer.foreach(_.cancel())
      _timeoutTimer = Some(context.system.scheduler.schedule(Duration(60,"s"),Duration(60,"s"),self,Lost)(context.dispatcher))
      _sendTime = System.currentTimeMillis()
    }
    actor ! bytes
    _count += 1L
  }

  override def preStart(): Unit = {
    if (isPing) {
      val remote = Await.result(context.system.actorSelection(s"akka.tcp://pingpongserver@${Main.remote}/user/pingpong$no").resolveOne(Duration(10,"s")),Duration(10,"s"))
      context.system.eventStream.subscribe(self, classOf[RemotingLifecycleEvent])
      _remoteActor = Some(remote)
      _sendMsg(remote,_randomBytes)
    }
  }

  override def receive: Receive = {
    case bytes:ByteString =>
      if (isPing) {
        val d = System.currentTimeMillis() - _sendTime
        total ! d
      } else {
        total ! CountUp
      }
      _sendMsg(sender,bytes)
      _count += 1L

    case Lost =>
      total ! Lost
      _remoteActor.foreach(remote=>_sendMsg(remote,_randomBytes))

    case ev : RemotingLifecycleEvent =>
      log.info(s"RemotingLifecycleEvent received: $ev")
  }
}
