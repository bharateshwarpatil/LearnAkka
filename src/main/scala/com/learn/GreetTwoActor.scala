package com.learn


import akka.actor.typed
import akka.actor.typed.internal.BehaviorImpl
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

import java.util
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.List

object GreetTwoActor {

  final case class Greet(whom: String, replyTo: ActorRef[Greeted])

  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info("Hello {}!", message.whom)
    print(s"Hello $message")
    message.replyTo ! Greeted(message.whom, context.self)
    Behaviors.same
  }


  def main(args: Array[String]): Unit = {
    print("Hello")
    var greetA = ActorSystem(GreetTwoActor.apply(), "Greet");
    var greetedA = ActorSystem(Behaviors.setup[Greeted] { context =>
      Behaviors.receive { (context, message) =>
        print("Hi there ", message)
        Behaviors.same
      }
    }, "Greeted")
    greetA ! Greet("hi", greetedA)
  }
}
