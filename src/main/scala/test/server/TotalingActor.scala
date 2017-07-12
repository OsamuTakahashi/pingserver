package test.server

import akka.actor.{Actor, ActorLogging}
import akka.actor.Actor.Receive
import kamon.Kamon

import scala.concurrent.duration.Duration

case object Total
case object Lost
case object CountUp

/**
  * Created by takahashi on 2016/12/27.
  */
class TotalingActor extends Actor with ActorLogging {

  private var _minReturn = Long.MaxValue
  private var _maxReturn = 0L
  private var _total = 0L
  private var _count = 0L
  private var _lost = 0L

  private val _lostCounter = Kamon.metrics.counter("pingpong-lost")
  private val _turn = Kamon.metrics.counter("pingpong-counter")
  private val _min = Kamon.metrics.histogram("pingpong-min")
  private val _avr = Kamon.metrics.histogram("pingpong-avr")
  private val _max = Kamon.metrics.histogram("pingpong-max")

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    context.system.scheduler.schedule(Duration(10,"s"),Duration(10,"s"),self,Total)(context.dispatcher)
  }

  override def receive: Receive = {
    case Total =>
      if (_count > 0) {
        _min.record(_minReturn)
        _avr.record(_total / _count)
        _max.record(_maxReturn)
      }
      _count = 0
      _minReturn = Long.MaxValue
      _maxReturn = 0L
      _total = 0
      _lost = 0L
      _turn.increment()
      //log.info("Totaling")
    case CountUp =>

    case Lost =>
      _lostCounter.increment()
      _lost += 1
    case t:Long =>
      if (t < _minReturn)
        _minReturn = t
      if (t > _maxReturn)
        _maxReturn = t
      _total += t
      _count += 1

  }
}
