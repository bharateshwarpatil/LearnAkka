package com.learn


import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

object HelloWorld {
  def apply(): Behavior[String] = Behaviors.receive[String] { (context, message) =>
    print(s"Hello $message")
    context.log.info("Hello {}!", message)
    Behaviors.same
  }

  def main(args: Array[String]): Unit = {
    ActorSystem(HelloWorld.apply(),"Greet").tell(s" World!!");
  }

}
