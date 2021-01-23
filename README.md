# LearnAkka 
###### This is are some points taken from AKKA Docs. 


### What is the Difference Between Actor Reference and Path?
An actor reference designates a single actor and the life-cycle of the reference matches that actor’s life-cycle; an actor path represents a name which may or may not be inhabited by an actor and the path itself does not have a life-cycle, it never becomes invalid. You can create an actor path without creating an actor, but you cannot create an actor reference without creating a corresponding actor.


### How are Actor References obtained?
There are two general categories to how actor references may be obtained: by creating actors or by looking them up through the Receptionist.

### Top-Level Scopes for Actor Paths
At the root of the path hierarchy resides the root guardian above which all other actors are found; its name is "/". The next level consists of the following:
* "/user" is the guardian actor for all user-created top-level actors; actors created using ActorSystem.actorOf are found below this one.
* "/system" is the guardian actor for all system-created top-level actors, e.g. logging listeners or actors automatically deployed by configuration at the start of the actor system.
* "/deadLetters" is the dead letter actor, which is where all messages sent to stopped or non-existing actors are re-routed (on a best-effort basis: messages may be lost even within the local JVM).
* "/temp" is the guardian for all short-lived system-created actors, e.g. those which are used in the implementation of ActorRef.ask.
* "/remote" is an artificial path below which all actors reside whose supervisors are remote actor references
The need to structure the name space for actors like this arises from a central and very simple design goal: everything in the hierarchy is an actor, and all actors function in the same way.

## Actor lifecycle

It is important to note that actors do not stop automatically when no longer referenced, every Actor that is created must also explicitly be destroyed. The only simplification is that stopping a parent Actor will also recursively stop all the child Actors that this parent has created. All actors are also stopped automatically when the ActorSystem is shut down.

## Creating Actor

Creating Actors
An actor can create, or spawn, an arbitrary number of child actors, which in turn can spawn children of their own, thus forming an actor hierarchy. ActorSystem hosts the hierarchy and there can be only one root actor, an actor at the top of the hierarchy of the ActorSystem. The lifecycle of a child actor is tied to the parent – a child can stop itself or be stopped at any time but it can never outlive its parent


### The ActorContext
The ActorContext can be accessed for many purposes such as:

Spawning child actors and supervision
Watching other actors to receive a Terminated(otherActor) event should the watched actor stop permanently
Logging
Creating message adapters
Request-response interactions (ask) with another actor
Access to the self ActorRef


### The Guardian Actor
The top level actor, also called the user guardian actor, is created along with the <b> ActorSystem </b>. Messages sent to the actor system are directed to the root actor.

### SpawnProtocol
The guardian actor should be responsible for initialization of tasks and create the initial actors of the application, but sometimes you might want to spawn new actors from the outside of the guardian actor. For example creating one actor per HTTP request.

That is not difficult to implement in your behavior, but since this is a common pattern there is a predefined message protocol and implementation of a behavior for this. It can be used as the guardian actor of the ActorSystem, possibly combined with Behaviors.setup to start some initial tasks or actors. Child actors can then be started from the outside by telling or asking SpawnProtocol.Spawn to the actor reference of the system. Using ask is similar to how ActorSystem.actorOf can be used in classic actors with the difference that a Future of the ActorRef is returned.


### Interaction Patterns

## Introduction

Interacting with an Actor in Akka is done through an ActorRef[T] where T is the type of messages the actor accepts, also known as the “protocol”. This ensures that only the right kind of messages can be sent to an actor and also that no one else but the Actor itself can access the Actor instance internals.

### Interaction Patterns

Interacting with an Actor in Akka is done through an ActorRef[T] where T is the type of messages the actor accepts, also known as the “protocol”. This ensures that only the right kind of messages can be sent to an actor and also that no one else but the Actor itself can access the Actor instance internals.

Message exchange with Actors follow a few common patterns, let’s go through each one of them

#### Fire and Forget
The fundamental way to interact with an actor is through “tell”, which is so common that it has a special symbolic method name: actorRef ! message. Sending a message with tell can safely be done from any thread.

Tell is asynchronous which means that the method returns right away. After the statement is executed there is no guarantee that the message has been processed by the recipient yet. It also means there is no way to know if the message was received, the processing succeeded or failed.

``` object Printer {

  case class PrintMe(message: String)

  def apply(): Behavior[PrintMe] =
    Behaviors.receive {
      case (context, PrintMe(message)) =>
        context.log.info(message)
        Behaviors.same
    }
}

val system = ActorSystem(Printer(), "fire-and-forget-sample")

// note how the system is also the top level actor ref
val printer: ActorRef[Printer.PrintMe] = system

// these are all fire and forget
printer ! Printer.PrintMe("message 1")
printer ! Printer.PrintMe("not message 2") 

```

##### Useful when:

It is not critical to be sure that the message was processed
There is no way to act on non successful delivery or processing
We want to minimize the number of messages created to get higher throughput (sending a response would require creating twice the number of messages)
Problems:

If the inflow of messages is higher than the actor can process the inbox will fill up and can in the worst case cause the JVM crash with an OutOfMemoryError
If the message gets lost, the sender will not know

##### Problems:

If the inflow of messages is higher than the actor can process the inbox will fill up and can in the worst case cause the JVM crash with an OutOfMemoryError
If the message gets lost, the sender will not know





